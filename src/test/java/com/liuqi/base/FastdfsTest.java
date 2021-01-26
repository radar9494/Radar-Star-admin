package com.liuqi.base;

import com.liuqi.fastdfs.FastDFSClientUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

public class FastdfsTest extends BaseTest {

    @Autowired
    private FastDFSClientUtils fastDFSClientUtils;

    @Test
    public void test01(){
        try {
            String s=fastDFSClientUtils.uploadFile(new File("E:/1.jpg"));

            InputStream is=fastDFSClientUtils.downFile(s);
            OutputStream os=new FileOutputStream("E:/2.jpg");
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
