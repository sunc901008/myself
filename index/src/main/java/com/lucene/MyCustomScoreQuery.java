package com.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;

/**
 * creator: sunc
 * date: 2017/4/11
 * description:
 */
class MyCustomScoreQuery extends CustomScoreQuery {
    private IndexSearcher searcher;
    private String queries;

    public MyCustomScoreQuery(Query subQuery, String queries, IndexSearcher searcher) {
        super(subQuery);
        this.searcher = searcher;
        this.queries = queries;
    }

    @Override
    protected MyCustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
        return new MyCustomScoreProvider(context, queries, searcher);
    }

}
