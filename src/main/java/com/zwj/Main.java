package com.zwj;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String org = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\org.txt";
            String words = "D:\\java_code\\031902333\\031902333\\src\\main\\resources\\words.txt";
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            AcUtils.AcNode root = AcUtils.getRoot();
            HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            AcUtils.creatKeyWords(root,keyWordsList);
            int line=0;
            for(String str:textList){
                line++;
               AcUtils.query(root,str,line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
