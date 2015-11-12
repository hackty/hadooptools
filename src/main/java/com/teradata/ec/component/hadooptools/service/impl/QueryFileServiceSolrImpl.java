package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.model.FileModel;
import com.teradata.ec.component.hadooptools.model.FileTypeModel;
import com.teradata.ec.component.hadooptools.model.PageModel;
import com.teradata.ec.component.hadooptools.service.IQueryFileService;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/11/7.
 */
@Service
public class QueryFileServiceSolrImpl implements IQueryFileService {

    private Logger log = Logger.getLogger(QueryFileServiceSolrImpl.class);
    private CloudSolrServer cloudSolrServer;

    String  defaultCollection ="collection_nrt"; //选择collection
    int  zkClientTimeout = 20000;
    int zkConnectTimeout = 1000;

    /**
     * 分页查询，包含查询，分页，高亮及获取高亮处摘要等内容；不同于数据库的查询分页，
     * solr的查询返回值中有文档总数，所以无需再次查询总条数。
     *
     * @param solrServer, page 自定义的翻页对象，包含查询信息及当前页数据列表。
     * @return List<FileModel>
     */
    public PageModel getSolrQuery(SolrServer solrServer, PageModel pageModel, String type) {
        if(type == null || "".equals(type)) {
            type = "*";
        }

        SolrQuery query = new SolrQuery();

        // 获取查询参数
        String para = pageModel.getParameter();

        System.out.println("para: " + para + ";  type: " + type);

        query.setQuery("content_text:" + para);
        query.setFilterQueries(getContentType(type));//过滤文件类型
        query.addSort("upload_time", SolrQuery.ORDER.desc);
        query.setStart((int)pageModel.getStart());
        query.setRows(pageModel.getSize());

//        System.out.println(page.getStart() + "  " + page.getSize());

        //设置高亮
        query.setHighlight(true);// 开启高亮组件
        query.addHighlightField("resource_name");// 高亮字段
        query.addHighlightField("content_text");// 高亮字段
        query.setHighlightSimplePre("<mark>");//标记，高亮关键字前缀
        query.setHighlightSimplePost("</mark>");//后缀
        query.setHighlight(true).setHighlightSnippets(2); //获取高亮分片数，一般搜索词可能分布在文章中的不同位置，其所在一定长度的语句即为一个片段，默认为1，但根据业务需要有时候需要多取出几个分片。 - 此处设置决定下文中titleList, contentList中元素的个数
        query.setHighlightFragsize(150);//每个分片的最大长度，默认为100。适当设置此值，如果太小，高亮的标题可能会显不全；设置太大，摘要可能会太长。

        return getPageModel(solrServer, pageModel, query);
    }

    /**
     * 根据SolrServer与SolrQuery查询并获取FileModel
     *
     * @param solrServer, query。
     * @return List<FileModel>
     */
    public PageModel getPageModel(SolrServer solrServer, PageModel pageModel, SolrQuery query) {
        List<FileModel> fileModels = new ArrayList<FileModel>();
        try {
            QueryResponse rsp = solrServer.query(query);
            SolrDocumentList docs = rsp.getResults();

            System.out.println("docs num:" + docs.getNumFound());


            Map<String,Map<String,List<String>>> highlightMap=rsp.getHighlighting(); //获取所有高亮的字段

            Iterator<SolrDocument> iter = docs.iterator();
            while (iter.hasNext()) {
                SolrDocument doc = iter.next();

                System.out.println("resource_name: " + doc.getFieldValue("resource_name").toString());


                String type = getFileTypeName(doc.getFieldValue("content_type").toString());
                String id = doc.getFieldValue("id").toString();
                String name = doc.getFieldValue("resource_name").toString();
                String modifyTime = null;
                if(doc.getFieldValue("last_modified") == null) {
                    modifyTime = "Mon Jun 26 23:04:09 CST 2000";
                } else {
                    modifyTime = doc.getFieldValue("last_modified").toString();
                }

                Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(modifyTime);
                modifyTime = date.toLocaleString();//日期转换成如：2015-9-10 13:06:33
                System.out.println(doc.getFieldValue("last_modified"));
                String author = null;
                if(doc.getFieldValue("author") == null) {
                    author = "微软用户";
                } else {
                    author = doc.getFieldValue("author").toString();
                }

                String indexTime = doc.getFieldValue("upload_time").toString();
                String hdfsPath = doc.getFieldValue("hdfs_path").toString();

                FileModel fileModel = new FileModel();
                fileModel.setId(id);
                fileModel.setName(name);
                fileModel.setType(type);
                fileModel.setAuthor(author);
                fileModel.setSize("");
                fileModel.setModifyTime(modifyTime);
                fileModel.setIndexTime(indexTime);
                fileModel.setHdfsPath(hdfsPath);

                List<String> titleList = highlightMap.get(id).get("resource_name");
                List<String> contentList = highlightMap.get(id).get("content_text");
                //获取并设置高亮的字段title
                if (titleList != null && titleList.size() > 0) {
                    fileModel.setHighlightName(titleList.get(0));
                }
                //获取并设置高亮的字段content
                if (contentList != null && contentList.size() > 0) {
                    fileModel.setHighlightContent(contentList.get(0));
                }
                fileModels.add(fileModel);
            }
            pageModel.setDatas(fileModels);
            pageModel.setCount(docs.getNumFound());

        } catch (Exception e) {
            log.error("从solr根据Page查询分页文档时遇到错误", e);
        }
        return pageModel;
    }

    @Override
    public PageModel queryFiles(String keyword) {
        return this.queryFiles(keyword, null);//默认值
    }

