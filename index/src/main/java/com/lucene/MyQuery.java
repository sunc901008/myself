package com.lucene;

/**
 * creator: sunc
 * date: 2017/4/11
 * description:
 */

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryCache;

import java.io.IOException;

public abstract class MyQuery extends Query {

    public abstract String toString(String field);

    @Override
    public MyWeight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        throw new UnsupportedOperationException("Query " + this + " does not implement createWeight");
    }

    @Override
    public MyQuery rewrite(IndexReader reader) throws IOException {
        return this;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    private final int CLASS_NAME_HASH = getClass().getName().hashCode();

}

