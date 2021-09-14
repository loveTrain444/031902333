package com.zwj;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        ArrayList<String> resultList;
        /*
         String words = args[0];
         String org = args[1];
         String ans = args[2];
            */
        String words = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\words.txt";
        String org = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\org.txt";
        String ans = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\ans.txt";
        try {
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            TireTree tree = new TireTree(keyWordsList);
            resultList = tree.getResultList(textList);
            FileUtils.writeLines(new File(ans), resultList,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
