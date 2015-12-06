package com.teradata.ec.component.hadooptools.util;

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

import java.io.IOException;

/**
 * Created by Administrator on 2015/12/6.
 */
public class ElasticsearchMr extends Configured implements Tool {
    public class MyMapper extends MapReduceBase implements Mapper {
        @Override
        public void map(Object key, Object value, OutputCollector output,
                        Reporter reporter) throws IOException {
            // assuming the document is a String called 'source'
            String source = "...";
            Text jsonDoc = new Text(source);
            // send the doc directly
            output.collect(NullWritable.get(), jsonDoc);
        }
    }

    @Override
    public int run(String[] arg0) throws Exception {
        JobConf conf = new JobConf();
        conf.set("es.input.json", "yes");
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);
        conf.setMapperClass(MyMapper.class);
        Job job = Job.getInstance(conf);

        FileInputFormat.setInputPaths(job, new Path(arg0[0]));
        FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
        //JobClient.runJob(conf);
        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new ElasticsearchMr(), args);
        System.exit(ret);
    }
}
