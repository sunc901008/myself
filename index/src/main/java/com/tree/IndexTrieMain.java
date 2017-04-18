package com.tree;

import com.commons.Commons;
import io.vertx.core.json.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class IndexTrieMain {
    private static String config = "conf/direction.properties";

    private static String dataPath = "/srv/focus/index-data/";

    private static String indexBackup = "indexBackup";

    private static final IndexTrie trie;

    static {
        trie = new IndexTrie();

        InputStream inputStream = null;
        try {
            Properties p = new Properties();
            File file = new File(config);
            if (!file.exists()) {
                config = System.getProperty("user.dir") + "/src/main/resources/" + config;
                file = new File(config);
            }
            inputStream = new BufferedInputStream(new FileInputStream(file));
            p.load(inputStream);
            dataPath = p.getProperty("index-data");
            indexBackup = dataPath + indexBackup;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static long buildTrie(String table, String column, String file, String type) {
        JsonObject json = new JsonObject().put("table", table).put("column", column).put("type", type).put("path", dataPath + file);
        return buildTrie(json);
    }

    public static long buildTrie(JsonObject json) {
        List<ValueInfo> list = new ArrayList<>();
        String path = json.getString("path");
        String table = json.getString("table");
        String column = json.getString("column");
        String type = json.getString("type");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
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
        lock(Commons.INSERT);
        int i = 1;
        System.out.println("all node count : " + list.size());

        for (ValueInfo value : list) {
            trie.addWord(value);
            if (i % 10000 == 0) {
                System.out.println("has add node count : " + i);
            }
            i++;
        }

        unlock();
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    public static JsonObject search(String word, int count) {
        if (trie.getLock() != Commons.UNLOCK) {
            return new JsonObject().put("result", trie.getLock()).put("state", "false");
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

    public static List<TrieNode> getAllNodesDFS() {
        return trie.getAllNodesDFS();
    }

    public static List<TrieNode> getAllNodesBFS() {
        return trie.getAllNodesBFS();
    }

    //  index 持久化
    public static long store() {
        return store(indexBackup);
    }

    public static long store(String path) {
        return TrieStore.store(path);
    }

    //  从磁盘恢复index
    public static long restore() {
        return restore(indexBackup);
    }

    public static long restore(String path) {
        Date start = new Date();
        lock(Commons.INSERT);
        trie.root = TrieStore.restore(path);
        unlock();
        Date end1 = new Date();
        return end1.getTime() - start.getTime();
    }

    private static void lock(int lock) {
        trie.setLock(lock);
    }

    private static void unlock() {
        trie.setLock(Commons.UNLOCK);
    }

}

