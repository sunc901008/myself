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
    private static final String dataPath = "g:/lucene/display-data/";

    private static IndexTrie trie;

    static {
        trie = new IndexTrie();
    }

    public static long buildTrie() {
        List<ValueInfo> list = new ArrayList<>();
        String path = dataPath + "display.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                if (count % 10000 == 0)
                    System.out.println("count : " + count);
                count++;
                ValueInfo valueInfo = new ValueInfo();
                valueInfo.setTable("users");
                valueInfo.setColumn("displayName");
                valueInfo.setType("columnValue");
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

