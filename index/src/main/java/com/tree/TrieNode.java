package com.tree;

import com.commons.Commons;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

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
    public String nodeId;

    //  父节点id
    String parentId;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    public TrieNode() {
        this("", "");
    }

    TrieNode(String nodeName, String parentId) {
        this.next = new ArrayList<>();
        this.maxScore = new ArrayList<>();
        this.valueInfo = new ArrayList<>();
        this.nodeName = nodeName;
        this.nodeId = Commons.id + "";
        this.parentId = parentId;
        Commons.id--;
    }

    JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.put("nodeState", this.nodeState);
        json.put("maxScore", this.maxScore);
        JsonArray jsonArray = new JsonArray();
        this.valueInfo.forEach(v -> jsonArray.add(v.toJsonObject()));
        json.put("valueInfo", jsonArray);
        json.put("nodeName", this.nodeName);
        json.put("nodeId", this.nodeId);
        json.put("parentId", this.parentId);
        return json;
    }

    static TrieNode toTrieNode(JsonObject json) {
        TrieNode node = new TrieNode();
        node.nodeState = json.getInteger("nodeState");
        node.nodeId = json.getString("nodeId");
        node.parentId = json.getString("parentId");
        node.nodeName = json.getString("nodeName");
        JsonArray score = json.getJsonArray("maxScore");
        score.forEach(s -> node.maxScore.add((Float) s));

        JsonArray value = json.getJsonArray("valueInfo");
        value.forEach(v -> node.valueInfo.add(ValueInfo.JsonObjectToTriNode((JsonObject) v)));
        return node;
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

}