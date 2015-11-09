package com.teradata.ec.component.hadooptools.service;

import com.teradata.ec.component.hadooptools.model.FileModel;
import com.teradata.ec.component.hadooptools.model.FileTypeModel;

import java.util.List;

/**
 * Created by taoyang on 11/6/15.
 */
public interface IQueryFileService {

    List<FileModel> queryFiles(String wd);

    List<FileModel> queryFiles(String wd,String type);

    List<FileModel> queryFiles(String wd,String type,Integer start,Integer limit);

    List<FileTypeModel> queryFileTypes(String wd);

}
