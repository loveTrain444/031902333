package com.zwj;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Recourse {
    //获取部首拆分文件
    //由于打成jar包后无法获取指定路径下的文件，所以需要用获取流的方法来获取文件
    public List<String>  getResource() {
        //返回读取指定资源的输入流
        List<String> breakList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/拆分词库.txt"), StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                    breakList.add(s);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return breakList;
    }
}
