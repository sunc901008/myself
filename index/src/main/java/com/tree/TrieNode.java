package com.tree;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * creator: sunc
 * date: 2017/4/13
 * description:
 */

public class TrieNode {

    final List<TrieNode> next;

    final List<ValueInfo> valueInfo;

    String nodeName;

    //  节点id
    String nodeId;
    //  父节点id
    String parentId;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    public TrieNode() {
        next = new ArrayList<>();
        nodeState = 0;
        maxScore = new ArrayList<>();
        valueInfo = new ArrayList<>();
        nodeName = "";
        nodeId = UUID.randomUUID().toString().replaceAll("-", "");
        parentId = "";
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.put("nodeState", this.nodeState);
        JsonArray score = new JsonArray();
        this.maxScore.forEach(score::add);
        json.put("maxScore", score);
        JsonArray value = new JsonArray();
        this.valueInfo.forEach(v -> value.add(v.toString()));
        json.put("valueInfo", value);
        json.put("nodeName", this.nodeName);
        json.put("nodeId", this.nodeId);
        json.put("parentId", this.parentId);
        return json.toString();
    }

    public static TrieNode JsonStringToTriNode(JsonObject json) {
        TrieNode node = new TrieNode();
        node.nodeState = json.getInteger("nodeState", 0);
        JsonArray score = json.getJsonArray("maxScore", new JsonArray());
        score.forEach(s -> node.maxScore.add((Float) s));
        JsonArray value = json.getJsonArray("valueInfo", new JsonArray());
        value.forEach(v -> node.valueInfo.add(ValueInfo.JsonStringToTriNode(v.toString())));
        node.nodeName = json.getString("nodeName", "");
        node.nodeId = json.getString("nodeId", "");
        node.parentId = json.getString("parentId", "");
        return node;
    }

}