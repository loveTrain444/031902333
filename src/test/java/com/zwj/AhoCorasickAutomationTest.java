package com.zwj;



import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.Test;

import java.text.Format;
import java.util.*;

public class AhoCorasickAutomationTest {
  @Test
    public void fun1() throws BadHanyuPinyinOutputFormatCombination {
       HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
      format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
      format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
      format.setVCharType(HanyuPinyinVCharType.WITH_V);
      String[] sp1 = PinyinHelper.toHanyuPinyinStringArray('邪',format);
      String[] sp2 = PinyinHelper.toHanyuPinyinStringArray('法',format);
    System.out.println(sp1[0]);
  }
}

