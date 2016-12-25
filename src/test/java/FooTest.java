import com.etop.example.ExampleController;
import com.etop.jansing.swopi.utils.SwopiUtil;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by jansing on 16-12-21.
 */
public class FooTest {


    @Test
    public void test01(){
        String link = "http://officewebapps.etop.com/wv/wordviewerframe.aspx?WOPISrc=http%3A%2F%2F192.168.1.106%3A8080%2FtestWOPI-1.0-SNAPSHOT%2Fwopi%2Ffiles%2Fdoc";
        System.out.println(SwopiUtil.encodeStr(link));
        System.out.println(SwopiUtil.decodeStr(link));
    }


    /**
     *
     * 假设访问路径
     * http://localhost:8080/image/login?username=aaa&password=ttt
     * 则：
     * getContextPath   /image
     * getServletPath   /login
     * getRequestURI    /image/login
     * getRequestURL    http://localhost:8080/image/login
     * getQueryString   username=aaa&password=ttt
     */
    @Test
    public void test02(){
        String base = "http://localhost:8080/image/login?username=aaa&password=ttt";
        String dis = "/login";
        String target = "http://localhost:8080/image";
        System.out.println(base.indexOf(dis));
        System.out.println(base.substring(0, base.indexOf(dis)));
        System.out.println(target.equals(base.substring(0, base.indexOf(dis))));
    }

    @Test
    public void test04(){
        String uri = "http://localhost:8098/libre/linuxView/null?host=http%3A%2F%2F0%3A0%3A0%3A0%3A0%3A0%3A0%3A1%3A8089%2F";
        System.out.println(SwopiUtil.decodeStr(uri));
    }

    @Test
    public void test03() throws FileNotFoundException {
        String path = "/home/jansing/learn/java/swopi/target/swopi/upload/doc/1.docx";
        System.out.println(path.substring(path.indexOf("/upload")+"/upload".length()));

    }

    @Test
    public void test05(){
        String fileId = "3";
        System.out.println(FilenameUtils.getExtension(ExampleController.getFilePath(fileId)));
    }

    @Test
    public void test06(){
        String fileExt = null;
        switch (fileExt){
            case "doc":
                System.out.println("doc");
                break;
            case "xls":
                System.out.println("xls");
                break;
            case "pdf":
                System.out.println("pdf");
                break;
            case "ppt":
                System.out.println("ppt");
                break;
            default:
                System.out.println("not support");
        }
    }


}
