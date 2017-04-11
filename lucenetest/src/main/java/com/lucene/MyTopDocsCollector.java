package com.lucene;

import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreDoc;

import java.util.Date;


public abstract class MyTopDocsCollector<T extends ScoreDoc> implements Collector {

    protected MyPriorityQueue<T> pq;

    protected int totalHits;

    protected MyTopDocsCollector(MyPriorityQueue<T> pq) {
        this.pq = pq;
    }

    public MyTopDocs topDocs1() {

        int howMany = totalHits < pq.size() ? totalHits : pq.size();

        int hits = totalHits;
        float maxScore = Float.NaN;
        ScoreDoc[] results;
        if (howMany <= 0) {
            hits = 0;
            results = new ScoreDoc[0];
        } else {
            results = new ScoreDoc[howMany];
            for (int i = pq.size() - howMany; i > 0; i--) {
                pq.popSmall();
            }
            for (int i = howMany - 1; i >= 0; i--) {
                results[i] = pq.popSmall();
            }
            maxScore = results[0].score;
        }

        return new MyTopDocs(hits, results, maxScore);
    }

    public MyTopDocs topDocs() {

        int howMany = totalHits < pq.size() ? totalHits : pq.size();

        int hits = totalHits;
        float maxScore = Float.NaN;
        ScoreDoc[] results;
        if (howMany <= 0) {
            hits = 0;
            results = new ScoreDoc[0];
        } else {
            results = new ScoreDoc[howMany];
            for (int i = 0; i < howMany; i++) {
                ScoreDoc pop = pq.popBig();
                results[i] = pop;
            }
            maxScore = results[0].score;
        }

        return new MyTopDocs(hits, results, maxScore);
    }

}

