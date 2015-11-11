package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.service.ICopyFileService;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class CopyFileServiceImpl implements ICopyFileService{

    private boolean flag = false;
    private String destPre1 = null;
    private String destPre2 = null;
    private String realName = null;

    /**
     * 复制一个目录及其子目录下所有文件到另外一个目录
     * @param strSrc
     * @param strDest
     * @throws IOException
     */
    @Override
    public void copyFiles(String strSrc, String strDest) throws IOException {
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

}
