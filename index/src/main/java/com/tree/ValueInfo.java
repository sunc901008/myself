package com.tree;

import com.commons.Commons;
import io.vertx.core.json.JsonArray;
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
    private int type;
    private float score;

    public ValueInfo() {
        this(null, null, null, 0, 0);
    }

    private ValueInfo(String table, String column, String content, int type, float score) {
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
        json.put("type", Commons.columnType(this.type));
        json.put("score", this.score);
        return json;
    }

    JsonArray toArray() {
        JsonArray array = new JsonArray();
        array.add(this.table);
        array.add(this.column);
        array.add(this.content);
        array.add(this.type);
        array.add(this.score);
        return array;
    }

    static ValueInfo JsonObjectToTriNode(JsonObject json) {
        String table = json.getString("table", "");
        String column = json.getString("column", "");
        String content = json.getString("content", "");
        int type = json.getInteger("type", 0);
        float score = json.getFloat("score", 0f);
        return new ValueInfo(table, column, content, type, score);
    }

    static ValueInfo JsonArrayToTriNode(JsonArray json) {
        String table = json.getString(0);
        String column = json.getString(1);
        String content = json.getString(2);
        int type = json.getInteger(3);
        float score = json.getFloat(4);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
