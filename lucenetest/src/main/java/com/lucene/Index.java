package com.lucene;

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
import java.util.Date;

/**
 * creator: sunc
 * date: 2017/3/30
 * description:
 */

public class Index {

    public JsonArray searchIndex(String queries, String indexPath) throws Exception {
        Date start = new Date();
        int hitsPerPage = Integer.MAX_VALUE;

//        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexReader reader = DirectoryReader.open(new MMapDirectory(Paths.get(indexPath)));
//        IndexReader reader = DirectoryReader.open(new RAMDirectory(new SimpleFSDirectory(Paths.get(indexPath)), new IOContext()));
        MyIndexSearcher searcher = new MyIndexSearcher(reader);
        Term term = new Term("contents", queries.toLowerCase());
        PrefixQuery query = new PrefixQuery(term);

        MyCustomScoreQuery myCustomScoreQuery = new MyCustomScoreQuery(query, queries, searcher);

        Date end1 = new Date();
        System.out.println(end1.getTime() - start.getTime() + " total milliseconds");

        TopDocs results = searcher.search(myCustomScoreQuery, hitsPerPage);

        Date end2 = new Date();
        System.out.println(end2.getTime() - end1.getTime() + " total milliseconds");

        ScoreDoc[] hits = results.scoreDocs;

        JsonArray ja = new JsonArray();
        for (ScoreDoc sd : hits) {
            JsonObject json = new JsonObject();
            json.put("doc", sd.doc);
            Document doc = searcher.doc(sd.doc);
            json.put("table", doc.get("table"));
            json.put("column", doc.get("column"));
            json.put("count", doc.get("count"));
            json.put("contents", doc.get("contents"));
            json.put("score", sd.score);
            ja.add(json);
        }

//        System.out.println(results.totalHits + " total matching documents");
        Date end = new Date();
        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        reader.close();
        return ja;
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
//            Document document = searcher.doc(doc);
//            String contents = document.get("contents").toLowerCase();
//            long count = Long.parseLong(document.get("count"));
//            float score = subQueryScore * valSrcScore / contents.length();
//            if (contents.startsWith(queries))
//                score *= 10;
            return 0;
        }
    }

    public void updateIndex(String queries, String indexPath, String table, String column) throws Exception {
        queries = queries.toLowerCase();
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);
        Term term = new Term("contents", queries.toLowerCase());
        JsonArray ja = searchIndex(queries, indexPath);

        for (Object j : ja) {
            JsonObject json = (JsonObject) j;
            String contents = json.getString("contents").toLowerCase();
            String tableDoc = json.getString("table").toLowerCase();
            String columnDoc = json.getString("column").toLowerCase();
            if (contents.equals(queries) && tableDoc.equals(table) && columnDoc.equals(column)) {
                int docIndex = json.getInteger("doc");
                IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
                IndexSearcher searcher = new IndexSearcher(reader);
                Document document = searcher.doc(docIndex);

                String count = String.valueOf(Long.parseLong(document.get("count")) + 1);
                StringField longPoint = new StringField("count", count, Field.Store.YES);

                document.removeField("count");
                document.add(longPoint);

                writer.updateDocument(term, document);
                reader.close();
                break;
            }
        }

        writer.close();
    }

}