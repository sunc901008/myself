package com.tree;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */

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

    private static IndexTrie trie;

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

    public static long buildTrie(String table, String column, String file, String type) {
        JsonObject json = new JsonObject().put("table", table).put("column", column).put("type", type).put("path", dataPath + file);
        return buildTrie(json);
    }

    public static long buildTrie() {
        return buildTrie("users", "displayname","columnValue", "display.csv");
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

    public static List<TrieNode> getAllNodes() {
        return trie.getAllNodes();
    }

    //  index 持久化
    public static long store() {
        return store(indexBackup);
    }

    public static long store(String path) {
        Date start = new Date();
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for (TrieNode node : IndexTrieMain.getAllNodes()) {
                bw.write(node.toString() + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    //  从磁盘恢复index
    public static long restore() {
        return restore(indexBackup);
    }

    public static long restore(String path) {
        Date start = new Date();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            if ((line = reader.readLine()) != null) {
                JsonObject json = new JsonObject(line);
                IndexTrieMain.trie.root = TrieNode.JsonStringToTriNode(json);
            }
            while ((line = reader.readLine()) != null) {
                JsonObject json = new JsonObject(line);
                TrieNode node = TrieNode.JsonStringToTriNode(json);
                for (TrieNode trieNode : getAllNodes()) {
                    if (trieNode.nodeId.equals(node.parentId)) {
                        trieNode.next.add(node);
                        break;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        return end.getTime() - start.getTime();
    }

    public static void lock() {
        trie.setLock(true);
    }

    public static void unlock() {
        trie.setLock(false);
    }

}

