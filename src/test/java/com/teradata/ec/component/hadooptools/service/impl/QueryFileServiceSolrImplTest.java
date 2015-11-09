package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.model.FileModel;
import com.teradata.ec.component.hadooptools.model.FileTypeModel;
import com.teradata.ec.component.hadooptools.service.IQueryFileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by Administrator on 2015/11/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/hadooptools-spring-config.xml")
public class QueryFileServiceSolrImplTest {

    private IQueryFileService qfs;

    @Before
    public void setUp(){
        qfs = new QueryFileServiceSolrImpl();
    }

    @Test
    public void testQueryFiles() {
        String wd = "工作量";
        String type = null;
//        String type = "txt";
        List<FileModel> models = qfs.queryFiles("content_text:" + wd, type, 0, 10);
        for (Object obj : models) {
            FileModel fm = (FileModel)obj;
            System.out.println(fm.getName());
//            System.out.println(fm.getHighlightName());
//            System.out.println(fm.getHighlightContent());
            System.out.println("----------------------------------------------------");
        }
    }

    @Test
    public void testQueryFileTypes() {
        String wd = "*";
        List<FileTypeModel> models = qfs.queryFileTypes("content_text:" + wd);
        for (Object obj : models) {
            FileTypeModel fm = (FileTypeModel)obj;
            System.out.println(fm.getTypeName());
            System.out.println(fm.getTypeCount());
            System.out.println("----------------------------------------------------");
        }
    }


    @Test
    public void testQueryAll() {
        String wd = "出差";
        List<FileModel> f = qfs.queryFiles("content_text:" + wd, "");
        List<FileTypeModel> ft = qfs.queryFileTypes("content_text:" + wd);

    }

}