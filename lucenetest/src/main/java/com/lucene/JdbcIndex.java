package com.lucene;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * creator: sunc
 * date: 2017/3/30
 * description:
 */

public class JdbcIndex {

    public static void buildIndex(JsonObject json, String indexPath) throws Exception {
        JsonArray list = json.getJsonArray("content");
        String table = json.getString("table");
        String column = json.getString("column");
        Date start = new Date();
//        Directory dir = FSDirectory.open(Paths.get(indexPath));
        MMapDirectory dir = new MMapDirectory(Paths.get(indexPath));
//        RAMDirectory dir = new RAMDirectory(new SimpleFSDirectory(Paths.get(indexPath)), new IOContext());
        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        StringField tableField = new StringField("table", table, Field.Store.YES);
        StringField columnField = new StringField("column", column, Field.Store.YES);
        StringField longPoint = new StringField("count", "1", Field.Store.YES);
        TextField textField = new TextField("contents", "", Field.Store.YES);

        for (int i = 0; i < list.size(); i++) {
            String str = list.getString(i);
            int index = i + 1;
            if (index % 10000 == 0) {
                System.out.println("build index : " + index);
            }
            Document doc = new Document();

            doc.add(tableField);
            doc.add(columnField);
            doc.add(longPoint);
            textField.setStringValue(str);
            doc.add(textField);
            writer.addDocument(doc);
        }

        writer.close();
        Date end = new Date();
        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
    }

    /**
     * 获取分词结果
     *
     * @param file
     * @return 分词结果
     */
    public static List<String> getWords(String file) {
        Analyzer analyzer = new StandardAnalyzer();
        List<String> result = new ArrayList<>();
        TokenStream stream = null;
        try {

            StringReader str = new StringReader(file);
            stream = analyzer.tokenStream("content", new BufferedReader(str));
//            InputStream inputStream = Files.newInputStream(Paths.get(file));
//            stream = analyzer.tokenStream("content", new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
            CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                result.add(attr.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}