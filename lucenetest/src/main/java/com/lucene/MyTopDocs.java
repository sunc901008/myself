package com.lucene;

import org.apache.lucene.search.*;
import org.apache.lucene.util.PriorityQueue;


public class MyTopDocs extends TopDocs {

    public int totalHits;

    public ScoreDoc[] scoreDocs;

    private float maxScore;

    public float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(float maxScore) {
        this.maxScore = maxScore;
    }

    public MyTopDocs(int totalHits, ScoreDoc[] scoreDocs, float maxScore) {
        super(totalHits, scoreDocs, maxScore);
        this.totalHits = totalHits;
        this.scoreDocs = scoreDocs;
        this.maxScore = maxScore;
    }

    private final static class ShardRef {
        final int shardIndex;

        final boolean useScoreDocIndex;

        int hitIndex;

        ShardRef(int shardIndex, boolean useScoreDocIndex) {
            this.shardIndex = shardIndex;
            this.useScoreDocIndex = useScoreDocIndex;
        }

        @Override
        public String toString() {
            return "ShardRef(shardIndex=" + shardIndex + " hitIndex=" + hitIndex + ")";
        }

        int getShardIndex(ScoreDoc scoreDoc) {
            if (useScoreDocIndex) {
                if (scoreDoc.shardIndex == -1) {
                    throw new IllegalArgumentException("setShardIndex is false but TopDocs[" + shardIndex + "].scoreDocs[" + hitIndex + "] is not set");
                }
                return scoreDoc.shardIndex;
            } else {
                return shardIndex;
            }
        }
    }

    static boolean tieBreakLessThan(ShardRef first, ScoreDoc firstDoc, ShardRef second, ScoreDoc secondDoc) {
        final int firstShardIndex = first.getShardIndex(firstDoc);
        final int secondShardIndex = second.getShardIndex(secondDoc);
        if (firstShardIndex < secondShardIndex) {
            return true;
        } else if (firstShardIndex > secondShardIndex) {
            return false;
        } else {
            assert first.hitIndex != second.hitIndex;
            return first.hitIndex < second.hitIndex;
        }
    }

    private static class ScoreMergeSortQueue extends PriorityQueue<ShardRef> {
        final ScoreDoc[][] shardHits;

        public ScoreMergeSortQueue(TopDocs[] shardHits) {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++) {
                this.shardHits[shardIDX] = shardHits[shardIDX].scoreDocs;
            }
        }

