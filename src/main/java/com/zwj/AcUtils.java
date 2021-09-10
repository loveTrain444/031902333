package com.zwj;

import javax.sound.sampled.Line;
import java.util.*;

public class AcUtils {
    static class AcNode
    {
        //孩子节点用HashMap存储，能够在O(1)的时间内查找到，效率高
        Map<Character,AcNode> children=new HashMap<>();
        AcNode failNode;
        //使用set集合存储字符长度，防止敏感字符重复导致集合内数据重复
        Set<Integer> wordLengthList = new HashSet<>();
    }
    public static AcNode getRoot(){
        return new AcNode();
    }
    public static void insert(AcNode root,String s){
        AcNode cur=root;
        char[] chars=s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (!cur.children.containsKey(chars[i])){ //如果不包含这个字符就创建孩子节点
                cur.children.put(chars[i], new AcNode());
            }
            cur = cur.children.get(chars[i]);//temp指向孩子节点
        }
        cur.wordLengthList.add(s.length());//一个字符串遍历完了后，将其长度保存到最后一个孩子节点信息中
    }
    public static void creatKeyWords(AcNode root,List<String> list){
        for(String keyWords:list){
            insert(root,keyWords);
        }
        buildFailPath(root);
    }
    public static void buildFailPath(AcNode root){
        //第一层的fail指针指向root,并且让第一层的节点入队，方便BFS
        Queue<AcNode> queue = new LinkedList<>();
        Map<Character,AcNode> children = root.children;
        for (Map.Entry<Character, AcNode> next : children.entrySet()) {
            queue.offer(next.getValue());
            next.getValue().failNode = root;
        }
        //构建剩余层数节点的fail指针,利用层次遍历
        while(!queue.isEmpty()){
            AcNode x=queue.poll();
            children=x.children; //取出当前节点的所有孩子
            Iterator<Map.Entry<Character, AcNode>> iterator = children.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<Character, AcNode> next = (Map.Entry<Character, AcNode>) iterator.next();
                AcNode y=next.getValue();  //得到当前某个孩子节点
                AcNode faFail=x.failNode;  //得到孩子节点的父节点的fail节点
                //如果 faFail节点没有与 当前节点父节点具有相同的转移路径，则继续获取 fafail 节点的失败指针指向的节点，将其赋值给 fafail
                while(faFail!=null&&(!faFail.children.containsKey(next.getKey()))){
                    faFail=faFail.failNode;
                }
                //回溯到了root节点，只有root节点的fail才为null
                if (faFail==null){
                    y.failNode=root;
                }
                else {
                    //fafail节点有与当前节点父节点具有相同的转移路径，则把当前孩子节点的fail指向fafail节点的孩子节点
                    y.failNode=faFail.children.get(next.getKey());
                }
                //如果当前节点的fail节点有保存字符串的长度信息，则把信息存储合并到当前节点
                if (y.failNode.wordLengthList!=null){
                    y.wordLengthList.addAll(y.failNode.wordLengthList);
                }
                queue.offer(y);//最后别忘了把当前孩子节点入队
            }
        }

    }
    public static boolean isIllegal(char c){
       String str = "[\"`~!@#$%^&*()+=|{}':;',\\.<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
       if(str.contains(String.valueOf(c))){
           return true;
       }
         else return false;
    }
    public static void query(AcNode root,String s,int line){
        AcNode temp = root;
        char[] c=s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            //如果这个字符在当前节点的孩子里面没有或者当前节点的fail指针不为空，就有可能通过fail指针找到这个字符
            //所以就一直向上更换temp节点
            if (isIllegal(c[i])){
                continue;
            }
            while(temp.children.get(c[i])==null&&temp.failNode!=null){
                temp=temp.failNode;
            }
            //如果因为当前节点的孩子节点有这个字符，则将temp替换为下面的孩子节点
            if (temp.children.get(c[i])!=null){
                temp=temp.children.get(c[i]);
            }
            //如果temp的failnode为空，代表temp为root节点，没有在树中找到符合的敏感字，故跳出循环，检索下个字符
            else{
                continue;
            }
            //如果检索到当前节点的长度信息存在，则代表搜索到了敏感词，打印输出即可
            if (temp.wordLengthList.size()!=0){
                handleMatchWords(temp,s,i, line);
            }
        }
    }

    //利用节点存储的字符长度信息，打印输出敏感词及其在搜索串内的坐标
    public static void handleMatchWords(AcNode node, String text, int currentPos,int line)
    {
        for (Integer wordLen : node.wordLengthList)
        {
           /* int startIndex = currentPos - wordLen + 1;
            String matchWord = text.substring(startIndex, currentPos + 1);*/
            StringBuilder ans = new StringBuilder();
            int pos = currentPos;
            int cnt = wordLen;
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
            ans.reverse();
            System.out.println("line"+line+" ："+ans);
        }
    }


}
