package com.zwj;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Main {
    public static void main(String[] args) {
         ArrayList<String> resultList;
         String words = args[0];
         String org = args[1];
         String ans = args[2];

     /*   String words = "src\\main\\resources\\example\\words.txt";
        String org = "src\\main\\resources\\example\\org.txt";
        String ans = "src\\main\\resources\\example\\ans.txt";*/
        try {
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            TireTree tree = new TireTree(keyWordsList);
            resultList = tree.getResultList(textList);
            FileUtils.writeLines(new File(ans), resultList,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
