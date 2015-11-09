package com.teradata.ec.component.hadooptools.service.impl;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;




import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/5.
 */

class RegexExludePathFilter implements PathFilter {                                                //文件过滤类
    private final String regex;

    public RegexExludePathFilter(String regex) {
        this.regex = regex;
    }
    public boolean accept(Path path) {
        return !path.toString().matches(regex);
    }
}

public class ReadHdfs {
    public static void main(String[] args) throws IOException {
        getFirstHdfsFile("hdfs://master/destination/2015-10-16/*全文*");
    }

    public static void getFirstHdfsFile(String hdfsPath) throws IOException {
        //String local = System.getProperty("user.dir");
        //System.out.println(local);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);

        FileStatus[] status = fs.globStatus(new Path(hdfsPath),new RegexExludePathFilter("^.*/1901"));   //根据通配符匹配文件
        Path[] listedPaths = FileUtil.stat2Paths(status);                                                //获取相匹配文件的Path
        if(listedPaths.length > 0) {
            FSDataInputStream fsdi = fs.open(listedPaths[0]);                                            //生产FSDataInputStream流
            //OutputStream output = new FileOutputStream("C:/File/" + listedPaths[0].toString().substring(listedPaths[0].toString().lastIndexOf("/"),listedPaths[0].toString().length()));
            //IOUtils.copyBytes(fsdi,output,4096,true);

            System.out.println(listedPaths[0]);
            fsdi.close();                                                                                //关闭流
        }
    }
}
