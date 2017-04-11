package com.lucene;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * creator: sunc
 * date: 2017/3/30
 * description:
 */

public class Index {

    private static final String indexPath = "g:/lucene/display-index";
    private static IndexReader reader;
    private static MyIndexSearcher searcher;
    static {
        try {
            reader = DirectoryReader.open(new MMapDirectory(Paths.get(indexPath)));
            searcher = new MyIndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject searchIndex(String queries, int hitsPerPage) throws Exception {
        Date start = new Date();

//        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//        IndexReader reader = DirectoryReader.open(new MMapDirectory(Paths.get(indexPath)));
//        IndexReader reader = DirectoryReader.open(new RAMDirectory(new SimpleFSDirectory(Paths.get(indexPath)), new IOContext()));
//        MyIndexSearcher searcher = new MyIndexSearcher(reader);
        Term term = new Term("contents", queries.toLowerCase());
        PrefixQuery query = new PrefixQuery(term);

        MyCustomScoreQuery myCustomScoreQuery = new MyCustomScoreQuery(query, queries, searcher);

        TopDocs results = searcher.search(myCustomScoreQuery, hitsPerPage);
        Date end = new Date();

        long time = end.getTime() - start.getTime();

        ScoreDoc[] hits = results.scoreDocs;

        JsonObject jsonObject = new JsonObject().put("time", time);
        List<JsonObject> list = new ArrayList<>();
        for (ScoreDoc sd : hits) {
            JsonObject json = new JsonObject();
            json.put("doc", sd.doc);
            Document doc = searcher.doc(sd.doc);
            json.put("table", doc.get("table"));
            json.put("column", doc.get("column"));
            json.put("count", doc.get("count"));
            json.put("contents", doc.get("contents"));
            json.put("score", sd.score);
            list.add(json);
        }
        jsonObject.put("result",list);
//        System.out.println(results.totalHits + " total matching documents");
//        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        return jsonObject;
    }

    public static void buildIndex(JsonObject json) throws Exception {
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
        StringField textField = new StringField("contents", "", Field.Store.YES);

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

//    public void updateIndex(String queries, String indexPath, String table, String column) throws Exception {
//        queries = queries.toLowerCase();
//        Directory dir = FSDirectory.open(Paths.get(indexPath));
//        Analyzer analyzer = new StandardAnalyzer();
//        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//        IndexWriter writer = new IndexWriter(dir, iwc);
//        Term term = new Term("contents", queries.toLowerCase());
//        List<JsonObject> list = searchIndex(queries, indexPath);
//
//        for (JsonObject json : list) {
//            String contents = json.getString("contents").toLowerCase();
//            String tableDoc = json.getString("table").toLowerCase();
//            String columnDoc = json.getString("column").toLowerCase();
//            if (contents.equals(queries) && tableDoc.equals(table) && columnDoc.equals(column)) {
//                int docIndex = json.getInteger("doc");
//                IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//                IndexSearcher searcher = new IndexSearcher(reader);
//                Document document = searcher.doc(docIndex);
//
//                String count = String.valueOf(Long.parseLong(document.get("count")) + 1);
//                StringField longPoint = new StringField("count", count, Field.Store.YES);
//
//                document.removeField("count");
//                document.add(longPoint);
//
//                writer.updateDocument(term, document);
//                reader.close();
//                break;
//            }
//        }
//
//        writer.close();
//    }

}