package com.zwj;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        ArrayList<String> resultSet = new ArrayList<>();
        resultSet.add("");
        try {
            String words = args[0];
            String org = args[1];
            String ans = args[2];
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            AcUtils.AcNode root = AcUtils.getRoot();
            AcUtils.creatKeyWords(root,keyWordsList);
            int line = 0;
            for(String str :textList){
                line++;
                resultSet.addAll(AcUtils.query(root, str, line));
            }
            resultSet.set(0,"total: "+(resultSet.size()-1));
            FileUtils.writeLines(new File(ans), resultSet,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
