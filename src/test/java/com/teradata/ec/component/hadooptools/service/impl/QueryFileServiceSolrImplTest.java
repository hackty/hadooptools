package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.model.FileModel;
import com.teradata.ec.component.hadooptools.service.IQueryFileService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


/**
 * Created by Administrator on 2015/11/7.
 */
public class QueryFileServiceSolrImplTest {

    private IQueryFileService qfs;

    @Before
    public void setUp(){
        qfs = new QueryFileServiceSolrImpl();
    }

    @Test
    public void testQueryFiles() {
        String wd = "数据";
        List<FileModel> models = qfs.queryFiles("content_text:" + wd);
        for (Object obj : models) {
            FileModel fm = (FileModel)obj;
            System.out.println(fm.getName());
            System.out.println(fm.getHighlightContent());
        }
    }
}