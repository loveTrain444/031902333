package com.zwj;



import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AhoCorasickAutomationTest {
  @Test
    public void fun1() throws BadHanyuPinyinOutputFormatCombination {
    List<String> keyWords = new ArrayList<>();
    HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    format.setVCharType(HanyuPinyinVCharType.WITH_V);
    String keyWord = "邪教";
      String [][] matrix = new String[keyWord.length()][4];
      for(int i=0;i<keyWord.length();i++){
        matrix[i][0] = String.valueOf(keyWord.charAt(i));
        String[] Spelling = new String[0];
        try {
          Spelling = PinyinHelper.toHanyuPinyinStringArray(keyWord.charAt(i),format);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
          badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        //如果不是汉字则直接按原字符填充
        if (Spelling==null){
          matrix[i][1] = String.valueOf(keyWord.charAt(i));
          matrix[i][2] = String.valueOf(keyWord.charAt(i));
          matrix[i][3] = String.valueOf(keyWord.charAt(i));
          continue;
        }
        matrix[i][1] = Spelling[0];
        matrix[i][2] = String.valueOf(Spelling[0].charAt(0));
        matrix[i][3] = "{"+Spelling[0]+"}";
      }

  }
}

