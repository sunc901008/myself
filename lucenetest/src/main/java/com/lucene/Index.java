package com.lucene;

import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

    public List<JsonObject> searchIndex(String queries, String indexPath) throws Exception {
        Date start = new Date();
        int hitsPerPage = 20;

//        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexReader reader = DirectoryReader.open(new MMapDirectory(Paths.get(indexPath)));
//        IndexReader reader = DirectoryReader.open(new RAMDirectory(new SimpleFSDirectory(Paths.get(indexPath)), new IOContext()));
        MyIndexSearcher searcher = new MyIndexSearcher(reader);
        Term term = new Term("contents", queries.toLowerCase());
        PrefixQuery query = new PrefixQuery(term);

        MyCustomScoreQuery myCustomScoreQuery = new MyCustomScoreQuery(query, queries, searcher);

        Date end1 = new Date();
        System.out.println("read index to ram : " + (end1.getTime() - start.getTime()));

        TopDocs results = searcher.search(myCustomScoreQuery, hitsPerPage);

        Date end2 = new Date();
        System.out.println("search : " + (end2.getTime() - end1.getTime()));

        ScoreDoc[] hits = results.scoreDocs;

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

        Date end = new Date();
        System.out.println("make structure : " + (end.getTime() - end2.getTime()));
        System.out.println(results.totalHits + " total matching documents");
        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        reader.close();
        return list;
    }


    class MyCustomScoreQuery extends CustomScoreQuery {
        private IndexSearcher searcher;
        private String queries;

        public MyCustomScoreQuery(Query subQuery, String queries, IndexSearcher searcher) {
            super(subQuery);
            this.searcher = searcher;
            this.queries = queries;
        }

        @Override
        protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
            return new MyCustomScoreProvider(context, queries, searcher);
        }

    }

    class MyCustomScoreProvider extends CustomScoreProvider {
        private IndexSearcher searcher;
        private String queries;

        public MyCustomScoreProvider(LeafReaderContext context, String queries, IndexSearcher searcher) {
            super(context);
            this.searcher = searcher;
            this.queries = queries;
        }

        @Override
        public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
            Document document = searcher.doc(doc);
            String contents = document.get("contents").toLowerCase();
            long count = Long.parseLong(document.get("count"));
            float score = subQueryScore * valSrcScore / contents.length();
            if (contents.startsWith(queries))
                score *= 10;
            return score;
        }
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