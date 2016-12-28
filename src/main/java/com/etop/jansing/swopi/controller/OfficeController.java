package com.etop.jansing.swopi.controller;

import com.etop.example.ExampleController;
import com.etop.jansing.swopi.utils.HttpClientUtil;
import com.etop.jansing.swopi.utils.JsonMapper;
import com.etop.jansing.swopi.utils.SwopiUtil;
import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * todo 需要维护一张file表，
 * 能够通过fileId找到文件存放位置，最好能存储一些文件信息；
 * 完成后再修改代码，去掉getFilePath方法
 * todo 限制某些ip才能访问（应该只有ConvertServer才能访问）
 * Created by jansing on 16-12-21.
 */
@Controller
@RequestMapping("/wopi/files")
public class OfficeController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 以下参数是wopi所需
     * map.put("BaseFileName", file.getName());
     * map.put("OwnerId", "jansing");
     * map.put("Version", "1.0");
     * map.put("Size", SwopiUtil.getFileSize(in));
     * map.put("SHA256", SwopiUtil.getFileSHA256(in));
     *
     * @param fileId
     * @param resp
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    @RequestMapping("/{fileId}")
    public void fileInfo(@PathVariable String fileId, HttpSession session, HttpServletResponse resp) throws IOException {
        Map<String, Object> map = new HashMap<>();

        InputStream in = null;
        try {
            String realPath = session.getServletContext().getRealPath(ExampleController.getFilePath(fileId));
            File file = new File(realPath);
            in = new FileInputStream(file);
            map.put("BaseFileName", file.getName());
            map.put("OwnerId", "jansing");
            map.put("Version", "1.0");
            map.put("Size", SwopiUtil.getFileSize(in));
            map.put("SHA256", SwopiUtil.getFileSHA256(in));
            resp.setStatus(HttpServletResponse.SC_OK);
//            return map;
        } catch (FileNotFoundException e) {
            logger.error("convert server请求文件时文件不存在！");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        PrintWriter writer = resp.getWriter();
        writer.println(JsonMapper.getInstance().toJson(map));
        resp.flushBuffer();
    }

    @RequestMapping("/{fileId}/contents")
    public void fileInputStream(@PathVariable String fileId, HttpSession session, HttpServletResponse resp) {
        InputStream in = null;
        try {
            String realPath = session.getServletContext().getRealPath(ExampleController.getFilePath(fileId));
            in = new FileInputStream(new File(realPath));
            resp.setContentType("application/octet-stream");
            org.apache.commons.io.IOUtils.copy(in, resp.getOutputStream());
            resp.flushBuffer();
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (FileNotFoundException e) {
            logger.error("convert server请求文件时文件不存在！");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            logger.error("链接异常，请重试！");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                ;
            }
        }
    }


    /**
     * 如果外网可以访问wopi服务器，并且允许暴露wopi服务器地址给浏览器，则可以访问这个方法。注意此时
     * <p>
     * 假设访问路径
     * http://localhost:8080/image/login?username=aaa&password=ttt
     * 则：
     * getContextPath   /image
     * getServletPath   /login
     * getRequestURI    /image/login
     * getRequestURL    http://localhost:8080/image/login
     * getQueryString   username=aaa&password=ttt
     *
     * @return
     */
    @RequestMapping("/view")
    public String view(HttpServletRequest req) {
//        String redirectPath = "http://officewebapps.etop.com/wv/wordviewerframe.aspx?WOPISrc=http%3A%2F%2F192.168.1.106%3A8080%2FtestWOPI-1.0-SNAPSHOT%2Fwopi%2Ffiles%2F"+fileId;
        String fileId = req.getParameter("fileId");
        String owaServerPath = req.getParameter("owaServerPath");
        return "redirect:" + owaServerPath + getOwaUrl(fileId,
                FilenameUtils.getExtension(ExampleController.getFilePath(fileId, req)),
                HttpClientUtil.getLocalServerPath(req));
    }


    //    private static String owaServerPath = "http://officewebapps.etop.com";
    private static String callbackServletPath = "/wopi/files";

    private static String getOwaUrl(String fileId, String fileExt, String curContextPath) {
        String fileInfoServletPath = curContextPath + callbackServletPath + "/" + fileId;

        Map<String, String> params = Maps.newHashMap();
        params.put("WOPISrc", fileInfoServletPath);

        String url = "";
//        String url = owaServerPath;
        if (StringUtils.isBlank(fileExt)) {
            throw new IllegalArgumentException("文件格式为空！");
        }
        switch (fileExt) {
            case "doc":
            case "docx":
                url += "/wv/wordviewerframe.aspx";
                break;
            case "xls":
            case "xlsx":
                url += "/x/_layouts/xlviewerinternal.aspx";
                break;
            case "pdf":
                params.put("PdfMode", "1");
                url += "/wv/wordviewerframe.aspx";
                break;
            case "ppt":
            case "pptx":
                params.put("PowerPointView", "ReadingView");
                url += "/p/PowerPointFrame.aspx";
                break;
            default:
                throw new IllegalArgumentException("不支持该种文件格式！");
        }
        return url + HttpClientUtil.encodeParams(params);
    }
}
