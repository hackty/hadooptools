package com.teradata.ec.component.hadooptools.util;

import java.io.*;

/**
 * Created by Administrator on 2015/11/17.
 */
public class FileCrawler {
    private static boolean flag = false;
    private static String destPre1 = null;
    private static String destPre2 = null;

    /**
     * 复制一个目录及其子目录下所有文件到另外一个目录
     * @param strSrc
     * @param strDest
     * @throws IOException
     */
    public static void copyFiles(String strSrc, String strDest, boolean needDelete) throws IOException {
        destPre1 = destPre2;
        if(!flag) {
            destPre1 = strDest;
            destPre2 = strDest + "/";//保留最初的路径
            flag = true;//标志
        }
        File src = new File(strSrc+"/");
        File dest = new File(strDest);
        if (src.isDirectory()) {
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                //File destFile = new File(dest, file);
                if(!srcFile.isDirectory()) {
                    destPre1 += srcFile.getName();//根据实际文件名创建目录
                }
                copyFiles(srcFile.getPath(), destPre1, needDelete);// 递归复制
            }
        } else {
            System.out.println(src + "  " + dest);

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

            if(needDelete) {//判断是否需要删除源文件
                src.delete();
            }
        }
    }

    public static void main(String[] args) {
        try {
            copyFiles(args[0], args[1], Boolean.parseBoolean(args[2]));
            //copyFiles("C:/data_src/", "C:/data_now/", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
