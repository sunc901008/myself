package com;

import com.tree.IndexTrieMain;
import com.tree.TrieNode;
import com.tree.ValueInfo;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */
public class Test {

    public static void main(String[] args) {

        test3();

    }

    public static List<ValueInfo> create() {
        String[] displayName = new String[]{"abc", "ab"};
        List<ValueInfo> list = new ArrayList<>();
        for (String content : displayName) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("displayName");
            valueInfo.setType("columnValue");
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        String[] name = new String[]{"abc"};
        for (String content : name) {
            ValueInfo valueInfo = new ValueInfo();
            valueInfo.setTable("users");
            valueInfo.setColumn("name");
            valueInfo.setType("columnValue");
            valueInfo.setContent(content);
            list.add(valueInfo);
        }
        return list;
    }

    public static void test1() {

        System.out.println(IndexTrieMain.buildTrie(create()));
        System.out.println(IndexTrieMain.getAllWords());
        List<TrieNode> nodes = IndexTrieMain.getAllNodes();
        nodes.forEach(v -> System.out.println(v.toString()));
        System.out.println(IndexTrieMain.search("ab", 10));
    }

    public static void test3() {
        System.out.println(IndexTrieMain.restore("g:/indexBackup"));
        System.out.println(IndexTrieMain.search("architecture", 10));
    }


    public static void test2() {
        JsonObject json = new JsonObject().put("table", "tags").put("column", "name").put("type", "columnValue").put("path", "f:/tagname.csv");
        System.out.println(IndexTrieMain.buildTrie(json));
        System.out.println(IndexTrieMain.store("g:/indexBackup"));
    }


}
