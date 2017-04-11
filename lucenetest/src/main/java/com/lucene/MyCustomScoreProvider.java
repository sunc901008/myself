package com.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 * creator: sunc
 * date: 2017/4/11
 * description:
 */
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
        float score = 1.0f / contents.length();
        if (contents.startsWith(queries))
            score *= 10;
        return score;
    }
}
