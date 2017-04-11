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

        System.out.println("-----------------------in reduce for ----------------------");
        int howMany = totalHits < pq.size() ? totalHits : pq.size();

        int hits = totalHits;
        float maxScore = Float.NaN;
        ScoreDoc[] results;
        if (howMany <= 0) {
            hits = 0;
            results = new ScoreDoc[0];
        } else {
            results = new ScoreDoc[howMany];

            System.out.println("pq.size() before : " + pq.size());

            for (int i = pq.size() - howMany; i > 0; i--) {
                pq.popSmall();
            }

            System.out.println("pq.size() after : " + pq.size());

            for (int i = howMany - 1; i >= 0; i--) {
                results[i] = pq.popSmall();
            }
            maxScore = results[0].score;
        }

        MyTopDocs myTopDocs = new MyTopDocs(hits, results, maxScore);
        System.out.println("-----------------------out reduce for ----------------------");

        return myTopDocs;
    }

    public MyTopDocs topDocs() {

        System.out.println("-----------------------in reduce for ----------------------");
        int howMany = totalHits < pq.size() ? totalHits : pq.size();

        int hits = totalHits;
        float maxScore = Float.NaN;
        ScoreDoc[] results;
        if (howMany <= 0) {
            hits = 0;
            results = new ScoreDoc[0];
        } else {
            results = new ScoreDoc[howMany];

            Date start = new Date();
            System.out.println("pq.size() before : " + pq.size());

            for (int i = 0; i < howMany; i++) {
                ScoreDoc pop = pq.popBig();
                results[i] = pop;
            }

            System.out.println("pq.size() after : " + results.length);
            Date end1 = new Date();
            System.out.println("in reduce for: " + (end1.getTime() - start.getTime()));

            maxScore = results[0].score;
        }

        MyTopDocs myTopDocs = new MyTopDocs(hits, results, maxScore);
        System.out.println("-----------------------out reduce for ----------------------");

        return myTopDocs;
    }

}

