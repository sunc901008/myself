package com;

import com.tree.IndexTrieMain;
import com.tree.TrieNode;
import com.tree.ValueInfo;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */
public class Test {


    private static final String file = "f:/display1.csv";
    private static final String word = "teacherrayl";

    public static void main(String[] args) throws Exception {
//        test40();
//        test41();
//        createFile();
        test20();
//        test21();

    }

    public static void test20() {
        JsonObject json = new JsonObject().put("table", "tags").put("column", "name").put("type", "columnValue").put("path", file);
        System.out.println(IndexTrieMain.buildTrie(json));
        Date start = new Date();
        List<TrieNode> list = IndexTrieMain.getAllNodesBFS();
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        System.out.println(list.size() + ":" + time);
        System.out.println(IndexTrieMain.search(word, 10));
        System.out.println(IndexTrieMain.store("g:/indexBackup"));
    }

    public static void test21() {
        System.out.println(IndexTrieMain.restore("g:/indexBackup"));
        Date start = new Date();
        List<TrieNode> list = IndexTrieMain.getAllNodesBFS();
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        System.out.println(list.size() + ":" + time);
        System.out.println(IndexTrieMain.search(word, 10));
    }

    public static void test40() {
        System.out.println(IndexTrieMain.buildTrie(create()));
        Date start = new Date();
        List<TrieNode> list = IndexTrieMain.getAllNodesBFS();
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        System.out.println(list.size() + ":" + time);
        list.forEach(v -> System.out.println(v.toString()));
        System.out.println(IndexTrieMain.search("abc", 10));
        System.out.println(IndexTrieMain.store("g:/indexBackup"));
    }

    public static void test41() {
        System.out.println(IndexTrieMain.restore("g:/indexBackup"));
        Date start = new Date();
        List<TrieNode> list = IndexTrieMain.getAllNodesBFS();
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        System.out.println(list.size() + ":" + time);
        list.forEach(v -> System.out.println(v.toString()));
        System.out.println(IndexTrieMain.search("abc", 10));
    }

    public static void createFile() {
        String[] chars = {"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "g", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "0", "1", "2", "3",
                "4", "5", "6", "7", "8", "9"};
        try {
            FileWriter fw = new FileWriter(new File(file));
            int count = 0;
            while (count < 100000) {
                int len = new Float(Math.random() * 5 + 5).intValue();// 5<=  <10
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    int index = new Float(Math.random() * chars.length).intValue();
                    str.append(chars[index]);
                }
                fw.write(str.toString() + "\n");
                count++;
                if (count % 10000 == 0)
                    System.out.println("create number : " + count);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<ValueInfo> create() {
        String[] displayName = new String[]{"abcd"};
        List<ValueInfo> list = new ArrayList<>();
        for (String content : displayName) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("table");
            valueInfo.setColumn("name");
            valueInfo.setType("type");
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
//        String[] name = new String[]{"ab"};
//        for (String content : name) {
//            ValueInfo valueInfo = new ValueInfo();
//            valueInfo.setTable("users");
//            valueInfo.setColumn("name");
//            valueInfo.setType("columnValue");
//            valueInfo.setContent(content);
//            list.add(valueInfo);
//        }
        return list;
    }

    public static double fun(double number, double root) {
        if (number == 1) {
            return 1;
        } else {
            return Math.pow(root, number) + fun(number - 1, root);
        }
    }

}
