package com.teradata.ec.component.hadooptools.service;

import com.teradata.ec.component.hadooptools.model.FileModel;

import java.util.List;

/**
 * Created by taoyang on 11/6/15.
 */
public interface IQueryFileService {

    List<FileModel> queryFiles(String wd);

}