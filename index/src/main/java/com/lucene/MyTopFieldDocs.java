package com.lucene;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;

/**
 * creator: sunc
 * date: 2017/4/10
 * description:
 */
public class MyTopFieldDocs extends MyTopDocs {

    /** The fields which were used to sort results by. */
    public SortField[] fields;

    /** Creates one of these objects.
     * @param totalHits  Total number of hits for the query.
     * @param scoreDocs  The top hits for the query.
     * @param fields     The sort criteria used to find the top hits.
     * @param maxScore   The maximum score encountered.
     */
    public MyTopFieldDocs (int totalHits, ScoreDoc[] scoreDocs, SortField[] fields, float maxScore) {
        super (totalHits, scoreDocs, maxScore);
        this.fields = fields;
    }
}
