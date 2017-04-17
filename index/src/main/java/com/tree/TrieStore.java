package com.tree;

import com.commons.FileOperate;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.xerial.snappy.Snappy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/4/17
 * description:
 */
class TrieStore {

    static long store(TrieNode node, String path) {
        Date start = new Date();
        JsonObject json = nodeToJson(node);
        Date end = new Date();
        System.out.println("construct json : " + (end.getTime() - start.getTime()));

        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(Snappy.compress(json.toString()));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end1 = new Date();

        return end1.getTime() - start.getTime();
    }

    private static JsonObject nodeToJson(TrieNode node) {
        JsonObject json = new JsonObject();
        json.put("nodeName", node.nodeName);
        json.put("nodeId", node.nodeId);
        json.put("parentId", node.parentId);
        json.put("nodeState", node.nodeState);
        List<ValueInfo> valueInfo = node.valueInfo;
        JsonArray values = new JsonArray();
        valueInfo.forEach(v -> values.add(v.toJsonObject()));
        json.put("valueInfo", values);
        List<Float> maxScore = node.maxScore;
        JsonArray score = new JsonArray();
        maxScore.forEach(score::add);
        List<TrieNode> next = node.next;
        if (next.size() == 0) {
            json.put("next", next);
        } else {
            JsonArray jsonArray = new JsonArray();
            for (TrieNode trieNode : next) {
                jsonArray.add(nodeToJson(trieNode));
            }
            json.put("next", jsonArray);
        }
        return json;
    }


    static TrieNode restore(String path) {
        JsonObject json = null;
        try {
            json = new JsonObject(new String(Snappy.uncompress(FileOperate.toByteArray(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (json == null) {
            return new TrieNode();
        }
        return jsonToNode(json);
    }

    private static TrieNode jsonToNode(JsonObject json) {
        TrieNode node = new TrieNode();
        node.nodeName = json.getString("nodeName");
        node.nodeId = json.getString("nodeId");
        node.parentId = json.getString("parentId");
        node.nodeState = json.getInteger("nodeState");
        JsonArray value = json.getJsonArray("valueInfo", new JsonArray());
        value.forEach(v -> node.valueInfo.add(ValueInfo.JsonObjectToTriNode((JsonObject) v)));
        JsonArray score = json.getJsonArray("maxScore", new JsonArray());
        score.forEach(s -> node.maxScore.add((Float) s));
        JsonArray next = json.getJsonArray("next", new JsonArray());
        next.forEach(n -> node.next.add(jsonToNode((JsonObject) n)));
        return node;
    }

}
