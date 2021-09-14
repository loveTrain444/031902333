package test.com.zwj;

import com.zwj.Words;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/** 
* Words Tester. 
* 
* @author <Authors name> 
* @since <pre>9月 14, 2021</pre> 
* @version 1.0 
*/ 
public class WordsTest { 

@Test
public void testIsIllegal() throws Exception {
    String str = "*&（（）32sad1adw%打的d￥是擦谁先手#大@阿瓦&932+_+=~`";
    String excepted = "*&（（）321%￥#@&932+_+=~`";
    StringBuilder ans = new StringBuilder();
    for(int i=0;i<str.length();i++){
        if(Words.isIllegal(str.charAt(i))){
            ans.append(str.charAt(i));
        }
    }
    Assert.assertEquals(excepted,ans.toString());
} 

/** 
* 
* Method: createDictionaryOfKeyword(List<String> list) 
* 
*/ 
@Test
public void testCreateDictionaryOfKeyword() throws Exception {
    List<String> keyWords = Arrays.asList(
            "你好",
            "hello"
    );
    HashMap<String, String> dictionaryOfKeyword = Words.createDictionaryOfKeyword(keyWords);
    HashMap<String,String> expected = new HashMap<>();
    expected.put("亻尔{hao}","你好");
    expected.put("nihao","你好");
    expected.put("ni女子","你好");
    expected.put("n女子","你好");
    expected.put("{ni}女子","你好");
    expected.put("nih","你好");
    expected.put("亻尔hao","你好");
    expected.put("{ni}{hao}","你好");
    expected.put("ni{hao}","你好");
    expected.put("亻尔女子","你好");
    expected.put("{ni}h","你好");
    expected.put("{ni}hao","你好");
    expected.put("nh","你好");
    expected.put("nhao","你好");
    expected.put("hello","hello");
    expected.put("亻尔h","你好");
    expected.put("n{hao}","你好");
    Assert.assertEquals(expected,dictionaryOfKeyword);
} 

/** 
* 
* Method: isNotContainChinese(String p) 
* 
*/ 
@Test
public void testIsNotContainChinese_1() throws Exception {
    String str = "das中dad文d";
    Assert.assertFalse(Words.isNotContainChinese(str));
}
@Test
public void testIsNotContainChinese_2() throws Exception {
        String str = "dasdad**&%d";
        Assert.assertTrue(Words.isNotContainChinese(str));
}
@Test(expected = IOException.class)
public void testIndexOutOfBoundsException() throws IOException {
    String path = "src\\main\\resources\\example\\no.txt";
    List<String> textList = FileUtils.readLines(new File(path), "UTF-8");
}
}
