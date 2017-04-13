package com;

import com.tree.IndexTrieMain;
import com.tree.ValueInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */
public class Test {

    public static void main(String[] args) {
        String[] displayName = new String[]{"teach", "te", "hello", "world"};
        List<ValueInfo> list = new ArrayList<>();
        for (String content : displayName) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("displayName");
            valueInfo.setType("columnValue");
            valueInfo.setScore(10);
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        String[] name = new String[]{"sunc", "ni", "hello", "teacher"};
        for (String content : name) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("name");
            valueInfo.setType("columnValue");
            valueInfo.setScore(10);
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        String[] age = new String[]{"hello", "teasdfscher"};
        for (String content : age) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("age");
            valueInfo.setType("columnValue");
            valueInfo.setScore(10);
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        System.out.println(IndexTrieMain.buildTrie(list));
        System.out.println(IndexTrieMain.getAllWords());
        System.out.println(IndexTrieMain.search("hell", 3));
        System.out.println(IndexTrieMain.getAllWords());

    }

}
