package com.teradata.ec.component.hadooptools.util;

/**
 * Created by Administrator on 2015/12/5.
 */
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class AvgSorceForTestHadoop extends Configured implements Tool {
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while(tokenizer.hasMoreElements()){
                String strName = tokenizer.nextToken();
                String strSorce = tokenizer.nextToken();
                System.out.println("strName: " +strName + "  strScore: " + strSorce);
                context.write(new Text(strName), new IntWritable(Integer.parseInt(strSorce)));
            }
        }
    }
    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            int num = 0;
            for (IntWritable sorce : values) {
                sum+=sorce.get();
                num++;
            }
            System.out.println("key: " + key + " avg:" + sum/num);
            context.write(key, new IntWritable((int)(sum/num)));
        }
    }
    @Override
    public int run(String[] arg0) throws Exception {
        Job job = new Job(getConf());
        job.setJobName("AvgSorceForTestHadoop");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(Map.class);
//        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.setInputPaths(job, new Path(arg0[0]));
        FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }
    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new AvgSorceForTestHadoop(), args);
        System.exit(ret);
    }
}