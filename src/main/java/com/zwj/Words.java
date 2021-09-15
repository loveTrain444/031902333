package com.zwj;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Words{
    public static String illegalString = "0123456789[\"`~!@#$%^&*()+=|{}':;',\\.<>/?~！@#￥%……&*（）——+| {}【】‘；：”“’。，、？_]";
    //汉语拼音的格式
    public static HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
    //查找部首拆分的字典
    public static Map<Character,String> dictionaryOfBreak = new HashMap<>();
    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
            //读取文件里的拆分词库
            List<String> breakList = new Recource().getResource();
            for(String str:breakList){
                char word = str.charAt(3);
                int i=8;
                while(str.charAt(i)!='\"'){
                    i++;
                }
                String breakUP = str.substring(8,i);
                dictionaryOfBreak.put(word,breakUP);
            }
    }
    //判断是否是非法字符
   public static boolean isIllegal(char c){
        String str = illegalString;
        return str.contains(String.valueOf(c));
    }
    //递归进行敏感词所有分支的组合
    private static void com(int step,int len,String[][] matrix,String str,HashMap<String,String> dictionaryOfKeyword){
        if(step == len){
            StringBuilder keyword = new StringBuilder();
            for(int i=0;i<len;i++){
                keyword.append(matrix[i][0]);
            }
            dictionaryOfKeyword.put(str, keyword.toString());
        }
        else {
            for (int k=1;k<matrix[step].length;k++){
                com(step+1,len,matrix,str+matrix[step][k], dictionaryOfKeyword);
            }
        }
    }
    public static HashMap<String,String> createDictionaryOfKeyword(List<String> list){
        HashMap<String,String> dictionaryOfKeyword = new HashMap<>();
        for(String keyWord:list){
            String [][] matrix ;
            matrix = new String[keyWord.length()][];
            //获取敏感词的扩展矩阵
            for(int i=0;i<keyWord.length();i++){
                try {
                    String key = String.valueOf(keyWord.charAt(i));
                    String[] Spelling ;
                    Spelling = PinyinHelper.toHanyuPinyinStringArray(keyWord.charAt(i),Words.format);
                    //Spelling返回null代表该敏感词字符是不是汉字，则有一个分支；是汉字有五个分支
                    //比如功有五种情况：功，gong.{gong},g,工力
                    if(Spelling!=null){
                        matrix[i] = new String[5];
                        matrix[i][0] = key;
                        matrix[i][1] = Spelling[0];
                        matrix[i][2] = "{"+Spelling[0]+"}";
                        matrix[i][3] = String.valueOf(Spelling[0].charAt(0));
                        matrix[i][4] = Words.dictionaryOfBreak.get(keyWord.charAt(i));

                    }else {
                        matrix[i] = new String[2];
                        matrix[i][0] = key;
                        matrix[i][1] = key;
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }

            }
            /* 根据分支组合出所有情况 */
            Words.com(0, keyWord.length(), matrix, "",dictionaryOfKeyword);
        }
        return dictionaryOfKeyword;
    }
    //判断字符串是否含中文
    public static boolean isNotContainChinese(String p) {
        byte[] bytes = p.getBytes();
        int i = bytes.length;//i为字节长度
        int j = p.length();//j为字符长度
        return i == j;
    }
}
