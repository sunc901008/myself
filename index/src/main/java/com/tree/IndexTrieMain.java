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

    static long buildTrie(String table, String column, int type, String file) {
        JsonObject json = new JsonObject().put("table", table).put("column", column).put("type", type).put("path", dataPath + file);
        return buildTrie(json);
    }

    public static long buildTrie(JsonObject json) {
        Date start = new Date();
        List<ValueInfo> list = new ArrayList<>();
        String path = json.getString("path");
        String table = json.getString("table");
        String column = json.getString("column");
        int type = json.getInteger("type");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                ValueInfo valueInfo = new ValueInfo();
                valueInfo.setTable(table);
                valueInfo.setColumn(column);
                valueInfo.setType(type);
                valueInfo.setContent(line);
                list.add(valueInfo);
                i++;
                if (list.size() >= Commons.LIMIT_SIZE_MAX) {
                    buildTrie(list);
                    System.out.println(i + " words have been inserted.");
                    list.clear();
                }
            }
            if (list.size() > 0) {
                buildTrie(list);
            }
            System.out.println("all words have been inserted. inserted words count: " + i);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    private static void buildTrie(List<ValueInfo> list) {
        lock(Commons.INSERT);
        for (ValueInfo value : list) {
            trie.addWord(value);
        }
        unlock();
    }

    public static JsonObject search(String word, int count) {
        if (trie.getLock() != Commons.UNLOCK) {
            return new JsonObject().put("result", trie.getLock()).put("state", "false");
        }
        Date start = new Date();
        List<ValueInfo> list = trie.prefixSearchWordRewrite(word, count);
        List<JsonObject> result = new ArrayList<>();
        for (ValueInfo v : list) {
            result.add(v.toJsonObject());
        }
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        return new JsonObject().put("time", time).put("count", result.size()).put("result", result);
    }

    public static List<String> getAllWords() {
        return trie.getAllWords();
    }

//    public static List<TrieNode> getAllNodesDFS() {
//        return trie.getAllNodesDFS();
//    }

    public static List<TrieNode> getAllNodesBFS() {
        return trie.getAllNodesBFS();
    }

    //  index 持久化
    public static long store() {
        return store(indexBackup);
    }

    public static long store(String path) {
        return trie.store(path);
    }

    //  从磁盘恢复index
    public static long restore() {
        return restore(indexBackup);
    }

    public static long restore(String path) {
        Date start = new Date();
        lock(Commons.RESTORE);
        trie.restore(path);
        unlock();
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    private static void lock(int lock) {
        trie.setLock(lock);
    }

    private static void unlock() {
        trie.setLock(Commons.UNLOCK);
    }

}

