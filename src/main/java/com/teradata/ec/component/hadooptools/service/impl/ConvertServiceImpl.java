package com.teradata.ec.component.hadooptools.service.impl;

import com.teradata.ec.component.hadooptools.service.IConvertService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/11/25.
 */
//public class ConvertServiceImpl {
@Service
public class ConvertServiceImpl implements IConvertService {
    /**
     * 判断文件是否存在
     * @param fileName
     * @return boolean.
     */
    public boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * doc转pdf
     * @param docPath
     * @param pdfLocation
     * @return pdfPath.
     */
    @Override
    public String doc2pdf(String docPath, String pdfLocation) {
//    public static String doc2pdf(String docPath, String pdfLocation) {
        String fileName = docPath.substring(docPath.lastIndexOf("/")+1, docPath.lastIndexOf("."));
        String pdfPath = pdfLocation + "/" + fileName + ".pdf";

        //./soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
        //java -jar /root/doc_view/jodconverter-2.2.2/lib/jodconverter-cli-2.2.2.jar  /root/doc_view/徽商银行.docx /root/doc_view/徽商银行.pdf

        String[] toPdf = {"/bin/sh","-c","java -jar ~/Service/doc_view/jodconverter-2.2.2/lib/jodconverter-cli-2.2.2.jar"};
        toPdf[2] += " " + docPath;
        toPdf[2] += " " + pdfPath;

        Process pro = null;
        try {
            if(!fileExists(pdfPath)) {
                pro = Runtime.getRuntime().exec(toPdf);
                InputStreamReader ir = new InputStreamReader(pro.getInputStream());
                BufferedReader input = new BufferedReader (ir);
                String line;
                while ((line = input.readLine ()) != null){
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pro.waitFor();

        return pdfPath;
    }

    /**
     * pdf转swf
     * @param pdfPath
     * @param swfLocation
     * @return swfPath.
     */
    @Override
    public String pdf2swf(String pdfPath, String swfLocation){
//    public static String pdf2swf(String pdfPath, String swfLocation){
        String fileName = pdfPath.substring(pdfPath.lastIndexOf("/")+1, pdfPath.lastIndexOf("."));
        String swfPath = swfLocation + "/" + fileName + ".swf";

        //pdf2swf -T 9 -s poly2bitmap -s zoom=150 -s flashversion=9 "/root/doc_view/徽商银行.pdf" -o "/root/doc_view/徽商银行.swf"

        String[] toSwf = {"/bin/sh","-c","pdf2swf -T 9 -s poly2bitmap -s zoom=150 -s flashversion=9 "};
        toSwf[2] += pdfPath;
        toSwf[2] += " -o ";
        toSwf[2] += swfPath;

        Process pro = null;
        try {
            if(!fileExists(swfPath)) {
                pro = Runtime.getRuntime().exec(toSwf);
                InputStreamReader ir = new InputStreamReader(pro.getInputStream());
                BufferedReader input = new BufferedReader (ir);
                String line;
                while ((line = input.readLine ()) != null){
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return swfPath;
    }

    /**
     * doc转swf
     * @param docPath
     * @param pdfLocation
     * @param swfLocation
     * @return swfPath.
     */
    @Override
    public String doc2swf(String docPath, String pdfLocation, String swfLocation){
//    public static String doc2swf(String docPath, String pdfLocation, String swfLocation){
        String pdfPath = doc2pdf(docPath, pdfLocation);
        String swfPath = pdf2swf(pdfPath, swfLocation);
        return swfPath;
    }

//    public static void main(String[] args) {
//        ConvertServiceImpl.doc2pdf(args[0], args[1]);
//        ConvertServiceImpl.doc2swf(args[0], args[1], args[2]);
//    }

}
