package com.tree;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */

import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndexTrieMain {
    private static final String dataPath = "/srv/focus/index-data/";

    private static IndexTrie trie;

    static {
        trie = new IndexTrie();
    }

    public static long buildTrie() {
        JsonObject json = new JsonObject().put("table", "users").put("column", "displayname").put("path", "display.csv");
        return buildTrie(json);
    }

    public static long buildTrie(JsonObject json) {
        List<ValueInfo> list = new ArrayList<>();
        String path = dataPath + json.getString("path");
        String table = json.getString("table");
        String column = json.getString("column");
        String type = json.getString("type", "columnValue");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                if (count % 10000 == 0)
                    System.out.println("count : " + count);
                count++;
                ValueInfo valueInfo = new ValueInfo();
                valueInfo.setTable(table);
                valueInfo.setColumn(column);
                valueInfo.setType(type);
                valueInfo.setContent(line);
                list.add(valueInfo);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildTrie(list);
    }

    public static long buildTrie(List<ValueInfo> list) {
        Date start = new Date();
        lock();
        for (ValueInfo valueInfo : list) {
            trie.addWord(valueInfo);
        }
        unlock();
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    public static JsonObject search(String word, int count) {
        if (trie.isLock()) {
            return new JsonObject().put("result", new ArrayList<String>()).put("state", "false");
        }
        Date start = new Date();
        List<ValueInfo> list = trie.prefixSearchWordRewrite(word, count);
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        return new JsonObject().put("time", time).put("count", list.size()).put("result", list);
    }

    public static List<String> getAllWords() {
        return trie.getAllWords();
    }

    public static void lock() {
        trie.setLock(true);
    }

    public static void unlock() {
        trie.setLock(false);
    }

}

