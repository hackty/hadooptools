package com.teradata.ec.component.hadooptools.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import org.elasticsearch.hadoop.mr.EsOutputFormat;

import java.io.*;

/**
 * Created by Administrator on 2015/12/6.
 */
public class ElasticsearchMr {

    private static String jsonFile = ""; //生成的json文件内容

    public class EsMapper extends MapReduceBase implements Mapper {
        @Override
        public void map(Object key, Object value, OutputCollector output,
                        Reporter reporter) throws IOException {
            // assuming the document is a String called 'source'
            //String source = "...";
            Text jsonDoc = new Text(jsonFile);
            // send the doc directly
            output.collect(NullWritable.get(), jsonDoc);
        }
    }

    /**
     * 执行MrJob，elasticsearch自动索引
     * @throws IOException
     */
    public static void runMrJob () throws IOException {
        JobConf conf = new JobConf();
        conf.set("es.nodes", "192.168.13.134:9200");//设置es地址
        conf.set("es.resource", "docindex/attachment");//设置index位置
        conf.set("es.mapping.id", "file");//设置mapping的id
        conf.set("es.input.json", "yes");//设置json输入格式
        conf.setOutputFormat(EsOutputFormat.class);//设置输出格式
        conf.setMapOutputValueClass(Text.class);//设置输出value格式
        conf.setMapperClass(EsMapper.class);//设置MapperClass

        JobClient.runJob(conf);//执行job，将json文件写入elasticsearch，elasticsearch会自动建索引
    }

    /**
     * 根据local/hdfs文件获得其base64编码
     * @param filePath
     * @param location
     * @throws Exception
     */
    public static String getFileByteString(String filePath, String location) throws Exception{
        Base64 b64 = new Base64();
        byte[] buffer;
        if(location.equals("local")) { //将本地文件转换成base64编码
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();
        } else {//将hdfs文件转换成base64编码
            FileSystem fs = FileSystem.get(new Configuration());
            FSDataInputStream inputStream = fs.open(new Path(filePath));
            buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
        }
        return b64.encodeToString(buffer);
    }

    /**
     * 根据本地文件目录将下面所有文件转换成json格式并执行MrJob
     * @param localDir
     * @throws Exception
     */
    public static void getLocalJsonAndRunJob(String localDir) throws Exception {
        File file = new File(localDir);
        File[] array = file.listFiles();//列出所有目录和文件

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                jsonFile = "";//拼接json文件内容
                jsonFile += "{\"file\":\"";
                jsonFile += getFileByteString(array[i].getPath(), "local");
                jsonFile += "\"}";
                runMrJob();

                System.out.println("^^^^^" + array[i].getName());//测试输出
                System.out.println("#####" + array[i]);
                System.out.println("*****" + array[i].getPath());

            }else if(array[i].isDirectory()){
                getLocalJsonAndRunJob(array[i].getPath());
            }
        }
    }

    /**
     * 根据本地文件目录将下面所有文件转换成json格式并执行MrJob
     * @param hdfsDir
     * @throws Exception
     */
    public static void getHdfsJsonAndRunJob(String hdfsDir) throws Exception {

        // 遍历目录下的所有文件
        try {
            FileSystem fs = FileSystem.get(new Configuration());
            FileStatus[] status = fs.listStatus(new Path(hdfsDir));
            for (FileStatus file : status) {
                jsonFile = "";//拼接json文件内容
                jsonFile += "{\"file\":\"";
                jsonFile += getFileByteString(file.getPath().getName(), "hdfs");
                jsonFile += "\"}";
                runMrJob();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入参数为文件夹目录，本地目录文件夹下可以包含有其它文件夹目录
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        getLocalJsonAndRunJob(args[0]);//本地路径
        //getHdfsJsonAndRunJob(args[0]);//若文件存在hdfs，可以使用hdfs文件路径
    }
}