    @Override
    public PageModel queryFiles(String keyword, String type) {
        return this.queryFiles(keyword, type, 1, 10);//默认值
    }

    /**
     * 根据给出的关键字和文件类型查询并获取FileModel
     *
     * @param keyword,type
     * @return List<FileModel>
     */
    @Override
    public PageModel queryFiles(String keyword, String type, Integer currentPage, Integer pageSize) {

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("spring/hadooptools-spring-config.xml");
        cloudSolrServer = (CloudSolrServer)ctx.getBean("cloudSolrServer");

        cloudSolrServer.setDefaultCollection(defaultCollection);
        cloudSolrServer.setZkClientTimeout(zkClientTimeout);
        cloudSolrServer.setZkConnectTimeout(zkConnectTimeout);

        cloudSolrServer.connect(); //连接zookeeper

//        System.out.println("The cloud Server has been connected !!!!");

//        ZkStateReader zkStateReader = cloudSolrServer.getZkStateReader();
//        CloudState cloudState  = zkStateReader.getCloudState();
//        System.out.println(zkStateReader.getClusterState());

        PageModel pageModel = new PageModel(); //可以设置当前页，显示条数等
        pageModel.setParameter(keyword);//默认为第一页显示10条
        pageModel.setCurrent(currentPage);
        pageModel.setSize(pageSize);
        pageModel = getSolrQuery(cloudSolrServer, pageModel, type);
//        cloudSolrServer.shutdown(); //关闭cloudSolrServer
        return pageModel;
    }

    /**
     * 根据给出的关键字查询并获取FileType
     *
     * @param keyword
     * @return List<FileTypeModel>
     */
    @Override
    public List<FileTypeModel> queryFileTypes(String keyword) {

        List<FileTypeModel> models = new ArrayList<FileTypeModel>();

//        CloudSolrServer cloudSolrServer = CloudSolrUtils.getCloudSolrServer(); //创建cloudSolrServer

        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("spring/hadooptools-spring-config.xml");
        cloudSolrServer = (CloudSolrServer)ctx.getBean("cloudSolrServer");

        cloudSolrServer.setDefaultCollection(defaultCollection);
        cloudSolrServer.setZkClientTimeout(zkClientTimeout);
        cloudSolrServer.setZkConnectTimeout(zkConnectTimeout);
        cloudSolrServer.connect(); //连接zookeeper

        SolrQuery query = new SolrQuery();//建立一个新的查询
        query.setQuery("content_text:" + keyword);
        query.setFacet(true);//设置facet=on
        query.addFacetField("content_type");//设置需要facet的字段
//        query.setFacetLimit(10);//限制facet返回的数量
        QueryResponse response = null;
        try {
            response = cloudSolrServer.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        List<FacetField> facets = response.getFacetFields();//返回的facet列表(由设置的facet字段决定这里只有content_type)
        for (FacetField facet : facets) {
//            System.out.println(facet.getName());
//            System.out.println("----------------");

            int[] sum = new int[6];
            String[] str = {"doc","xls","ppt","pdf","txt","others"};
            List<FacetField.Count> counts = facet.getValues();
            for (FacetField.Count count : counts) {
//                System.out.println(count.getName() + ":" + count.getCount());
                String name = getFileTypeName(count.getName());
                int num = (int)count.getCount();
                if(name.equals("doc") || name.equals("docx")) {
                    sum[0] += num;
                } else  if(name.equals("xls") || name.equals("xlsx")) {
                    sum[1] += num;
                } else  if(name.equals("ppt") || name.equals("pptx")) {
                    sum[2] += num;
                } else  if(name.equals("pdf")) {
                    sum[3] += num;
                } else  if(name.equals("txt")) {
                    sum[4] += num;
                } else {
                    sum[5] += num;
                }
            }
            for(int i=0; i<6; i++) {
                FileTypeModel model = new FileTypeModel();
                model.setTypeName(str[i]);
                model.setTypeCount(sum[i]);
                models.add(model);//添加model
            }
        }
//        cloudSolrServer.shutdown();//关闭cloudSolrServer
        return models;
    }

    /**
     * 根据solr查询出来的content_type，转换出对应的文件格式
     *
     * @param type
     * @return String
     */
    public String getFileTypeName(String type) {
        String name;
        switch (type) {
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                name="docx";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                name="xlsx";
                break;
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                name="pptx";
                break;
            case "application/vnd.ms-powerpoint":
                name="ppt";
                break;
            case "application/vnd.ms-excel":
                name="xls";
                break;
            case "application/msword":
                name="doc";
                break;
            case "application/pdf":
                name="pdf";
                break;
            case "text/plain; charset=utf-8":
                name="txt";
                break;
            default:
                name="others";
                break;
        }
        return name;
    }

    /**
     * 根据页面文件格式转换成content_type
     *
     * @param type
     * @return String
     */
    public String getContentType(String type) {
        String name;
        switch (type) {
            case "doc":
                name = "content_type:application/vnd.openxmlformats-officedocument.wordprocessingml.document OR content_type:application/msword";
                break;
            case "xls":
                name = "content_type:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet OR content_type:application/vnd.ms-excel";
                break;
            case "ppt":
                name = "content_type:application/vnd.ms-powerpoint OR content_type:application/vnd.openxmlformats-officedocument.presentationml.presentation";
                break;
            case "pdf":
                name = "content_type:application/pdf";
                break;
            case "txt":
                name = "content_type:text/plain*";
                break;
            default:
                name = "content_type:*";
        }
        return name;
    }

}
