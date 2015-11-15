package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.service.ICopyFileService;
import com.teradata.ec.component.hadooptools.util.FileMover;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Administrator on 2015/11/11.
 */
@Service
public class CopyFileServiceImpl implements ICopyFileService{

    @Override
    public void copyFiles(String src, String dest, Integer interval, boolean needDelete) throws IOException {
        FileMover.copyFiles(src,dest, interval, needDelete);
    }
}
