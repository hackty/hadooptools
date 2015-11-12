package com.teradata.ec.component.hadooptools.service;

import java.io.IOException;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface ICopyFileService {
    void copyFiles(String src, String dest) throws IOException;
}
