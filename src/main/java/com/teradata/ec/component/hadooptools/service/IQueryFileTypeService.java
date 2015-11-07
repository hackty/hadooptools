package com.teradata.ec.component.hadooptools.service;


import com.teradata.ec.component.hadooptools.model.FileTypeModel;

import java.util.List;

/**
 * Created by taoyang on 11/6/15.
 */
public interface IQueryFileTypeService {

    List<FileTypeModel> queryFileTypes(String wd);

}
