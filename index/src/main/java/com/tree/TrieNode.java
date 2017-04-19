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
    long nodeId;

    //  父节点id
    long parentId;

    /**
     * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径为一个词,即叶子节点
     */
    int nodeState = 0;

    final List<Float> maxScore;

    TrieNode() {
        this("", -1);
    }

    TrieNode(String nodeName, long parentId) {
        this.next = new ArrayList<>();
        this.maxScore = new ArrayList<>();
        this.valueInfo = new ArrayList<>();
        this.nodeName = nodeName;
        this.nodeId = Commons.id;
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

    JsonArray toArray() {
        JsonArray array = new JsonArray();
        array.add(this.nodeName);
        array.add(this.nodeId);
        array.add(this.parentId);
        array.add(this.nodeState);
        array.add(this.maxScore);
        JsonArray tmp = new JsonArray();
        for (ValueInfo aValueInfo : this.valueInfo)
            tmp.add(aValueInfo.toArray());
        array.add(tmp);
        return array;
    }

    static TrieNode toTrieNode(String json) {
        return toTrieNode(new JsonArray(json));
    }

    private static TrieNode toTrieNode(JsonArray json) {
        TrieNode node = new TrieNode();
        node.nodeState = json.getInteger(3);
        node.nodeId = json.getLong(1);
        node.parentId = json.getLong(2);
        node.nodeName = json.getString(0);
        JsonArray score = json.getJsonArray(4);
        score.forEach(s -> node.maxScore.add((Float) s));
        JsonArray value = json.getJsonArray(5);
        value.forEach(v -> node.valueInfo.add(ValueInfo.JsonArrayToTriNode((JsonArray) v)));
        return node;
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

}