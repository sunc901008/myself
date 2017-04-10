package com;

import com.lucene.MyPriorityQueue;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.ScoreDoc;

/**
 * creator: sunc
 * date: 2017/4/10
 * description:
 */
public class home {

    public static void main(String[] args) {
        MyPriorityQueue myHitQueue = new MyPriorityQueue(5, true) {

            @Override
            protected ScoreDoc getSentinelObject() {
                Double db = new Double(Math.random())*1000;
                return new ScoreDoc(-1 , db.floatValue());
            }

            @Override
            protected boolean lessThan(Object a, Object b) {
                ScoreDoc hitA = (ScoreDoc)a;
                ScoreDoc hitB = (ScoreDoc)b;
                if (hitA.score == hitB.score)
                    return hitA.doc > hitB.doc;
                else
                    return hitA.score < hitB.score;
            }

            @Override
            protected boolean moreThan(Object a, Object b) {
                ScoreDoc hitA = (ScoreDoc)a;
                ScoreDoc hitB = (ScoreDoc)b;
                if (hitA.score == hitB.score)
                    return hitA.doc < hitB.doc;
                else
                    return hitA.score > hitB.score;
            }

        };

        myHitQueue.forEach(System.out::println);
        System.out.println("************");
        ScoreDoc pop = (ScoreDoc)myHitQueue.popSmall();
        System.out.println("pop : " + pop.doc + " | " + pop.score);
        myHitQueue.forEach(System.out::println);
        System.out.println("************");
        pop = (ScoreDoc)myHitQueue.popSmall();
        System.out.println("pop : " + pop.doc + " | " + pop.score);
        myHitQueue.forEach(System.out::println);

    }

}
