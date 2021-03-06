package practica2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Consultas {
	
	private static String index = "index2";
    private static String field = "contents";
    private static String queries = null;
    private static int repeat = 0;
    private static boolean raw = false;
    private static String queryString = null;
    private static int hitsPerPage = 10;

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {
	    String usage =
	      "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
	    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
	      System.out.println(usage);
	      System.exit(0);
	    }
	    
	    for(int i = 0;i < args.length;i++) {
	      if ("-index".equals(args[i])) {
	        index = args[i+1];
	        i++;
	      } else if ("-field".equals(args[i])) {
	        field = args[i+1];
	        i++;
	      } else if ("-queries".equals(args[i])) {
	        queries = args[i+1];
	        i++;
	      } else if ("-query".equals(args[i])) {
	        queryString = args[i+1];
	        i++;
	      } else if ("-repeat".equals(args[i])) {
	        repeat = Integer.parseInt(args[i+1]);
	        i++;
	      } else if ("-raw".equals(args[i])) {
	        raw = true;
	      } else if ("-paging".equals(args[i])) {
	        hitsPerPage = Integer.parseInt(args[i+1]);
	        if (hitsPerPage <= 0) {
	          System.err.println("There must be at least 1 hit per page.");
	          System.exit(1);
	        }
	        i++;
	      }
	    }
	    
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    //Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_44);
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);

	    BufferedReader in = null;
	    if (queries != null) {
	      in = new BufferedReader(new InputStreamReader(new FileInputStream(queries), "UTF-8"));
	    } else {
	      in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
	    }
	    QueryParser parser = new QueryParser(Version.LUCENE_44, field, analyzer);
	    while (true) {
	      if (queries == null && queryString == null) {                        // prompt the user
	        System.out.println("Enter query: ");
	      }

	      String line = queryString != null ? queryString : in.readLine();

	      if (line == null || line.length() == -1) {
	        break;
	      }

	      line = line.trim();
	      if (line.length() == 0) {
	        break;
	      }
	      
	      String primerTermino = line.substring(0,line.indexOf(":"));
	      if(primerTermino.equals("spatial") && line.indexOf("*")==-1){
	    	  // Si es consulta espacial...
	    	  onlySpatial(line,parser,searcher,in);
	    	    
	      } else if(primerTermino.equals("spatial") && line.indexOf("*")!=-1){
	    	  // Si es espacial y otra...
	    	  spatialAndOther(line,parser,searcher,in);
	    	  
	      } else if(primerTermino.equals("temporal")){
	    	  // Si es de tipo espacial...
	    	  temporal(line,parser,searcher,in);
	    	  
	      } else{	// Si es de cualquier otro tipo...
	    	  Query query = null;
	    	  if(line.indexOf("*")==-1){	// Solo hay una consulta
	    		  query = parser.parse(line);
	    		  System.out.println("Searching for: " + query.toString(field));

		          doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
	    	  } else{
	    		  BooleanQuery queryBoolean = new BooleanQuery();
	    		  boolean terminado = false;
	    		  while(!terminado){	// Obtenemos todas las consultas.
	    			  if(line.indexOf("*")<=0){
	    				  query = parser.parse(line);
	    				  terminado = true;
	    			  } else{
	    				  query = parser.parse(line.substring(0,line.indexOf("*")));
	    			  }
	    			  line = line.substring(line.indexOf("*")+1,line.length());
	    			  queryBoolean.add(query,BooleanClause.Occur.SHOULD);	  
	    		  }
	    		  System.out.println("Searching for: " + queryBoolean.toString(field));
	                
		          doPagingSearch(in, searcher, queryBoolean, hitsPerPage, raw, queries == null && queryString == null);
	    	  }
	    	  
	          System.out.println("Searching for: " + query.toString(field));
	                
	          if (repeat > 0) {                           // repeat & time as benchmark
	            Date start = new Date();
	            for (int i = 0; i < repeat; i++) {
	            	 searcher.search(query, 100);
	            }
	            Date end = new Date();
	            System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
	          }
	          doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
	      }

	      if (queryString != null) {
	        break;
	      }
	    }
	    reader.close();
	  }

	  /**
	   * This demonstrates a typical paging search scenario, where the search engine presents 
	   * pages of size n to the user. The user can then go to the next page if interested in
	   * the next hits.
	   * 
	   * When the query is executed for the first time, then only enough results are collected
	   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
	   * is executed another time and all hits are collected.
	   * 
	   */
	  public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
	                                     int hitsPerPage, boolean raw, boolean interactive) throws IOException {
	 
	    // Collect enough docs to show 5 pages
	    TopDocs results = searcher.search(query, 5 * hitsPerPage);
	    ScoreDoc[] hits = results.scoreDocs;
	    
	    int numTotalHits = results.totalHits;
	    System.out.println(numTotalHits + " total matching documents");

	    int start = 0;
	    int end = Math.min(numTotalHits, hitsPerPage);
	        
	    while (true) {
	      if (end > hits.length) {
	        System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
	        System.out.println("Collect more (y/n) ?");
	        String line = in.readLine();
	        if (line.length() == 0 || line.charAt(0) == 'n') {
	          break;
	        }

	        hits = searcher.search(query, numTotalHits).scoreDocs;
	      }
	      
	      end = Math.min(hits.length, start + hitsPerPage);
	      
	      for (int i = start; i < end; i++) {
	        if (raw) {                              // output raw format
	          System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
	          continue;
	        }

	        Document doc = searcher.doc(hits[i].doc);
	        String path = doc.get("path");
	        Date fecha = new Date(Long.parseLong(doc.get("modified")));
	        if (path != null) {
	          System.out.println((i+1) + ". " + path);
	          System.out.println("modified: " + fecha);
	        } else {
	          System.out.println((i+1) + ". " + "No path for this document");
	        }
	                  
	      }

	      if (!interactive || end == 0) {
	        break;
	      }

	      if (numTotalHits >= end) {
	        boolean quit = false;
	        while (true) {
	          System.out.print("Press ");
	          if (start - hitsPerPage >= 0) {
	            System.out.print("(p)revious page, ");  
	          }
	          if (start + hitsPerPage < numTotalHits) {
	            System.out.print("(n)ext page, ");
	          }
	          System.out.println("(q)uit or enter number to jump to a page.");
	          
	          String line = in.readLine();
	          if (line.length() == 0 || line.charAt(0)=='q') {
	            quit = true;
	            break;
	          }
	          if (line.charAt(0) == 'p') {
	            start = Math.max(0, start - hitsPerPage);
	            break;
	          } else if (line.charAt(0) == 'n') {
	            if (start + hitsPerPage < numTotalHits) {
	              start+=hitsPerPage;
	            }
	            break;
	          } else {
	            int page = Integer.parseInt(line);
	            if ((page - 1) * hitsPerPage < numTotalHits) {
	              start = (page - 1) * hitsPerPage;
	              break;
	            } else {
	              System.out.println("No such page");
	            }
	          }
	        }
	        if (quit) break;
	        end = Math.min(numTotalHits, start + hitsPerPage);
	      }
	    }
	  }
	  
	  /*
	   * Método que realiza la búsqueda de sólo una condición espacial.
	   */
	  public static void onlySpatial(String line, QueryParser parser, IndexSearcher searcher,
			  BufferedReader in){
		  try{
			// Declaramos las variables para la consulta espacial.
		      Double east = null, west = null, south = null, north = null;
	    	  
	    	  // Obtenemos las distintas coordenadas.
	    	  String coordenadas = line.substring(line.indexOf(":")+1,line.length());
	    	  
	    	  west = new Double(Double.parseDouble(coordenadas.substring(
	    			  						0,coordenadas.indexOf(","))));
	    	  coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      east = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.indexOf(","))));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      south = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.indexOf(","))));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      north = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.length())));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      
		      // Creamos la query.
		      BooleanQuery queryBoolean = new BooleanQuery();
		      
		      // Calculamos las intersecciones entre cajas.
		      NumericRangeQuery<Double> westRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "west", null, east, true, true);
		      NumericRangeQuery<Double> eastRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "east", west, null, true, true);
		      NumericRangeQuery<Double> northRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "north", south, null, true, true);
		      NumericRangeQuery<Double> southRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "south", null, north, true, true);
		      
		      // Realizamos las búsquedas.
		      queryBoolean.add(westRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(eastRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(northRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(southRangeQuery,BooleanClause.Occur.MUST);
		      
		      // Realizamos la búsqueda y mostramos los resultados.
		      System.out.println("Searching for: " + queryBoolean.toString(field));
	            
		      doPagingSearch(in, searcher, queryBoolean, hitsPerPage, raw, queries == null && queryString == null);
		  } catch(Exception e){
			  System.err.println(e);
		  }
		  
	  }
	  
	  /*
	   * Método que realiza la búsqueda de una condición espacial y otra distinta.
	   */
	  public static void spatialAndOther(String line, QueryParser parser, IndexSearcher searcher,
			  BufferedReader in){
		  try{
			// Declaramos las variables para la consulta espacial.
		      Double east = null, west = null, south = null, north = null;
	    	  
	    	  // Obtenemos las distintas coordenadas.
	    	  String coordenadas = line.substring(line.indexOf(":")+1,line.indexOf("*"));
	    	  
	    	  west = new Double(Double.parseDouble(coordenadas.substring(
	    			  						0,coordenadas.indexOf(","))));
	    	  coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      east = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.indexOf(","))));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      south = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.indexOf(","))));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      north = new Double(Double.parseDouble(coordenadas.substring(
						0,coordenadas.length())));
		      coordenadas = coordenadas.substring(coordenadas.indexOf(",")+1,
	    			  	coordenadas.length());
		      
		      // Creamos la query.
		      BooleanQuery queryBoolean = new BooleanQuery();
		      
		      // Calculamos las intersecciones entre cajas.
		      NumericRangeQuery<Double> westRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "west", null, east, true, true);
		      NumericRangeQuery<Double> eastRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "east", west, null, true, true);
		      NumericRangeQuery<Double> northRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "north", south, null, true, true);
		      NumericRangeQuery<Double> southRangeQuery = NumericRangeQuery.newDoubleRange(
		    		  "south", null, north, true, true);
		      
		      // Realizamos las búsquedas.
		      queryBoolean.add(westRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(eastRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(northRangeQuery,BooleanClause.Occur.MUST);
		      queryBoolean.add(southRangeQuery,BooleanClause.Occur.MUST);
		      
	    	  // Obtenemos la otra consulta.
	    	  String resto = line.substring(line.indexOf("*")+1,line.length());
	    	  // Creamos la otra query.
	    	  Query query = parser.parse(resto);
	    	  
	    	  // Creamos la query final.
	    	  BooleanQuery queryFinal = new BooleanQuery();
	    	  queryFinal.add(queryBoolean,BooleanClause.Occur.SHOULD);
	    	  queryFinal.add(query,BooleanClause.Occur.SHOULD);
	    	  
	          System.out.println("Searching for: " + queryFinal.toString(field));
	                
	          doPagingSearch(in, searcher, queryFinal, hitsPerPage, raw, queries == null && queryString == null);
		  } catch(Exception e){
			  System.err.println(e);
		  }
	  }
	  
	  /*
	   * Método que procesa las consultas de tipo temporal.
	   */
	  public static void temporal(String line, QueryParser parser, IndexSearcher searcher,
			  BufferedReader in){
		  try{
			  // Declaramos las variables para la consulta temporal.
			  String begin = null, end = null;
		    	  
			  // Obtenemos las distintas coordenadas.
			  System.out.println(line.substring(line.indexOf("[")+1,line.indexOf("TO")-1));
			  System.out.println("hola");
			  begin = line.substring(line.indexOf("[")+1,line.indexOf("TO")-1);
			  end = line.substring(line.indexOf("TO")+3,line.indexOf("]"));
			  System.out.println(begin+"-"+end);
			   
			  // Si es solo año, rellenamos con 0 al final.
			  for(int i=begin.length(); i<8; i++){
				  begin = begin + "0";
			  }
			  for(int i=end.length(); i<8; i++){
				  end = end + "0";
			  }
			  
			  System.out.println(Integer.parseInt(end)+"-"+Integer.parseInt(begin));
			  // Creamos la query.
			  BooleanQuery queryBoolean = new BooleanQuery();
			      
			  // Calculamos las intersecciones entre fechas.
			  NumericRangeQuery<Integer> beginRangeQuery = NumericRangeQuery.newIntRange(
			    		  "begin", null, Integer.parseInt(end), true, true);
			  NumericRangeQuery<Integer> endRangeQuery = NumericRangeQuery.newIntRange(
			    		  "end", Integer.parseInt(begin), null, true, true);
			      
			  // Realizamos las búsquedas.
			  queryBoolean.add(beginRangeQuery,BooleanClause.Occur.MUST);
			  queryBoolean.add(endRangeQuery,BooleanClause.Occur.MUST);
		    	  
		      System.out.println("Searching for: " + queryBoolean.toString(field));
		                
		      doPagingSearch(in, searcher, queryBoolean, hitsPerPage, raw, queries == null && queryString == null);
		  } catch(Exception e){
			  System.err.println(e);
		  }  
	  }
}