        @Override
        public boolean lessThan(ShardRef first, ShardRef second) {
            assert first != second;
            ScoreDoc firstScoreDoc = shardHits[first.shardIndex][first.hitIndex];
            ScoreDoc secondScoreDoc = shardHits[second.shardIndex][second.hitIndex];
            if (firstScoreDoc.score < secondScoreDoc.score) {
                return false;
            } else if (firstScoreDoc.score > secondScoreDoc.score) {
                return true;
            } else {
                return tieBreakLessThan(first, firstScoreDoc, second, secondScoreDoc);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class MergeSortQueue extends PriorityQueue<ShardRef> {
        final ScoreDoc[][] shardHits;
        final FieldComparator<?>[] comparators;
        final int[] reverseMul;

        public MergeSortQueue(Sort sort, TopDocs[] shardHits) {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++) {
                final ScoreDoc[] shard = shardHits[shardIDX].scoreDocs;
                if (shard != null) {
                    this.shardHits[shardIDX] = shard;
                    for (int hitIDX = 0; hitIDX < shard.length; hitIDX++) {
                        final ScoreDoc sd = shard[hitIDX];
                        if (!(sd instanceof FieldDoc)) {
                            throw new IllegalArgumentException("shard " + shardIDX + " was not sorted by the provided Sort (expected FieldDoc but got ScoreDoc)");
                        }
                        final FieldDoc fd = (FieldDoc) sd;
                        if (fd.fields == null) {
                            throw new IllegalArgumentException("shard " + shardIDX + " did not set sort field values (FieldDoc.fields is null); you must pass fillFields=true to IndexSearcher.search on each shard");
                        }
                    }
                }
            }

            final SortField[] sortFields = sort.getSort();
            comparators = new FieldComparator[sortFields.length];
            reverseMul = new int[sortFields.length];
            for (int compIDX = 0; compIDX < sortFields.length; compIDX++) {
                final SortField sortField = sortFields[compIDX];
                comparators[compIDX] = sortField.getComparator(1, compIDX);
                reverseMul[compIDX] = sortField.getReverse() ? -1 : 1;
            }
        }

        @Override
        public boolean lessThan(ShardRef first, ShardRef second) {
            assert first != second;
            final FieldDoc firstFD = (FieldDoc) shardHits[first.shardIndex][first.hitIndex];
            final FieldDoc secondFD = (FieldDoc) shardHits[second.shardIndex][second.hitIndex];

            for (int compIDX = 0; compIDX < comparators.length; compIDX++) {
                final FieldComparator comp = comparators[compIDX];

                final int cmp = reverseMul[compIDX] * comp.compareValues(firstFD.fields[compIDX], secondFD.fields[compIDX]);

                if (cmp != 0) {
                    return cmp < 0;
                }
            }
            return tieBreakLessThan(first, firstFD, second, secondFD);
        }
    }

    public static MyTopDocs merge1(int start, int topN, TopDocs[] shardHits, boolean setShardIndex) {
        return mergeAux(null, start, topN, shardHits, setShardIndex);
    }

    private static MyTopDocs mergeAux(Sort sort, int start, int size, TopDocs[] shardHits, boolean setShardIndex) {

        final PriorityQueue<ShardRef> queue;
        if (sort == null) {
            queue = new ScoreMergeSortQueue(shardHits);
        } else {
            queue = new MergeSortQueue(sort, shardHits);
        }

        int totalHitCount = 0;
        int availHitCount = 0;
        float maxScore = Float.MIN_VALUE;
        for (int shardIDX = 0; shardIDX < shardHits.length; shardIDX++) {
            final TopDocs shard = shardHits[shardIDX];
            totalHitCount += shard.totalHits;
            if (shard.scoreDocs != null && shard.scoreDocs.length > 0) {
                availHitCount += shard.scoreDocs.length;
                queue.add(new ShardRef(shardIDX, setShardIndex == false));
                maxScore = Math.max(maxScore, shard.getMaxScore());
            }
        }

        if (availHitCount == 0) {
            maxScore = Float.NaN;
        }

        final ScoreDoc[] hits;
        if (availHitCount <= start) {
            hits = new ScoreDoc[0];
        } else {
            hits = new ScoreDoc[Math.min(size, availHitCount - start)];
            int requestedResultWindow = start + size;
            int numIterOnHits = Math.min(availHitCount, requestedResultWindow);
            int hitUpto = 0;
            while (hitUpto < numIterOnHits) {
                assert queue.size() > 0;
                ShardRef ref = queue.top();
                final ScoreDoc hit = shardHits[ref.shardIndex].scoreDocs[ref.hitIndex++];
                if (setShardIndex) {
                    hit.shardIndex = ref.shardIndex;
                } else if (hit.shardIndex == -1) {
                    throw new IllegalArgumentException("setShardIndex is false but TopDocs[" + ref.shardIndex + "].scoreDocs[" + (ref.hitIndex - 1) + "] is not set");
                }

                if (hitUpto >= start) {
                    hits[hitUpto - start] = hit;
                }

                hitUpto++;

                if (ref.hitIndex < shardHits[ref.shardIndex].scoreDocs.length) {
                    queue.updateTop();
                } else {
                    queue.pop();
                }
            }
        }

        if (sort == null) {
            return new MyTopDocs(totalHitCount, hits, maxScore);
        } else {
            return new MyTopFieldDocs(totalHitCount, hits, sort.getSort(), maxScore);
        }
    }
}