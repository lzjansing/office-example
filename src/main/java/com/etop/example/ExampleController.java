package com.etop.example;

import com.etop.jansing.swopi.utils.HttpClientUtil;
import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
        return absoPath.substring(absoPath.indexOf("/upload"));
    }

    public static String getFilePath(String i) {
        return getFilePath(i, null);
    }

//    private String convertServer = "http://127.0.0.1:8098/libre";
//    private String servletPath = "/view";

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(String fileId, HttpServletResponse resp, HttpServletRequest req) throws Exception {
        String convertServletPath = req.getParameter("convertServletPath");
        String callbackAddr = HttpClientUtil.getLocalServerPath(req);
        Map<String, String> params = Maps.newHashMap();
        params.put("fileId", fileId);
        params.put("host", callbackAddr);
        params.put("fileExt", FilenameUtils.getExtension(getFilePath(fileId)));
        return "redirect:" + convertServletPath + HttpClientUtil.encodeParams(params);
    }
}
