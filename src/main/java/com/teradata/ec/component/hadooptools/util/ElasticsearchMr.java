package com.teradata.ec.component.hadooptools.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.elasticsearch.hadoop.mr.EsOutputFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        conf.set("es.input.json", "yes");//设置json输入格式
        conf.setOutputFormat(EsOutputFormat.class);//设置输出格式
        conf.setMapOutputKeyClass(Text.class);//设置输出key格式
        conf.setMapOutputValueClass(Text.class);//设置输出value格式
        conf.setMapperClass(EsMapper.class);//设置MapperClass

        JobClient.runJob(conf);//执行job
    }

    /**
     * 根据文件获得其base64编码
     * @param file
     * @throws Exception
     */
    public static String getFileByteString(File file) throws Exception{
        Base64 b64 = new Base64();
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        fis.read(buffer);
        fis.close();

        return b64.encodeToString(buffer);
    }

    /**
     * 根据文件目录将下面所有文件转换成json格式并执行MrJob
     * @param fileDir
     * @throws Exception
     */
    public static void getJsonAndRunJob(String fileDir) throws Exception {
        File file = new File(fileDir);
        File[] array = file.listFiles();//列出所有目录和文件

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                jsonFile = "";//拼接json文件内容
                jsonFile += "{\"file\":\"";
                jsonFile += getFileByteString(array[i]);
                jsonFile += "\"}";
                runMrJob();

                System.out.println("^^^^^" + array[i].getName());//测试输出
                System.out.println("#####" + array[i]);
                System.out.println("*****" + array[i].getPath());

            }else if(array[i].isDirectory()){
                getJsonAndRunJob(array[i].getPath());
            }
        }
    }

    /**
     * 输入参数为文件夹目录，文件夹下可以包含有其它文件夹目录
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        getJsonAndRunJob(args[0]);
    }
}
