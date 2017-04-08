package com.mysql;

import io.vertx.core.json.JsonArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcController {

    public static JsonArray query(String sql) {
        JsonArray list = new JsonArray();
        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()){
                list.add(rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
