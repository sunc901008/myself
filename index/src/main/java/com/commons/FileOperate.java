package com.commons;

import java.io.*;

/**
 * creator: sunc
 * date: 2017/4/17
 * description:
 */
public class FileOperate {

    public static byte[] toByteArray(String filename) {
        File f = new File(filename);
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            in.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    // 一次性读取文件所有内容
    public static String readToString(String filename) {
        File file = new File(filename);
        Long length = file.length();     //获取文件长度
        byte[] content = new byte[length.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(content);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(content);
    }

}
