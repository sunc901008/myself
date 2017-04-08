package com.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyCustomScore {
    public void searchByScoreQuery() {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(""))));
            Query q = new TermQuery(new Term("content", "1"));
            // 1.创建评分域
//            FunctionQuery fieldScoreQuery = new FunctionScoreQuery("size", SortField.Type.INT);
            // 2.根据评分域和原有的query创建自定义的query对象
            // 2.1 创建MyCustomScoreQuery继承CustomScoreQuery
            // 2.2重写getCustomScoreProvider方法
            // 2.3创建MyCustomScoreProvider继承CustomScoreProvider
            // 2.4重写customScore方法,自定义评分算法
            MyCustomScoreQuery query = new MyCustomScoreQuery(q);
            TopDocs docs = searcher.search(query, 30);
            // 输出信息
            ScoreDoc[] sds = docs.scoreDocs;
            Document d = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (ScoreDoc s : sds) {
                d = searcher.doc(s.doc);
                System.out.println(s.doc + "->" + s.score + "->"
                        + d.get("filename") + "->" + d.get("size") + "->"
                        + sdf.format(new Date(Long.valueOf(d.get("date")))));
            }

        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MyCustomScoreQuery extends CustomScoreQuery {

    public MyCustomScoreQuery(Query subQuery) {
        super(subQuery);
    }

    @Override
    protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
        return new MyCustomScoreProvider(context);
    }

}

class MyCustomScoreProvider extends CustomScoreProvider {

    /**
     * Creates a new instance of the provider class for the given {@link IndexReader}.
     *
     * @param context
     */
    public MyCustomScoreProvider(LeafReaderContext context) {
        super(context);
    }

    @Override
    public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
        return subQueryScore * valSrcScore + 1;
    }
}
