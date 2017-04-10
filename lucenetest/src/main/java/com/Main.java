package com;

import com.lucene.Index;
import com.lucene.JdbcIndex;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * creator: sunc
 * date: 2017/3/30
 * description:
 */
public class Main {

    private static final String indexPath = "f:/test/test";

    public static void main(String[] args) throws Exception {
//        build();
//        buildIndex();
        search("you");
//        search("teacher");
//        updateIndex("java");
//        priority();
//        梁宇腾
//        wordsIndex();
    }


    public static void build() throws Exception {

//        Date start = new Date();
//        JsonArray list = JdbcController.query("select distinct DisplayName from users");
//        Date end = new Date();
//        System.out.println("get datas from mysql : " + (end.getTime() - start.getTime()) + " total milliseconds");

        Set<String> set = new HashSet<>();
        Date start = new Date();
        BufferedReader reader = new BufferedReader(new FileReader("f:/test.csv"));
        String line;
        while ((line = reader.readLine()) != null) {
            set.add(line);
        }
        reader.close();
        JsonArray list = new JsonArray(new ArrayList<>(set));
        Date end = new Date();
        System.out.println("get datas from file : " + (end.getTime() - start.getTime()) + " total milliseconds");

        System.out.println("list size :" + list.size());

        JsonObject json = new JsonObject();
        json.put("table", "tags");
        json.put("column", "tagname");
        json.put("content", list);

        JdbcIndex.buildIndex(json, indexPath);
    }

    public static void search(String query) throws Exception {
//        Community
        Index index = new Index();
//        index.updateIndex(query,indexPath,"tags","tagname");
        List<JsonObject> list = index.searchIndex(query, indexPath);

        for (JsonObject json : list) {
            System.out.println("-----------------");
            System.out.println("doc: " + json.getValue("doc"));
//            System.out.println("table: " + json.getValue("table"));
//            System.out.println("column: " + json.getValue("column"));
//            System.out.println("count: " + json.getValue("count"));
            System.out.println("contents: " + json.getValue("contents"));
            System.out.println("score:" + json.getValue("score"));
        }

    }


    public static void priority() throws Exception {
        PriorityQueue <Integer> p = new PriorityQueue<>();
        p.add(12);
        p.add(2);
        p.add(5);
        p.add(6);

        for (int i = 3; i > 0; i--) {
        }

        p.forEach(System.out::println);
    }

    public static void wordsIndex() throws Exception {
        List<String> list = JdbcIndex.getWords("f:/displayname.csv");
        System.out.println(list.size());
        for (int i = 0; i < 50; i++) {
            System.out.println(list.get(i));
        }
    }

}
