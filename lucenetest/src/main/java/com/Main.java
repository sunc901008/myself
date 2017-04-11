package com;

import com.lucene.Index;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/3/30
 * description:
 */
public class Main {

    private static final String dataPath = "g:/lucene/display-data/display.csv";


    public static void build() throws Exception {
        List<String> set = new ArrayList<>();
        Date start = new Date();
        BufferedReader reader = new BufferedReader(new FileReader(dataPath));
        String line;
        while ((line = reader.readLine()) != null) {
            set.add(line);
        }
        reader.close();
        JsonArray list = new JsonArray(set);
        Date end = new Date();
        System.out.println("get data from file : " + (end.getTime() - start.getTime()) + " total milliseconds");

        System.out.println("list size :" + list.size());

        JsonObject json = new JsonObject();
        json.put("table", "users");
        json.put("column", "DisplayName");
        json.put("content", list);

        Index.buildIndex(json);
    }

    public static void search(String query, int limit) throws Exception {
        JsonObject jsonObject = Index.searchIndex(query, limit);
        List<JsonObject> list = (List<JsonObject>) jsonObject.getValue("result");
        System.out.println("time-consuming: " + jsonObject.getLong("time"));
        for (JsonObject json : list) {
            System.out.println("-----------------");
            System.out.println("doc: " + json.getValue("doc"));
            System.out.println("table: " + json.getValue("table"));
            System.out.println("column: " + json.getValue("column"));
            System.out.println("count: " + json.getValue("count"));
            System.out.println("contents: " + json.getValue("contents"));
            System.out.println("score:" + json.getValue("score"));
        }

    }

}
