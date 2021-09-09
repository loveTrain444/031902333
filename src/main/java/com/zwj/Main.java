package com.zwj;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            String org = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\org.txt";
            String words = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\words.txt";
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            Map map = DfaUtils.addWordToHashMap(keyWordsList);
            int len=0;
            for(String str:textList){
                len++;
                String ans = DfaUtils.checkWork(str, map, true);
                if(ans!=null){
                    System.out.println("line"+len+": "+ans);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
