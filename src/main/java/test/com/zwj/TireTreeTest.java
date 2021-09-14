package test.com.zwj;

import com.zwj.TireTree;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
* TireTree Tester. 
* 
* @author <Authors name> 
* @since <pre>9月 14, 2021</pre> 
* @version 1.0 
*/ 
public class TireTreeTest {
/** 
* 
* Method: getResultList(List<String> textList) 
* 
*/ 
@Test
public void testGetResultList() throws Exception {
        ArrayList<String> actualResultList;
        //文本
        List<String> textList = Arrays.asList(
            "w3。12}{3d31#231n21m121d即使还带i很多汉文化我f车轮力的",
            "法423lun312gong达到坏得很洗fa",
            "法*++=lun@工力dhadfa车仑工",
            "安徽iiawdxi#发嗲awdaw花a3@12d我低洼和你比");
        //关键字
        List<String> keyWordsList = Arrays.asList(
            "法轮功",
            "洗发",
            "wdnmd");
        //预期答案
        List<String> expectedResultList = Arrays.asList(
            "total: 5",
            "Line1: <wdnmd> w3。12}{3d31#231n21m121d",
            "Line2: <洗发> 洗fa",
            "Line3: <法轮功> 法*++=lun@工力",
            "Line3: <法轮功> fa车仑工",
            "Line4: <洗发> xi#发"
        );
        TireTree tree = new TireTree(keyWordsList);
        actualResultList = tree.getResultList(textList);
        Assert.assertEquals(expectedResultList,actualResultList);
        }
} 
