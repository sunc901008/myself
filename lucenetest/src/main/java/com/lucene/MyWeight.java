package com.lucene;

/**
 * creator: sunc
 * date: 2017/4/11
 * description:
 */

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.Set;

public abstract class MyWeight extends Weight {

    protected final Query parentQuery;

    protected MyWeight(Query query) {
        super(query);
        this.parentQuery = query;
    }

    public abstract void extractTerms(Set<Term> terms);

    public abstract Explanation explain(LeafReaderContext context, int doc) throws IOException;

    public abstract float getValueForNormalization() throws IOException;

    public abstract void normalize(float norm, float boost);

    public abstract Scorer scorer(LeafReaderContext context) throws IOException;

    public ScorerSupplier scorerSupplier(LeafReaderContext context) throws IOException {
        final Scorer scorer = scorer(context);
        if (scorer == null) {
            return null;
        }
        return new ScorerSupplier() {
            @Override
            public Scorer get(boolean randomAccess) {
                return scorer;
            }

            @Override
            public long cost() {
                return scorer.iterator().cost();
            }
        };
    }

    public MyBulkScorer bulkScorer(LeafReaderContext context) throws IOException {

        Scorer scorer = scorer(context);
        if (scorer == null) {
            return null;
        }

        return new DefaultBulkScorer(scorer);
    }

    protected static class DefaultBulkScorer extends MyBulkScorer {
        private final Scorer scorer;
        private final DocIdSetIterator iterator;
        private final TwoPhaseIterator twoPhase;

        public DefaultBulkScorer(Scorer scorer) {
            if (scorer == null) {
                throw new NullPointerException();
            }
            this.scorer = scorer;
            this.iterator = scorer.iterator();
            this.twoPhase = scorer.twoPhaseIterator();
        }

        @Override
        public long cost() {
            return iterator.cost();
        }

        @Override
        public int score(LeafCollector collector, Bits acceptDocs, int min, int max) throws IOException {
            collector.setScorer(scorer);
            if (scorer.docID() == -1 && min == 0 && max == DocIdSetIterator.NO_MORE_DOCS) {
                scoreAll(collector, iterator, twoPhase, acceptDocs);
                return DocIdSetIterator.NO_MORE_DOCS;
            } else {
                int doc = scorer.docID();
                if (doc < min) {
                    if (twoPhase == null) {
                        doc = iterator.advance(min);
                    } else {
                        doc = twoPhase.approximation().advance(min);
                    }
                }
                return scoreRange(collector, iterator, twoPhase, acceptDocs, doc, max);
            }
        }

        static int scoreRange(LeafCollector collector, DocIdSetIterator iterator, TwoPhaseIterator twoPhase,
                              Bits acceptDocs, int currentDoc, int end) throws IOException {
            if (twoPhase == null) {
                while (currentDoc < end) {
                    if (acceptDocs == null || acceptDocs.get(currentDoc)) {
                        collector.collect(currentDoc);
                    }
                    currentDoc = iterator.nextDoc();
                }
                return currentDoc;
            } else {
                final DocIdSetIterator approximation = twoPhase.approximation();
                while (currentDoc < end) {
                    if ((acceptDocs == null || acceptDocs.get(currentDoc)) && twoPhase.matches()) {
                        collector.collect(currentDoc);
                    }
                    currentDoc = approximation.nextDoc();
                }
                return currentDoc;
            }
        }

        static void scoreAll(LeafCollector collector, DocIdSetIterator iterator, TwoPhaseIterator twoPhase, Bits acceptDocs) throws IOException {
            if (twoPhase == null) {
                for (int doc = iterator.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = iterator.nextDoc()) {
                    if (acceptDocs == null || acceptDocs.get(doc)) {
                        collector.collect(doc);
                    }
                }
            } else {
                final DocIdSetIterator approximation = twoPhase.approximation();
                for (int doc = approximation.nextDoc(); doc != DocIdSetIterator.NO_MORE_DOCS; doc = approximation.nextDoc()) {
                    if ((acceptDocs == null || acceptDocs.get(doc)) && twoPhase.matches()) {
                        collector.collect(doc);
                    }
                }
            }
        }
    }

}

