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


import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class MyIndexSearcher extends IndexSearcher {

    private static QueryCache DEFAULT_QUERY_CACHE;
    private static QueryCachingPolicy DEFAULT_CACHING_POLICY = new UsageTrackingQueryCachingPolicy();

    static {
        final int maxCachedQueries = 1000;
        final long maxRamBytesUsed = Math.min(1L << 25, Runtime.getRuntime().maxMemory() / 20);
        DEFAULT_QUERY_CACHE = new LRUQueryCache(maxCachedQueries, maxRamBytesUsed);
    }

    final IndexReader reader;

    protected final IndexReaderContext readerContext;
    protected final List<LeafReaderContext> leafContexts;
    protected final LeafSlice[] leafSlices;

    private QueryCache queryCache = DEFAULT_QUERY_CACHE;
    private QueryCachingPolicy queryCachingPolicy = DEFAULT_CACHING_POLICY;

    public MyIndexSearcher(IndexReader r) {
        this(r.getContext());
    }

    public MyIndexSearcher(IndexReaderContext context) {
        super(context, null);
        assert context.isTopLevel : "IndexSearcher's ReaderContext must be topLevel for reader" + context.reader();
        reader = context.reader();
        this.readerContext = context;
        leafContexts = context.leaves();
        this.leafSlices = null;
    }

    protected LeafSlice[] slices(List<LeafReaderContext> leaves) {
        LeafSlice[] slices = new LeafSlice[leaves.size()];
        for (int i = 0; i < slices.length; i++) {
            slices[i] = new LeafSlice(leaves.get(i));
        }
        return slices;
    }

    public Document doc(int docID) throws IOException {
        return reader.document(docID);
    }


    public MyTopDocs searchAfter(Query query, int numHits) throws IOException {

        System.out.println("-----------------------in search 1----------------------");

        final int limit = Math.max(1, reader.maxDoc());
        numHits = Math.min(numHits, limit);

        final int cappedNumHits = Math.min(numHits, limit);

        final CollectorManager<MyTopScoreDocCollector, MyTopDocs> manager = new CollectorManager<MyTopScoreDocCollector, MyTopDocs>() {

            @Override
            public MyTopScoreDocCollector newCollector() throws IOException {
                MyTopScoreDocCollector collector = MyTopScoreDocCollector.create(cappedNumHits);
                return collector;
            }

            @Override
            public MyTopDocs reduce(Collection<MyTopScoreDocCollector> collectors) throws IOException {

                System.out.println("-----------------------in reduce ----------------------");
                final MyTopDocs[] topDocs = new MyTopDocs[collectors.size()];
                int i = 0;
                Date start = new Date();
                for (MyTopScoreDocCollector collector : collectors) {
                    topDocs[i++] = collector.topDocs1();
                }
                Date end = new Date();
                System.out.println("in reduce : " + (end.getTime() - start.getTime()));

                MyTopDocs top = MyTopDocs.merge1(0, cappedNumHits, topDocs, true);

                System.out.println("-----------------------out reduce ----------------------");
                return top;
            }
        };

        MyTopDocs result = search(query, manager);
        System.out.println("-----------------------out search 1----------------------");
        return result;
    }

    public MyTopDocs search(Query query, int n) throws IOException {
        return searchAfter(query, n);
    }

    public void search(Query query, Collector results) throws IOException {
        search(leafContexts, createNormalizedWeight(query, results.needsScores()), results);
    }

    public <C extends Collector, T> T search(Query query, CollectorManager<C, T> collectorManager) throws IOException {

        System.out.println("-----------------------in search 2----------------------");

        final C collector = collectorManager.newCollector();

        search(query, collector);

        List<C> co = Collections.singletonList(collector);
        T coll = collectorManager.reduce(co);

        System.out.println("-----------------------in search 2----------------------");
        return coll;

    }

    protected void search(List<LeafReaderContext> leaves, Weight weight, Collector collector) throws IOException {

        System.out.println("-----------------------in search 3----------------------");

        Date start = new Date();
        int i = 0;
        for (LeafReaderContext ctx : leaves) {
            i++;
            System.out.println("-----------------------in search 3 for " + i + "----------------------");
            Date start1 = new Date();
            final LeafCollector leafCollector;
            try {
                leafCollector = collector.getLeafCollector(ctx);
            } catch (CollectionTerminatedException e) {
                continue;
            }
            Date end1 = new Date();
            System.out.println("collector.getLeafCollector: " + (end1.getTime() - start1.getTime()));

            BulkScorer scorer = weight.bulkScorer(ctx);
            Date end2 = new Date();
            System.out.println("weight.bulkScorer: " + (end2.getTime() - end1.getTime()));
            if (scorer != null) {
                try {
                    scorer.score(leafCollector, ctx.reader().getLiveDocs());
                } catch (CollectionTerminatedException e) {
                }
            }

            Date end3 = new Date();
            System.out.println("scorer.score: " + (end3.getTime() - end2.getTime()));
        }
        Date end = new Date();
        System.out.println("in search 3: " + (end.getTime() - start.getTime()));

        System.out.println("-----------------------out search 3----------------------");
    }

    public Query rewrite(Query original) throws IOException {
        Query query = original;
        for (Query rewrittenQuery = query.rewrite(reader); rewrittenQuery != query;
             rewrittenQuery = query.rewrite(reader)) {
            query = rewrittenQuery;
        }
        return query;
    }

    public Weight createNormalizedWeight(Query query, boolean needsScores) throws IOException {
        query = rewrite(query);
        Weight weight = createWeight(query, needsScores);
        weight.normalize(1.0f, 1.0f);
        return weight;
    }

    public Weight createWeight(Query query, boolean needsScores) throws IOException {
        final QueryCache queryCache = this.queryCache;
        Weight weight = query.createWeight(this, needsScores);
        if (!needsScores && queryCache != null) {
            weight = queryCache.doCache(weight, queryCachingPolicy);
        }
        return weight;
    }

}
