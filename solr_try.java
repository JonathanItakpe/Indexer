/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.apache.http.impl.client.SystemDefaultHttpClient;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
/**
 *
 * @author PR0PH3T
 */
public class solr_try {
    private static HttpSolrServer solr;
    public static SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
    
    //HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr/", httpClient);
	public static void main(String[] args) throws IOException, SAXException, TikaException {
	
		try {
			//solr = new HttpSolrServer("http://localhost:8983/solr/"); //create solr connection
                        solr = new HttpSolrServer("http://localhost:8983/solr/", httpClient) {};
			//solr.deleteByQuery( "*:*" ); //delete everything in the index; good for testing
			
			//location of source documents
			//later this will be switched to a database
			String path = "C:\\Users\\PR0PH3T\\Documents\\NetBeansProjects\\Indexer\\content\\";
			String file_html = path + "mobydick.htm";
			String file_txt = path + "robinsoncrusoe.txt";
			String file_pdf = path + "one.pdf";
			
			//processDocument(file_html);
			//processDocument(file_txt);
			processDocument(file_pdf);
			
			solr.commit(); //after all docs are added, commit to the index
			
			//now you can search at http://localhost:8983/solr/browse
		}
	    catch  (Exception ex) {
	        System.out.println(ex.getMessage());
	    }			
	}
	
	private static void processDocument(String pathfilename)  {
		 
	    try {
	        InputStream input = new FileInputStream(new File(pathfilename));

	        //use Apache Tika to convert documents in different formats to plain text
	        ContentHandler textHandler = new BodyContentHandler(10*1024*1024);
	        Metadata meta = new Metadata();
	        Parser parser = new AutoDetectParser(); //handles documents in different formats:
	        ParseContext context = new ParseContext();	 
	        parser.parse(input, textHandler, meta, context); //convert to plain text

	        //collect metadata and content from Tika and other sources
	        
	        //document id must be unique, use guid
		UUID guid = java.util.UUID.randomUUID();
		String docid = guid.toString();
	        	        
	        //Dublin Core metadata (partial set)
	        String doctitle = meta.get(DublinCore.TITLE);
	        String doccreator = meta.get(DublinCore.CREATOR); 
	        
	        //other metadata
	        String docurl = pathfilename; //document url
	        
	        //content
	        String doccontent = textHandler.toString();
	        
	        //call to index
	        indexDocument(docid, doctitle, doccreator, docurl, doccontent);
	    }
	    catch  (Exception ex) {
	        System.out.println(ex.getMessage());
	    }
	}	
	
	private static void indexDocument(String docid, String doctitle, String doccreator, String docurl, String doccontent)  {
		 
		try {
			SolrInputDocument doc = new SolrInputDocument();
			
			doc.addField("id", docid);
			
			//map metadata fields to default schema
			//location: path\solr-4.7.2\example\solr\collection1\conf\schema.xml
			
			//Dublin Core
			//thought: schema could be modified to use Dublin Core
			doc.addField("title", doctitle);
			doc.addField("author", doccreator);

			//other metadata
			doc.addField("url", docurl);
			
			//content (and text)
			//per schema, the content field is not indexed by default, used for returning and highlighting document content
			//the schema "copyField" command automatically copies this to the "text" field which is indexed
			doc.addField("content", doccontent);
			
			//indexing
			//when a field is indexed, like "text", Solr will handle tokenization, stemming, removal of stopwords etc, per the schema defn
			
			//add to index
			solr.add(doc);	
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
    
}
