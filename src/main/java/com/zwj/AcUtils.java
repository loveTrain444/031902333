package com.zwj;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.*;
//使用AC自动机算法
public class AcUtils {
    public static String illegalString = "0123456789[\"`~!@#$%^&*()+=|{}':;',\\.<>/?~！@#￥%……&*（）——+| {}【】‘；：”“’。，、？_]";
    public static HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
    //查找原词的字典
    public static Map<String,String> dictionaryOfKeyword = new HashMap<>();
    //查找部首拆分的字典
    public static Map<Character,String> dictionaryOfBreak = new HashMap<>();
    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        try {
            List<String> breakList = FileUtils.readLines(new File("D:\\java_code\\031902333\\031902333\\src\\main\\resources\\拆分词库.txt"), "UTF-8");
            for(String str:breakList){
                char word = str.charAt(3);
                int i=8;
                while(str.charAt(i)!='\"'){
                    i++;
                }
                String breakUP = str.substring(8,i);
                dictionaryOfBreak.put(word,breakUP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class AcNode {
        //孩子节点用HashMap存储，能够在O(1)的时间内查找到，效率高
        Map<String,AcNode> children=new HashMap<>();
        AcNode failNode;
        //使用set集合存储字符长度，防止敏感字符重复导致集合内数据重复
        int wordLength = 0;
    }
    //获取根节点
    public static AcNode getRoot(){
        return new AcNode();
    }
    //判断字符串内是否含有中文，原理是中文占俩个字节，英文和其他字符占一个
    private static boolean isNotContainChinese(String p) {
        byte[] bytes = p.getBytes();
        int i = bytes.length;//i为字节长度
        int j = p.length();//j为字符长度
        return i == j;
    }
    //插入节点
    public static void insert(AcNode root,String s){
        AcNode cur=root;
        int len=0;
        for (int i = 0; i < s.length(); i++) {
            String sub;
            //将拼音转化成整个字符串装入node
            if(s.charAt(i)=='{'){
                StringBuilder subStr= new StringBuilder();
                i=i+1;
                while (s.charAt(i)!='}'){
                    subStr.append(s.charAt(i));
                    i++;
                }
                sub = subStr.toString();
            }else {
                sub = String.valueOf(s.charAt(i));
            }
            if (!cur.children.containsKey(sub)){ //如果不包含这个字符就创建孩子节点
                cur.children.put(sub, new AcNode());
            }
            cur = cur.children.get(sub);//temp指向孩子节点
            len++;
        }
        cur.wordLength = len;//一个字符串遍历完了后，将其长度保存到最后一个孩子节点信息中
    }
    public static void creatKeyWords(AcNode root, List<String> list){
        List<String> keyWords = new ArrayList<>();
        for(String keyWord:list){
            //如果是敏感词英文直接加入词库
            if(isNotContainChinese(keyWord)){
                keyWords.add(keyWord);
                dictionaryOfKeyword.put(keyWord,keyWord);
                continue;
            }
            String [][] matrix = new String[keyWord.length()][4];
            //获取敏感词的扩展矩阵
                for(int i=0;i<keyWord.length();i++){
                    matrix[i][0] = String.valueOf(keyWord.charAt(i));
                    String[] Spelling = new String[0];
                    try {
                        Spelling = PinyinHelper.toHanyuPinyinStringArray(keyWord.charAt(i),format);
                    } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                matrix[i][1] = Spelling[0];
                matrix[i][2] = "{"+Spelling[0]+"}";
                matrix[i][3] = String.valueOf(Spelling[0].charAt(0));
            }
            //根据矩阵组合出所有敏感词
            List<String> result = new ArrayList<>();
            List<String> com = com(0, keyWord.length(), matrix, "", result);
            keyWords.addAll(com);
            //构建拆分词库
            for (String str:list){
                StringBuilder br = new StringBuilder();
                for(int i=0;i<str.length();i++){
                    br.append(dictionaryOfBreak.get(str.charAt(i)));
                }
                dictionaryOfKeyword.put(br.toString(),str);
            }
        }
        //将敏感词库建成树
        for(String keyword:keyWords){
            insert(root,keyword);
        }
        buildFailPath(root);
    }

    private static List<String> com(int step,int len,String[][] matrix,String str,List<String> result){
        if(step == len){
            StringBuilder keyword = new StringBuilder();
            for(int i=0;i<len;i++){
                keyword.append(matrix[i][0]);
            }
            dictionaryOfKeyword.put(str, keyword.toString());
            result.add(str);
        }
          else {
              for (int k=1;k<4;k++){
                  com(step+1,len,matrix,str+matrix[step][k],result);
              }
        }
        return result;
    }
    private static void buildFailPath(AcNode root){
        //第一层的fail指针指向root,并且让第一层的节点入队，方便BFS
        Queue<AcNode> queue = new LinkedList<>();
        Map<String,AcNode> children = root.children;
        for (Map.Entry<String, AcNode> next : children.entrySet()) {
            queue.offer(next.getValue());
            next.getValue().failNode = root;
        }
        //构建剩余层数节点的fail指针,利用层次遍历
        while(!queue.isEmpty()){
            AcNode x=queue.poll();
            children=x.children; //取出当前节点的所有孩子
            for (Map.Entry<String, AcNode> next : children.entrySet()) {
                AcNode y = next.getValue();  //得到当前某个孩子节点
                AcNode faFail = x.failNode;  //得到孩子节点的父节点的fail节点
                //如果 faFail节点没有与 当前节点父节点具有相同的转移路径，则继续获取 fafail 节点的失败指针指向的节点，将其赋值给 fafail
                while (faFail != null && (!faFail.children.containsKey(next.getKey()))) {
                    faFail = faFail.failNode;
                }
                //回溯到了root节点，只有root节点的fail才为null
                if (faFail == null) {
                    y.failNode = root;
                } else {
                    //fafail节点有与当前节点父节点具有相同的转移路径，则把当前孩子节点的fail指向fafail节点的孩子节点
                    y.failNode = faFail.children.get(next.getKey());
                }
                /* 如果当前节点的fail节点有保存字符串的长度信息，则把信息存储合并到当前节点
                  if (y.failNode.wordLengthList!=null){
                    y.wordLengthList.addAll(y.failNode.wordLengthList);
                }*/
                queue.offer(y);//最后别忘了把当前孩子节点入队
            }
        }

    }
    private static boolean isIllegal(char c){
       String str = illegalString;
        return str.contains(String.valueOf(c));
    }
    public static ArrayList<String> query(AcNode root, String s, int line){
        ArrayList<String> resultSet = new ArrayList<>();
        AcNode temp = root;
        int tmp=-1;
        for (int i = 0; i < s.length(); i++) {
            //如果是非法字符直接跳过
            if (isIllegal(s.charAt(i))){
                continue;
            }
            try {
                //spelling如果是null说明该字符不是汉字
                String[] sp = PinyinHelper.toHanyuPinyinStringArray(s.charAt(i), format);
                //先查拆分库，若查出直接加入结果集
                if(sp!=null) {
                    for (Map.Entry<String, String> entry : dictionaryOfKeyword.entrySet()) {
                        if (i + entry.getKey().length() > s.length()) {
                            continue;
                        }
                        String sub = s.substring(i, i + entry.getKey().length());
                        if (entry.getKey().equals(sub)) {
                            resultSet.add("line" + line + ": " + "<" + dictionaryOfKeyword.get(entry.getKey()) + "> " + entry.getKey());
                            i += entry.getKey().length();
                            break;
                        }
                    }
                }
                String[] spelling = PinyinHelper.toHanyuPinyinStringArray(s.charAt(i), format);
                String str = String.valueOf(s.charAt(i));
                //如果是汉字转换为拼音，方便之后查询谐音字
                if(spelling!=null){
                    str = spelling[0];
                }else{
                    //如果是英文字符转换成小写统一查询
                    str = str.toLowerCase();
                }
                //如果这个字符在当前节点的孩子里面没有或者当前节点的fail指针不为空，就有可能通过fail指针找到这个字符
                //所以就一直向上更换temp节点
                while(temp.children.get(str)==null&&temp.failNode!=null){
                temp=temp.failNode;
                }
                //如果因为当前节点的孩子节点有这个字符，则将temp替换为下面的孩子节点
                 if (temp.children.get(str)!=null){
                temp=temp.children.get(str);
                 }
                //如果temp的failnode为空，代表temp为root节点，没有在树中找到符合的敏感字，故跳出循环，检索下个字符
                else{
                    continue;
                }
                //如果检索到当前节点的长度信息存在，则代表搜索到了敏感词，打印输出即可
                 if (temp.wordLength!=0){
                    int startIndex=handleMatchWords(temp,s,i,line,resultSet);
                    if(startIndex==tmp){
                        resultSet.remove(resultSet.size()-2);
                    }
                    tmp=startIndex;
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return resultSet;
    }

    //利用节点存储的字符长度信息，打印输出敏感词及其在搜索串内的坐标
    private static int handleMatchWords(AcNode node, String text, int currentPos, int line, ArrayList<String> resultSet) {
            int startIndex;
            StringBuilder ans1 = new StringBuilder();
            StringBuilder ans2 = new StringBuilder();
            int pos = currentPos;
            int cnt = node.wordLength;
            while(cnt>0){
                if(!isIllegal(text.charAt(pos))){
                    ans1.append(text.charAt(pos));
                    ans2.append(text.charAt(pos));
                    cnt--;
                }
                   else{
                       ans2.append(text.charAt(pos));
                }
                   pos--;
            }
            startIndex=pos+1;
            ans2.reverse();
            ans1.reverse();
            String newAns;
                for(int i=0;i<ans1.length();i++) {
                    try {
                        String[] sp = PinyinHelper.toHanyuPinyinStringArray(ans1.charAt(i), format);
                        //是汉字
                        if (sp != null) {
                            ans1.replace(i, i + 1, sp[0]);
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                        badHanyuPinyinOutputFormatCombination.printStackTrace();
                    }
                }
                newAns = dictionaryOfKeyword.get(ans1.toString().toLowerCase());
            resultSet.add("Line"+line+": "+"<"+newAns+"> " +ans2);
            return startIndex;
      }
    }

