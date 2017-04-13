package com.lucene;

import org.apache.lucene.search.ScoreDoc;

/**
 * creator: sunc
 * date: 2017/4/10
 * description:
 */
public class MyHitQueue extends MyPriorityQueue<ScoreDoc> {

    MyHitQueue(int size, boolean prePopulate) {
        super(size, prePopulate);
    }

    @Override
    protected ScoreDoc getSentinelObject() {
        return new ScoreDoc(-1, Float.NEGATIVE_INFINITY);
    }

    @Override
    protected final boolean lessThan(ScoreDoc hitA, ScoreDoc hitB) {
        if (hitA.score == hitB.score)
            return hitA.doc > hitB.doc;
        else
            return hitA.score < hitB.score;
    }

    @Override
    protected boolean moreThan(ScoreDoc hitA, ScoreDoc hitB) {
        return hitA.score > hitB.score;
    }
}
