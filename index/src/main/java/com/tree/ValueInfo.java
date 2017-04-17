package com.tree;

import io.vertx.core.json.JsonObject;

/**
 * creator: sunc
 * date: 2017/4/12
 * description:
 */
public class ValueInfo {

    private String table;
    private String column;
    private String content;
    private String type;
    private float score;

    public ValueInfo() {
        this(null, null, null, null, 0);
    }

    private ValueInfo(String table, String column, String content, String type, float score) {
        this.table = table;
        this.column = column;
        this.content = content;
        this.type = type;
        this.score = score;
    }

    JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.put("table", this.table);
        json.put("column", this.column);
        json.put("content", this.content);
        json.put("type", this.type);
        json.put("score", this.score);
        return json;
    }

    static ValueInfo JsonObjectToTriNode(JsonObject json) {
        String table = json.getString("table", "");
        String column = json.getString("column", "");
        String content = json.getString("content", "");
        String type = json.getString("type", "");
        float score = json.getFloat("score", 0f);
        return new ValueInfo(table, column, content, type, score);
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getTable() {
        return this.table;
    }

    public String getColumn() {
        return this.column;
    }

    public String getContent() {
        return this.content;
    }

    public float getScore() {
        return this.score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
