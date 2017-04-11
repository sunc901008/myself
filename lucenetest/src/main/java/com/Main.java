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

    private static final String indexPath = "f:/test/index";

    public static void main(String[] args) throws Exception {
//        build();
        search("teacher");
    }


    public static void build() throws Exception {

        List<String> set = new ArrayList<>();
        Date start = new Date();
        BufferedReader reader = new BufferedReader(new FileReader("f:/display.csv"));
        String line;
        while ((line = reader.readLine()) != null) {
            set.add(line);
        }
        reader.close();
        JsonArray list = new JsonArray(set);
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

}
