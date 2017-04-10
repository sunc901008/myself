/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lucene;


import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;

import java.io.IOException;

public abstract class MyTopScoreDocCollector extends MyTopDocsCollector<ScoreDoc> {

    abstract static class ScorerLeafCollector implements LeafCollector {

        Scorer scorer;

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
        }

    }

    private static class SimpleTopScoreDocCollector extends MyTopScoreDocCollector {

        SimpleTopScoreDocCollector(int numHits) {
            super(numHits);
        }

        @Override
        public LeafCollector getLeafCollector(LeafReaderContext context)
                throws IOException {
            final int docBase = context.docBase;
            return new ScorerLeafCollector() {

                @Override
                public void collect(int doc) throws IOException {
                    float score = scorer.score();

                    assert score != Float.NEGATIVE_INFINITY;
                    assert !Float.isNaN(score);

                    totalHits++;
                    if (score <= pqTop.score) {
                        return;
                    }
                    pqTop.doc = doc + docBase;
                    pqTop.score = score;
                    pqTop = pq.updateTop();
                }

            };
        }

    }

    public static MyTopScoreDocCollector create(int numHits) {
        return new SimpleTopScoreDocCollector(numHits);
    }

    ScoreDoc pqTop;

    MyTopScoreDocCollector(int numHits) {
        super(new MyHitQueue(numHits, true));
        pqTop = pq.top();
    }

    @Override
    public boolean needsScores() {
        return true;
    }

}
