package com.zwj;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

public class DfaUtils {
   // 1、创建敏感字Map

    public static Map addWordToHashMap(List<String> keyWordSet) {
        if(0 == keyWordSet.size()) return new HashMap();
        Map map = new HashMap(keyWordSet.size());
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        Iterator<String> iterator = keyWordSet.iterator();
        while(iterator.hasNext()){
            key = iterator.next();
            nowMap = map;
            for(int i = 0; i < key.length(); i++){
                char keyChar = key.charAt(i);
                Map wordMap = (Map) nowMap.get(keyChar);
                if(wordMap != null) nowMap =  wordMap;
                else{
                    newWorMap = new HashMap<String,String>();
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                if(i == key.length() - 1) nowMap.put("isEnd", "1");
            }
        }
        return map;
    }
 

//2、创建正则特殊符号过滤

    public static String formatString(String str) {
        if (StringUtils.isNotBlank(str)) {
            String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            return   Pattern.compile(regEx).matcher(str).replaceAll("").trim();
        }
        return "";
    }
 

//3、校验字符串



    public static String checkWork(String txt, Map map, boolean needToFormat) {
        try {
            if (needToFormat) txt = formatString(txt);
            StringBuffer sb = new StringBuffer();
            boolean has = false;
            char word;
            int num;
            for (int i = 0; i < txt.length(); i++) {
                word = txt.charAt(i);
                sb = new StringBuffer(word);
                if (map.containsKey(word)) {
                    Map m = (Map) map.get(word);
                    sb.append(word);
                    num = i + 1;
                    while (true) {
                        if (num < txt.length()) {
                            word = txt.charAt(num++);
                            sb.append(word);
                            if (m.containsKey(word)){
                                m = (Map) m.get(word);
                                if (m.containsKey("isEnd")) {
                                    has = true;
                                    return sb.toString();
                                }
                            } else break;
                        } else break;
                    }
                }
            }
            if (has)  return sb.toString();
        } catch (Exception e) {
            //logger.error("检验敏感字出错，原因为：{}", e);
            e.printStackTrace();
        }
        return null;
    }
 

}
