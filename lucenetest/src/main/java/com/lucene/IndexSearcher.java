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
import java.util.concurrent.ExecutorService;

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

    private final ExecutorService executor;

    private QueryCache queryCache = DEFAULT_QUERY_CACHE;
    private QueryCachingPolicy queryCachingPolicy = DEFAULT_CACHING_POLICY;

    public MyIndexSearcher(IndexReader r) {
        this(r, null);
    }

    public MyIndexSearcher(IndexReader r, ExecutorService executor) {
        this(r.getContext(), executor);
    }

    public MyIndexSearcher(IndexReaderContext context, ExecutorService executor) {
        super(context, executor);
        assert context.isTopLevel : "IndexSearcher's ReaderContext must be topLevel for reader" + context.reader();
        reader = context.reader();
        this.executor = executor;
        this.readerContext = context;
        leafContexts = context.leaves();
        this.leafSlices = executor == null ? null : slices(leafContexts);
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


    public TopDocs searchAfter(Query query, int numHits) throws IOException {
        final int limit = Math.max(1, reader.maxDoc());
        numHits = Math.min(numHits, limit);

        final int cappedNumHits = Math.min(numHits, limit);

        final CollectorManager<TopScoreDocCollector, TopDocs> manager = new CollectorManager<TopScoreDocCollector, TopDocs>() {

            @Override
            public TopScoreDocCollector newCollector() throws IOException {
                return TopScoreDocCollector.create(cappedNumHits, null);
            }

            @Override
            public TopDocs reduce(Collection<TopScoreDocCollector> collectors) throws IOException {
                final TopDocs[] topDocs = new TopDocs[collectors.size()];
                int i = 0;
                for (TopScoreDocCollector collector : collectors) {
                    topDocs[i++] = collector.topDocs();
                }
                return TopDocs.merge(0, cappedNumHits, topDocs, true);
            }

        };

        Date start = new Date();
        TopDocs result = search(query, manager);
        Date end = new Date();
        System.out.println("114 line : " + (end.getTime() - start.getTime()) + " total milliseconds");
        return result;
    }

    public TopDocs search(Query query, int n) throws IOException {
        return searchAfter(query, n);
    }

    public void search(Query query, Collector results) throws IOException {
        search(leafContexts, createNormalizedWeight(query, results.needsScores()), results);
    }


    public <C extends Collector, T> T search(Query query, CollectorManager<C, T> collectorManager) throws IOException {
        final C collector = collectorManager.newCollector();

        Date start = new Date();
        search(query, collector);
        Date end = new Date();
        System.out.println("134 line : " + (end.getTime() - start.getTime()) + " total milliseconds");

        List<C> co = Collections.singletonList(collector);
        Date end1 = new Date();
        System.out.println("137 line : " + (end1.getTime() - end.getTime()) + " total milliseconds");

        T coll = collectorManager.reduce(co);
        Date end2 = new Date();
        System.out.println("141 line : " + (end2.getTime() - end1.getTime()) + " total milliseconds");
        return coll;

    }

    protected void search(List<LeafReaderContext> leaves, Weight weight, Collector collector) throws IOException {

        for (LeafReaderContext ctx : leaves) {
            final LeafCollector leafCollector;
            try {
                leafCollector = collector.getLeafCollector(ctx);
            } catch (CollectionTerminatedException e) {
                continue;
            }
            BulkScorer scorer = weight.bulkScorer(ctx);
            if (scorer != null) {
                try {
                    scorer.score(leafCollector, ctx.reader().getLiveDocs());
                } catch (CollectionTerminatedException e) {
                }
            }
        }
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

    /**
     * Creates a {@link Weight} for the given query, potentially adding caching
     * if possible and configured.
     */
    public Weight createWeight(Query query, boolean needsScores) throws IOException {
        final QueryCache queryCache = this.queryCache;
        Weight weight = query.createWeight(this, needsScores);
        if (needsScores == false && queryCache != null) {
            weight = queryCache.doCache(weight, queryCachingPolicy);
        }
        return weight;
    }

    @Override
    public String toString() {
        return "IndexSearcher(" + reader + "; executor=" + executor + ")";
    }

}
