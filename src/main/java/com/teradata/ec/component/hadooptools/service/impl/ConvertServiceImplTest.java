package com.teradata.ec.component.hadooptools.service.impl;


import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by Administrator on 2015/11/25.
 */
public class ConvertServiceImplTest {

    /**
     * 判断文件是否存在
     * @param fileName
     * @return boolean.
     */
    public static boolean fileExists(String fileName) throws IOException {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * doc转pdf
     * @param docPath
     * @param pdfLocation
     * @return pdfPath.
     */
    public static String doc2pdf(String docPath, String pdfLocation) {
//    public static String doc2pdf(String docPath, String pdfLocation) {
        String fileName = docPath.substring(docPath.lastIndexOf("/")+1, docPath.lastIndexOf("."));
        String pdfPath = pdfLocation + "/" + fileName + ".pdf";

        //./soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
        //java -jar /root/doc_view/jodconverter-2.2.2/lib/jodconverter-cli-2.2.2.jar  /root/doc_view/徽商银行.docx /root/doc_view/徽商银行.pdf

        String[] toPdf = {"/bin/sh","-c","java -jar /root/doc_view/jodconverter-2.2.2/lib/jodconverter-cli-2.2.2.jar"};
        toPdf[2] += " " + docPath;
        toPdf[2] += " " + pdfPath;

        Process pro = null;
        try {
            if(!fileExists(pdfPath)) {
                System.out.println("DOC转PDF开始");
                pro = Runtime.getRuntime().exec(toPdf);
                InputStreamReader ir = new InputStreamReader(pro.getInputStream());
                BufferedReader input = new BufferedReader (ir);
                String line;
                while ((line = input.readLine ()) != null){
                    System.out.println(line);
                }
                System.out.println("DOC转PDF完毕");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        try {
            pro.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return pdfPath;
    }

    /**
     * pdf转swf
     * @param pdfPath
     * @param swfLocation
     * @return swfPath.
     */
    public static String pdf2swf(String pdfPath, String swfLocation){
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
/*        try {
            pro.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return swfPath;
    }

    /**
     * doc转swf
     * @param docPath
     * @param pdfLocation
     * @param swfLocation
     * @return swfPath.
     */
    public static String doc2swf(String docPath, String pdfLocation, String swfLocation) throws IOException {
//    public static String doc2swf(String docPath, String pdfLocation, String swfLocation){
        String fileName = docPath.substring(docPath.lastIndexOf("/")+1, docPath.lastIndexOf("."));
        String pdfPath = pdfLocation + "/" + fileName + ".pdf";

        //String pdfPath = doc2pdf(docPath, pdfLocation);
        System.out.println("启动doc2pdf");
        doc2pdf(docPath, pdfLocation);


        /*while(!file.exists()) {
            file = new File(pdfPath);
            System.out.println(file.length());
        }*/



        Thread td = Thread.currentThread();
        try {
            td.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File file = new File(pdfPath);
        FileOutputStream fis = new FileOutputStream(file);
        FileChannel fc = fis.getChannel();
        // 试图获取对此通道的文件的独占锁定
        // 如果由于另一个程序保持着一个重叠锁定而无法获取锁定，则返回 null
        FileLock fl = fc.tryLock();
        while(fl == null) {
            fl = fc.tryLock();
        }
        fl.release();
        fc.close();
        fis.close();

        System.out.println("启动pdf2swf");
        String swfPath = pdf2swf(pdfPath, swfLocation);

        return swfPath;
    }

    public static void main(String[] args) {
        ConvertServiceImplTest.doc2pdf(args[0], args[1]);
/*        try {
            ConvertServiceImplTest.doc2swf(args[0], args[1], args[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
