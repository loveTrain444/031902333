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
        //测试纯敏感词
        @Test
        public void testGetResultList_1() {
        ArrayList<String> actualResultList;
        //文本
        List<String> textList = Arrays.asList(
                "大叔打骆驼小骄大傲",
                "大fhello京das东哎"
               );
        //关键字
        List<String> keyWordsList = Arrays.asList(
                "骆驼",
                "hello"
                );
        //预期答案
        List<String> expectedResultList = Arrays.asList(
                "total: 2",
                "Line1: <骆驼> 骆驼",
                "Line2: <hello> hello"
        );
        TireTree tree = new TireTree(keyWordsList);
        actualResultList = tree.getResultList(textList);
        Assert.assertEquals(expectedResultList,actualResultList);
        }
        //测试中文拼音全拼
        @Test
        public void testGetResultList_2() {
                ArrayList<String> actualResultList;
                //文本
                List<String> textList = Arrays.asList(
                        "大叔打luotuo小骄大傲",
                        "大nihao京das东哎"
                );
                //关键字
                List<String> keyWordsList = Arrays.asList(
                        "骆驼",
                        "你好"
                );
                //预期答案
                List<String> expectedResultList = Arrays.asList(
                        "total: 2",
                        "Line1: <骆驼> luotuo",
                        "Line2: <你好> nihao"
                );
                TireTree tree = new TireTree(keyWordsList);
                actualResultList = tree.getResultList(textList);
                Assert.assertEquals(expectedResultList,actualResultList);
        }
        //测试汉字首字母
        @Test
        public void testGetResultList_3() {
                ArrayList<String> actualResultList;
                //文本
                List<String> textList = Arrays.asList(
                        "大叔打lt小骄大傲",
                        "大nh京das东哎"
                );
                //关键字
                List<String> keyWordsList = Arrays.asList(
                        "骆驼",
                        "你好"
                );
                //预期答案
                List<String> expectedResultList = Arrays.asList(
                        "total: 2",
                        "Line1: <骆驼> lt",
                        "Line2: <你好> nh"
                );
                TireTree tree = new TireTree(keyWordsList);
                actualResultList = tree.getResultList(textList);
                Assert.assertEquals(expectedResultList,actualResultList);
        }
        //测试谐音字
        @Test
        public void testGetResultList_4() {
                ArrayList<String> actualResultList;
                //文本
                List<String> textList = Arrays.asList(
                        "大叔打罗托小骄大傲",
                        "大fhello泥嚎京das东哎"
                );
                //关键字
                List<String> keyWordsList = Arrays.asList(
                        "骆驼",
                        "你好"
                );
                //预期答案
                List<String> expectedResultList = Arrays.asList(
                        "total: 2",
                        "Line1: <骆驼> 罗托",
                        "Line2: <你好> 泥嚎"
                );
                TireTree tree = new TireTree(keyWordsList);
                actualResultList = tree.getResultList(textList);
                Assert.assertEquals(expectedResultList,actualResultList);
        }
        //测试部首拆分
        @Test
        public void testGetResultList_5() {
                ArrayList<String> actualResultList;
                //文本
                List<String> textList = Arrays.asList(
                        "大叔打马各马它小骄大傲",
                        "大fhello亻尔女子京das东哎"
                );
                //关键字
                List<String> keyWordsList = Arrays.asList(
                        "骆驼",
                        "你好"
                );
                //预期答案
                List<String> expectedResultList = Arrays.asList(
                        "total: 2",
                        "Line1: <骆驼> 马各马它",
                        "Line2: <你好> 亻尔女子"
                );
                TireTree tree = new TireTree(keyWordsList);
                actualResultList = tree.getResultList(textList);
                Assert.assertEquals(expectedResultList,actualResultList);
        }

        //测试非法字符，如果汉字中有数字就不算敏感词
        //英语里有数字算敏感词
        @Test
        public void testGetResultList_6() {
                ArrayList<String> actualResultList;
                //文本
                List<String> textList = Arrays.asList(
                        "大叔打骆%&（（%￥+驼小骄大傲",
                        "骆46%￥驼",
                        "大fhellhel*l(&75700==o好京das东哎"
                );
                //关键字
                List<String> keyWordsList = Arrays.asList(
                        "骆驼",
                        "hello"
                );
                //预期答案
                List<String> expectedResultList = Arrays.asList(
                        "total: 2",
                        "Line1: <骆驼> 骆%&（（%￥+驼",
                        "Line3: <hello> hel*l(&75700==o"
                );
                TireTree tree = new TireTree(keyWordsList);
                actualResultList = tree.getResultList(textList);
                Assert.assertEquals(expectedResultList,actualResultList);
        }
        //所有情况混合检测
        @Test
        public void testGetResultList_7() {
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
