package com.zwj;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import java.util.*;
//使用AC自动机算法
public class TireTree {
    private static class AcNode {
        String originKey;
        //孩子节点用HashMap存储，能够在O(1)的时间内查找到，效率高
        Map<String,AcNode> children=new HashMap<>();
        AcNode failNode;
        //设置敏感词长度变量，如果不为零说明该节点为敏感词的结束，可以根据其值往前找敏感词的开始节点
        int wordLength;
    }

    private final AcNode root;
    public TireTree(List<String> list) {
        root = new AcNode();
        createTree(list);
    }
    //插入节点
    private   void insert(Map.Entry<String,String> entry){
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
                //在孩子节点里存放原词，便于最后的时候查阅
                //增加原词信息时保留最长的信息
                if( cur.children.get(sub).originKey==null||cur.children.get(sub).originKey.length() > ori.length()){
                    cur.children.get(sub).originKey = ori;
                }
            }
            //temp指向孩子节点
            cur=cur.children.get(sub);
            len++;
        }
        cur.wordLength = len;//一个字符串遍历完了后，将其长度保存到最后一个孩子节点信息中
        cur.originKey = ori;//将原词信息保存到最后一个孩子节点信息中
    }
    private   void createTree( List<String> list){
        //创建敏感词字典
        HashMap<String, String> dictionaryOfKeyword = Words.createDictionaryOfKeyword(list);
        //根据敏感词字典将敏感词库建成树
        for(Map.Entry<String,String> entry: dictionaryOfKeyword.entrySet()){
            insert(entry);
        }
        buildFailPath();
    }
    //建立fail指针
    private  void buildFailPath(){
        //指定第一层的fail指针,并且让第一层的节点入队，方便BFS
        try{
        Queue<AcNode> queue = new LinkedList<>();
        Map<String,AcNode> children = root.children;
        for (Map.Entry<String, AcNode> next : children.entrySet()) {
            String[] spelling;
            spelling = PinyinHelper.toHanyuPinyinStringArray(next.getKey().charAt(0), Words.format);
            queue.offer(next.getValue());
            if(spelling==null){
                //是英文的话他的fail就是根
                next.getValue().failNode = root;
                    }
                else {//是中文就查询它的兄弟中是否有它的拼音，有fail就指向它；没有就指向根
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
                String[] spelling  = PinyinHelper.toHanyuPinyinStringArray(next.getKey().charAt(0), Words.format);
                //如果 failOfParent节点没有与 当前节点父节点具有相同的转移路径，
                // 则继续获取 failOfParent 节点的失败指针指向的节点，将其赋值给 failOfParent
                while (failOfParent != null && (!failOfParent.children.containsKey(next.getKey()))) {
                    failOfParent = failOfParent.failNode;
                }
                boolean flag=false;
                //如果是，汉字判断其兄弟节点有无与当前节点相等的值，若有则将当前节点的fail连道该兄弟节点上
                if(spelling!=null){
                    if(x.children.containsKey(spelling[0])){
                        //是中文就查询它的兄弟中是否有它的拼音，有fail就指向它
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
                 // 如果当前节点的fail节点的保存字符串的长度比当前节点长，则把信息存储到当前节点
                if (y.failNode.wordLength>y.wordLength){
                    y.wordLength = y.failNode.wordLength;
                }
                queue.offer(y);//把当前孩子节点入队
            }
        }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
    }

    private   ArrayList<String> query(String s, int line){
        ArrayList<String> resultSet = new ArrayList<>();
        AcNode cur = root;
        int start=-1;
        boolean isRemake = false;
        //待检测字段，可能是字符也可能是拼音
        String str;
        for (int i = 0; i < s.length(); i++) {
            //如果是非法字符直接跳过
            if (Words.isIllegal(s.charAt(i))){
                continue;
            }
            AcNode tamp = cur;//保存回溯时需要的信息
            str = String.valueOf(s.charAt(i)).toLowerCase(Locale.ROOT);
            try {
                String[] spelling = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0), Words.format);
                    if(isRemake) {
                        //如果本次查询是重查，则查询其拼音
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
                    //如果重查还查不到则跳出循环
                    if (isRemake){
                        isRemake = false;
                        continue;
                    }
                        else if (spelling!=null){//如果是汉字且查不到，则说明其不是部首，启动回溯，查其谐音
                         isRemake=true;
                         //回溯
                         cur=tamp;
                         i--;
                         continue;
                        }//其余情况没查到直接跳出循环
                     isRemake = false;
                     continue;
                }
                //如果检索到当前节点的长度信息存在，则代表搜索到了敏感词，存入结果集
                 if (cur.wordLength!=0){
                    int startIndex=handleMatchWords(cur,s,i,line,resultSet);
                    //如果两个结果开始索引一样，取后来的结果
                    if(startIndex==start&&startIndex!=-1){
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
    private  int handleMatchWords(AcNode node, String text, int currentPos, int line, ArrayList<String> resultSet) {
            int startIndex;
            StringBuilder ans = new StringBuilder();
            int pos = currentPos;
            int cnt = node.wordLength;
            //从当前坐标开始往回找敏感词
            while(cnt>0){
                if(!Words.isIllegal(text.charAt(pos))){
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
            //如果c查询结果是中文且含有数字，则查询失败
            if(!Words.isNotContainChinese(node.originKey)){
                for(int i=0;i<ans.length();i++){
                    if(ans.charAt(i)>'0'&&ans.charAt(i)<'9'){
                        return -1;
                    }
                }
            }
            resultSet.add("Line"+line+": "+"<"+node.originKey+"> " +ans);
            return startIndex;
      }
      //供main使用的获取结果集函数，封装了对query的调用
      public ArrayList<String> getResultList(List<String> textList){
        int line = 0;
        ArrayList<String> resultList = new ArrayList<>();
        resultList.add("");
          for(String str :textList){
              line++;
              resultList.addAll(query(str, line));
          }
          resultList.set(0,"total: "+(resultList.size()-1));
        return resultList;
      }
}

