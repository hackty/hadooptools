package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.service.IHdfsService;
import org.apache.hadoop.fs.FSDataInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by taoyang on 11/11/15.
 */
public class HdfsServiceImplTest {

    private IHdfsService hs;

    @Before
    public void setUp() throws Exception {
        hs = new HdfsServiceImpl();
    }

    @Test
    public void testGetHdfsFile() throws Exception {
        FSDataInputStream fsDataInputStream = hs.getHdfsFile("hdfs://master/destination/2015-11-10/保密制度.doc*");
    }

    @Test
    public void testGetRealPath() throws Exception {
        System.out.println(hs.getRealPath("hdfs://master/destination/2015-11-10/保密制度.doc.*"));
    }
}