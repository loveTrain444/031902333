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
            //读取文件里的拆分词库
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
        String originKey;
        //孩子节点用HashMap存储，能够在O(1)的时间内查找到，效率高
        Map<String,AcNode> children=new HashMap<>();
        AcNode failNode;
        //设置敏感词长度变量，如果不为零说明该节点为敏感词的结束，可以根据其值往前找敏感词的开始节点
        int wordLength;
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
    public static void insert(AcNode root,Map.Entry<String,String> entry){
        String ori = entry.getValue();
        String s = entry.getKey();
        AcNode cur=root;
        int len=0;
        for (int i = 0; i < s.length(); i++) {
            String sub;
            //将拼音转化成整个字符串装入node，通过{}标志判别
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
                if( cur.children.get(sub).originKey==null||cur.children.get(sub).originKey.length() > ori.length()){
                    cur.children.get(sub).originKey = ori;
                }
            }
            //temp指向孩子节点
            cur=cur.children.get(sub);
            len++;
        }
        cur.wordLength = len;//一个字符串遍历完了后，将其长度保存到最后一个孩子节点信息中
        cur.originKey = ori;
    }
    public static void creatKeyWords(AcNode root, List<String> list){
        for(String keyWord:list){
            String [][] matrix ;
            matrix = new String[keyWord.length()][];
            //获取敏感词的扩展矩阵
                for(int i=0;i<keyWord.length();i++){
                    try {
                        String key = String.valueOf(keyWord.charAt(i));
                        String[] Spelling ;
                        Spelling = PinyinHelper.toHanyuPinyinStringArray(keyWord.charAt(i),format);
                        //Spelling返回null代表该敏感词字符是不是汉字，则有一个分支；是汉字有五个分支
                        //比如功有五种情况：功，gong.{gong},g,工力
                        if(Spelling!=null){
                            matrix[i] = new String[5];
                            matrix[i][0] = key;
                            matrix[i][1] = Spelling[0];
                            matrix[i][2] = "{"+Spelling[0]+"}";
                            matrix[i][3] = String.valueOf(Spelling[0].charAt(0));
                            matrix[i][4] = dictionaryOfBreak.get(keyWord.charAt(i));

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
            List<String> result = new ArrayList<>();
            List<String> com = com(0, keyWord.length(), matrix, "", result);
        }
        //将敏感词库建成树
        for(Map.Entry<String,String> entry:dictionaryOfKeyword.entrySet()){
            insert(root,entry);
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
              for (int k=1;k<matrix[step].length;k++){
                  com(step+1,len,matrix,str+matrix[step][k],result);
              }
        }
        return result;
    }
    private static void buildFailPath(AcNode root){
        //第一层的fail指针指向root,并且让第一层的节点入队，方便BFS
        try{
        Queue<AcNode> queue = new LinkedList<>();
        Map<String,AcNode> children = root.children;
        for (Map.Entry<String, AcNode> next : children.entrySet()) {
            String[] spelling;
            spelling = PinyinHelper.toHanyuPinyinStringArray(next.getKey().charAt(0), format);
            queue.offer(next.getValue());
            if(spelling==null){
                next.getValue().failNode = root;
                    }
                else {
                next.getValue().failNode = root.children.getOrDefault(spelling[0], root);
             }
        }
        //构建剩余层数节点的fail指针,利用层次遍历
        while(!queue.isEmpty()){
            AcNode x=queue.poll();
            children=x.children; //取出当前节点的所有孩子
            for (Map.Entry<String, AcNode> next : children.entrySet()) {
                AcNode y = next.getValue();  //得到当前某个孩子节点
                AcNode failOfParent = x.failNode;  //得到孩子节点的父节点的fail节点
                String[] spelling  = PinyinHelper.toHanyuPinyinStringArray(next.getKey().charAt(0), format);
                //如果 failOfParent节点没有与 当前节点父节点具有相同的转移路径，
                // 则继续获取 failOfParent 节点的失败指针指向的节点，将其赋值给 failOfParent
                while (failOfParent != null && (!failOfParent.children.containsKey(next.getKey()))) {
                    failOfParent = failOfParent.failNode;
                }
                boolean flag=false;
                //如果是，汉字判断其兄弟节点有无与当前节点相等的值，若有则将当前节点的fail连道该兄弟节点上
                if(spelling!=null){
                    if(x.children.containsKey(spelling[0])){
                        y.failNode = x.children.get(spelling[0]);
                        flag=true;
                    }
                }
                //flag为false代表failOfParent可能是null，如果是null将当前节点的fail连到根节点；
                // 如果不是则将当前节点的fail连到failOfParent的孩子节点中与当前节点值相等的节点
                if(!flag){
                    if(failOfParent==null){
                        y.failNode = root;
                    }else y.failNode = failOfParent.children.get(next.getKey());
                }
                 // 如果当前节点的fail节点有保存字符串的长度信息，则把信息存储合并到当前节点
                  if (y.failNode.wordLength!=0){
                    y.wordLength = y.failNode.wordLength;
                }
                queue.offer(y);//把当前孩子节点入队
            }
        }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
    }

    private static boolean isIllegal(char c){
       String str = illegalString;
        return str.contains(String.valueOf(c));
    }
    public static ArrayList<String> query(AcNode root, String s, int line){
        ArrayList<String> resultSet = new ArrayList<>();
        AcNode cur = root;
        int start=-1;
        boolean isRemake = false;
        //待检测字段，可能是字符也可能是拼音
        String str;
        for (int i = 0; i < s.length(); i++) {
            AcNode tamp = cur;
            //如果是非法字符直接跳过
            if (isIllegal(s.charAt(i))){
                continue;
            }
            try {
                str = String.valueOf(s.charAt(i)).toLowerCase(Locale.ROOT);
                String[] spelling = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0), format);
                if(isRemake) {
                str = spelling[0];
                 }
                //如果这个字符在当前节点的孩子里面没有或者当前节点的fail指针不为空，就有可能通过fail指针找到这个字符
                //所以就一直向上更换temp节点
                while(cur.children.get(str)==null&&cur.failNode!=null){
                    cur=cur.failNode;
                }
                //如果因为当前节点的孩子节点有这个字符，则将cur替换为下面的孩子节点
                 if (cur.children.get(str)!=null){
                     isRemake = false;
                     cur=cur.children.get(str);
                 }
                //如果temp的failOfParent为空，代表cur为root节点，没有在树中找到符合的敏感字，故跳出循环，检索下个字符
                else{
                    if (isRemake){
                        isRemake = false;
                        continue;
                     }
                        else if (spelling!=null){
                         isRemake=true;
                         //回溯
                         cur=tamp;
                         i--;
                         continue;
                     }
                         isRemake = false;
                         continue;
                }
                //如果检索到当前节点的长度信息存在，则代表搜索到了敏感词，存入结果集
                 if (cur.wordLength!=0){
                    int startIndex=handleMatchWords(cur,s,i,line,resultSet);
                    //如果两个结果开始索引一样，取后来的结果
                    if(startIndex==start){
                        resultSet.remove(resultSet.size()-2);
                    }
                     start=startIndex;
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
            StringBuilder ans = new StringBuilder();
            int pos = currentPos;
            int cnt = node.wordLength;
            while(cnt>0){
                if(!isIllegal(text.charAt(pos))){
                    ans.append(text.charAt(pos));
                    cnt--;
                }
                   else{
                       ans.append(text.charAt(pos));
                }
                   pos--;
            }
            startIndex=pos+1;
            ans.reverse();
            resultSet.add("Line"+line+": "+"<"+node.originKey+"> " +ans);
            return startIndex;
      }
    }

