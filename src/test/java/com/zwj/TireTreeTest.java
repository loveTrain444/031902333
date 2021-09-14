package com.zwj;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class TireTreeTest {
    @Test
    public void testGetResultList_1() {
        ArrayList<String>  actualResultList;
        String words = "src/test/resources/test_1/words.txt";
        String org = "src/test/resources/test_1/org.txt";
        String expectedAns = "src/test/resources/test_1/expectedAns.txt";
        try {
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            ArrayList<String> expectedResultList = (ArrayList<String>) FileUtils.readLines(new File(expectedAns), "UTF-8");
            TireTree tree = new TireTree(keyWordsList);
            actualResultList = tree.getResultList(textList);
            Assert.assertEquals(expectedResultList,actualResultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetResultList_2() {
        ArrayList<String>  actualResultList;
        String words = "src/test/resources/test_2/words.txt";
        String org = "src/test/resources/test_2/org.txt";
        String expectedAns = "src/test/resources/test_2/expectedAns.txt";
        try {
            List<String> textList = FileUtils.readLines(new File(org), "UTF-8");
            List<String> keyWordsList = FileUtils.readLines(new File(words), "UTF-8");
            ArrayList<String> expectedResultList = (ArrayList<String>) FileUtils.readLines(new File(expectedAns), "UTF-8");
            TireTree tree = new TireTree(keyWordsList);
            actualResultList = tree.getResultList(textList);
            Assert.assertEquals(expectedResultList,actualResultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}