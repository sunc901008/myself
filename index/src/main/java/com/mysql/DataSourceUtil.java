package com.mysql;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.util.Properties;

/**
 * The Class DataSourceUtil.
 */
public class DataSourceUtil {

    public static String confile = "conf/druid.properties";
    public static Properties p = null;
    public static DataSource dataSource = null;

    static {
        p = new Properties();
        InputStream inputStream = null;
        try {
            File file = new File(confile);
            if (!file.exists()) {
                confile = System.getProperty("user.dir") + "/src/main/resources/" + confile;
                file = new File(confile);
            }

            inputStream = new BufferedInputStream(new FileInputStream(file));
            p.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(p);
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

    /**
     * 获取数据源
     */
    public static final Connection getConnection() throws Exception {
        dataSource = dataSource == null ? DruidDataSourceFactory.createDataSource(p) : dataSource;
        return dataSource.getConnection();
    }
}
