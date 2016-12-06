package cn.psvmc;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Directory directory=null;
    public static void main( String[] args )
    {
        try {
            String classPath = ClassLoader.getSystemResource("").getPath();
            directory= FSDirectory.open(new File(classPath+"luceneIndex"));
            createIndex();
            searchIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //删除索引
    public void delete()
    {
        Analyzer analyzer = new PaodingAnalyzer();
        try{
            IndexWriter iwriter = new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_CURRENT,analyzer));
            //删除至回收站
            //iwriter.deleteAll();
            //彻底删除
            iwriter.forceMergeDeletes();
        }catch (Exception e){

        }
    }

    //创建索引
    public static void createIndex(){
        try {
            Analyzer analyzer = new PaodingAnalyzer();
            IndexWriter iwriter = new IndexWriter(directory,new IndexWriterConfig(Version.LUCENE_CURRENT,analyzer));
            Document doc = new Document();
            String text = "我爱你祖国.";
            doc.add(new Field("article_content", text, TextField.TYPE_STORED));
            iwriter.addDocument(doc);
            iwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询
    public static void searchIndex(){
        try {
            Analyzer analyzer = new PaodingAnalyzer();
            DirectoryReader ireader  = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Parse a simple query that searches for "text":
            QueryParser parser=new QueryParser(Version.LUCENE_CURRENT, "body" ,analyzer );


            TermQuery tq = new TermQuery(new Term("article_content", "祖国"));
            ScoreDoc[] hits = isearcher.search(tq, 100, Sort.INDEXORDER).scoreDocs;

            // Iterate through the results:
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                String article_content = hitDoc.get("article_content");
                System.out.println(article_content);
            }
            ireader.close();
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
