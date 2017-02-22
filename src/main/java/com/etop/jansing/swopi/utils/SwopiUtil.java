package com.etop.jansing.swopi.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sealter on 15-7-10.
 */
public class SwopiUtil {

    public static final String OS_LINUX = "linux";
    public static final String OS_WIN = "win";
    private static final String DATE_FORMAT_STRING = "yyyy-MM-ddHH:mm:ss";
    public static final String ENCODE_CHARACTERSET = "UTF-8"; // 编码字符集
    public static final long TIME_OUT = 5 * 60 * 1000;
    public static final String TOKEN_SEPERITOR = "_";


    public static final String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        return dateFormat.format(date);
    }

    public static final Date getDate(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        Date date = null;
        try {
            date = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 编码URL格式的字符串
     *
     * @param str
     * @return
     */
    public static final String encodeStr(String str) {

        try {
            str = URLEncoder.encode(str, ENCODE_CHARACTERSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static final String decodeStr(String str) {


        try {
            str = URLDecoder.decode(str, ENCODE_CHARACTERSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }


    /**
     * 获取文件内容的SHA256值
     * <p>
     * TODO: 编码待完善
     *
     * @param inputStream
     * @return
     */
    public static final String getFileSHA256(InputStream inputStream) {

        String sha256 = "";
        try {
            byte[] bytes = DigestUtils.sha256(inputStream);
            sha256 = Base64Utils.encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sha256;

    }

    /**
     * 获取文件大小，给OWA的也是int类型
     *
     * @param inputStream
     * @return
     */
    public static final int getFileSize(InputStream inputStream) {

        int size = 0;

        try {
            size = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }


}
