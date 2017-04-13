package com.mysql;

import io.vertx.core.json.JsonObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataTransfer {

    public static List<JsonObject> resultSetToJsonObject(ResultSet rs) {
        if (rs == null)
            return null;
        List<JsonObject> list_json = new ArrayList<>();

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int column_size = meta.getColumnCount();
            List<String> column_list = new ArrayList<>();
            for (int i = 1; i <= column_size; i++) {
                column_list.add(meta.getColumnName(i));
            }
            while (rs.next()) {
                JsonObject json = new JsonObject();
                column_list.forEach(cl -> {
                    Object value = null;
                    try {
                        value = rs.getObject(cl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    json.put(cl, value);
                });
                list_json.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list_json;
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

}
