package com.lucene;

import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
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

            Date start = new Date();
            System.out.println("pq.size() before : " + pq.size());
            System.out.println("********************");
            pq.forEach(p -> System.out.println(p.doc + " | score : " + p.score));
            System.out.println("********************");

            for (int i = pq.size() - howMany; i > 0; i--) {
                ScoreDoc pop = pq.popSmall();
                System.out.println("pop : " + pop.doc + " | score : " + pop.score);
                pq.forEach(p -> System.out.print(p.doc + "|"));
                System.out.println();
            }

            System.out.println("********************");
            pq.forEach(p -> System.out.println(p.doc + " | score : " + p.score));
            System.out.println("********************");
            System.out.println("pq.size() after : " + pq.size());
            Date end1 = new Date();
            System.out.println("in reduce for 1: " + (end1.getTime() - start.getTime()));

            for (int i = howMany - 1; i >= 0; i--) {
                results[i] = pq.popSmall();
            }
            Date end2 = new Date();
            System.out.println("in reduce for 2: " + (end2.getTime() - end1.getTime()));

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
            System.out.println("********************");
            pq.forEach(p -> System.out.println(p.doc + " | score : " + p.score));
            System.out.println("********************");

            for (int i = 0; i < howMany; i++) {
                ScoreDoc pop = pq.popBig();
                results[i] = pop;
                System.out.println("pop : " + pop.doc + " | score : " + pop.score);
                pq.forEach(p -> System.out.print(p.doc + "|"));
                System.out.println();
            }

            System.out.println("********************");
            Arrays.asList(results).forEach(p -> System.out.println(p.doc + " | score : " + p.score));
            System.out.println("********************");
            System.out.println("pq.size() after : " + results.length);
            Date end1 = new Date();
            System.out.println("in reduce for 1: " + (end1.getTime() - start.getTime()));

            Date end2 = new Date();
            System.out.println("in reduce for 2: " + (end2.getTime() - end1.getTime()));

            maxScore = results[0].score;
        }

        MyTopDocs myTopDocs = new MyTopDocs(hits, results, maxScore);
        System.out.println("-----------------------out reduce for ----------------------");

        return myTopDocs;
    }

}

