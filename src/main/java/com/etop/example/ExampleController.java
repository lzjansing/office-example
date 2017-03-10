package com.etop.example;

import com.etop.jansing.swopi.utils.SwopiUtil;
import com.google.common.collect.Maps;
import com.jansing.common.config.Global;
import com.jansing.common.utils.Message;
import com.jansing.web.utils.FileTransmitUtil;
import com.jansing.web.utils.HttpClientUtil;
import com.jansing.web.utils.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jansing on 16-12-21.
 */
@Controller
public class ExampleController {
    private static List<String> fileList = null;

    private static void initFileList(HttpServletRequest req) {
        fileList = new ArrayList<>(32);
        File file = new File(req.getSession().getServletContext().getRealPath("/upload"));
        fileList.add(file.getAbsolutePath());
        List<File> tmpList = new ArrayList<>(32);
        Collections.addAll(tmpList, file.listFiles());
        while (!tmpList.isEmpty()) {
            File tmp = tmpList.remove(tmpList.size() - 1);
            fileList.add(tmp.getAbsolutePath());
            if (tmp.isDirectory()) {
                Collections.addAll(tmpList, tmp.listFiles());
            }
        }
    }

    @RequestMapping(value = {"", "/", "/files"}, method = RequestMethod.GET)
    public String getFileList(Model model, HttpServletRequest req) {
        if (fileList == null) {
            initFileList(req);
        }
        if (model != null) {
            model.addAttribute("fileList", fileList);
        }
        return "/files";
    }

    public static String getFilePath(String i, HttpServletRequest req) {
        if (req != null && fileList == null) {
            initFileList(req);
        }
        String absoPath = fileList.get(Integer.parseInt(i));
        return absoPath;
    }

    public static String getFilePath(String i) {
        return getFilePath(i, null);
    }

    public static String getFileRelativePath(String i, HttpServletRequest req) {
        String absoPath = getFilePath(i, req);
        return absoPath.substring(absoPath.indexOf("/upload"));
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(String fileId, HttpServletResponse resp, HttpServletRequest req) throws Exception {
        String convertServletPath = req.getParameter("convertServletPath");
        String callbackAddr = HttpClientUtil.getLocalServerPath(req);
        Map<String, String> params = Maps.newHashMap();
        params.put("fileId", fileId);
        params.put("host", callbackAddr);
        String fileExt = FilenameUtils.getExtension(getFilePath(fileId, req));
        params.put("fileExt", fileExt);

        String os = req.getParameter("os");
        if (os != null) {
            params.put("os", os);
        } else if (fileExt.endsWith("xls") || fileExt.equals("xlsx")) {
            params.put("os", SwopiUtil.OS_WIN);
        }
        return "redirect:" + convertServletPath + HttpClientUtil.encodeParams(params);
    }


    /**
     * 单文件ajax上传
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadAjax", method = RequestMethod.POST)
    public Message uploadAjax(@RequestParam CommonsMultipartFile file, HttpServletRequest req) throws IOException {
        Message message = new Message();
        try {
            String filePath = HttpClientUtil.getLocalServerPath(req) + FileTransmitUtil.upload(file, req, Global.getConfig("uploadPath"));
            message.setCode(Message.SUCCESS);
            message.setMessage("上传成功");
            message.getExtra().put(FileTransmitUtil.PATH_KEY, filePath);
            //html的id，用以回显
            String id = req.getParameter("id");
            if (StringUtil.isNotBlank(id)) {
                message.getExtra().put("id", id);
            }
        } catch (Exception e) {
            message.setCode(Message.FAIL);
            message.setMessage("上传失败，" + e.getMessage());
        }
        return message;

    }

    @ResponseBody
    @RequestMapping(value = "/askExist", method = {RequestMethod.GET, RequestMethod.POST})
    public Message exist(@RequestParam String filePath, HttpServletRequest req) {
        Message message = new Message();
        if (StringUtil.isNotBlank(filePath)) {
            if (new File(FileTransmitUtil.getRealPathFromUrl(req, filePath)).exists()) {
                message.setCode(Message.SUCCESS);
                message.setMessage("文件存在");
            } else {
                message.setCode(Message.FAIL);
                message.setMessage("文件不存在");
            }
        } else {
            message.setCode(Message.FAIL);
            message.setMessage("参数不正确：filePath=" + filePath);
        }
        return message;
    }
}
