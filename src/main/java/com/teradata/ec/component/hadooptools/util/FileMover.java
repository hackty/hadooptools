package com.teradata.ec.component.hadooptools.util;

import java.io.*;

/**
 * Created by taoyang on 11/13/15.
 */
public class FileMover {


    private static boolean flag = false;
    private static String destPre1 = null;
    private static String destPre2 = null;
    private static String realName = null;

    /**
     * 复制一个目录及其子目录下所有文件到另外一个目录
     * @param strSrc
     * @param strDest
     * @throws IOException
     */
    public static void copyFiles(String strSrc, String strDest) throws IOException {
        destPre1 = destPre2;
        if(!flag) {
            destPre1 = strDest;
            destPre2 = strDest + "/";
            flag = true;
        }
        File src = new File(strSrc+"/");
        File dest = new File(strDest);
        if (src.isDirectory()) {
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                //File destFile = new File(dest, file);
                if(!srcFile.isDirectory()) {
                    destPre1 += srcFile.getName();
                    realName = destPre1;
                    destPre1 += ".tour";//加上一个新的后缀
                }
                copyFiles(srcFile.getPath(), destPre1);// 递归复制
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
            dest.renameTo(new File(realName));
        }
    }


    public static void main(String[] args) {
        try {
            copyFiles(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
