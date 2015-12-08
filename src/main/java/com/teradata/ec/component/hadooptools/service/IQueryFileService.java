package com.teradata.ec.component.hadooptools.service;

import com.teradata.ec.component.hadooptools.model.FileTypeModel;
import com.teradata.ec.component.hadooptools.model.PageModel;

import java.util.List;
import java.util.Map;

/**
 * Created by taoyang on 11/6/15.
 */
public interface IQueryFileService {

    PageModel queryFiles(String keyword);

    PageModel queryFiles(String keyword, Map filterQuery);

    PageModel queryFiles(String keyword, Map filterQuery, Integer currentPage, Integer pageSize);

    List<FileTypeModel> queryFileTypes(String keyword, Map filterQuery);

}
