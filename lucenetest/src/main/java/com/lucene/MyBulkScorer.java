package com.lucene;

import org.apache.lucene.search.BulkScorer;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.util.Bits;

import java.io.IOException;


public abstract class MyBulkScorer extends BulkScorer {

    public void score(LeafCollector collector, Bits acceptDocs) throws IOException {
        final int next = score(collector, acceptDocs, 0, DocIdSetIterator.NO_MORE_DOCS);
        assert next == DocIdSetIterator.NO_MORE_DOCS;
    }

    public abstract int score(LeafCollector collector, Bits acceptDocs, int min, int max) throws IOException;

    public abstract long cost();
}
