package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.model.FileModel;
import com.teradata.ec.component.hadooptools.model.FileTypeModel;
import com.teradata.ec.component.hadooptools.model.PageModel;
import com.teradata.ec.component.hadooptools.service.IQueryFileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/11/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/hadooptools-spring-config.xml")
public class QueryFileServiceSolrImplTest {

    private IQueryFileService qfs;

    @Before
    public void setUp(){
        qfs = new QueryFileServiceSolrMRImpl();
    }

    @Test
    public void testQueryFiles() {
        String wd = "工作";
        String type = null;
        String role = null;
//        String type = "doc";
//        String role = "dudu";
        Map filterQuery = new HashMap();
        filterQuery.put("type",type);
        filterQuery.put("role",role);
        PageModel pageModel = qfs.queryFiles(wd, filterQuery, 1, 8);
        List<FileModel> fileModel = pageModel.getDatas();
        System.out.println("Count: " + pageModel.getCount());
        System.out.println("Totalpages: " + pageModel.getTotalPages());
        for (Object obj : fileModel) {
            FileModel fm = (FileModel)obj;
            System.out.println(fm.getName());
//            System.out.println(fm.getModifyTime());
            System.out.println(fm.getHighlightName());
            System.out.println(fm.getHighlightContent());
            System.out.println("----------------------------------------------------");
        }
    }

    @Test
    public void testQueryFileTypes() {
        String wd = "*";
        Map filterQuery = new HashMap();
//        filterQuery.put("role",null);
        filterQuery.put("role","dudu");
        List<FileTypeModel> models = qfs.queryFileTypes(wd, filterQuery);
        for (Object obj : models) {
            FileTypeModel fm = (FileTypeModel)obj;
            System.out.println(fm.getTypeName());
            System.out.println(fm.getTypeCount());
            System.out.println("----------------------------------------------------");
        }
    }

    @Test
    public void testGetFileTypeName() {
        QueryFileServiceSolrMRImpl qfssi = new QueryFileServiceSolrMRImpl();
        String str = qfssi.getFileTypeName("text/plain*");
        String str2 = qfssi.getFileTypeName("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        System.out.println(str);
        System.out.println(str2);
    }

}