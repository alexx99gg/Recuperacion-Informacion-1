package practica3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import Trabajo.Consulta;
import Trabajo.XMLParser;

/** Simple command-line based search demo. */
public class SearchFiles1 {

private static PrintWriter ficheroSal;	// Fichero de resultados.
	
  private SearchFiles1() {}

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
	  comprobarArgumentos(args);	// Comprobamos los argumentos.
	  
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(args[1])));
		IndexSearcher searcher = new IndexSearcher(reader);
    //Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_44);
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);

    // Creamos el parser para las consultas.
 	XMLParser parserXML = new XMLParser(args[3]);
 	// Creamos lista de consultas.
 	ArrayList<Consulta> consultas = parserXML.parserNeeds();

 // Creamos el parser para la consulta.
 		QueryParser parser = new QueryParser(Version.LUCENE_44, "contents", analyzer);
 		for(int i=0; i<consultas.size(); i++){	// Recorremos las consultas.
 			
 			 Consulta consulta = consultas.get(i); // Obtenemos la consulta.
 			 String texto = consulta.getNecesidad().trim();	// Normalizamos texto.
 			 Query query = parser.parse(texto);	// Creamos la query.
 			 buscarConsulta(searcher, query,consultas.get(i).getIdentificador());
      }
    reader.close();
  }

  /*
	 * Método que comprueba si los argumentos de la invocación son correctos.
	 */
	public static void comprobarArgumentos(String [] argumentos){
		
		if(argumentos.length != 6){	// Comprueba el número de argumentos.
			System.err.println("Usar: java SearchFiles -index <indexPath>"
					+ " -infoNeeds <infoNeedsFile> -output <resultsFile>");
			System.exit(-1);
		}
		if(!argumentos[0].equals("-index")){		// Comprueba primer argumento.
			System.err.println("Primer argumento: -index");
			System.exit(-1);
		}
		if(!argumentos[2].equals("-infoNeeds")){		// Comprueba segundo argumento.
			System.err.println("Segundo argumento: -infoNeeds");
			System.exit(-1);
		}
		if(!argumentos[4].equals("-output")){		// Comprueba cuarto argumento.
			System.err.println("Segundo argumento: -output");
			System.exit(-1);
		}
		
		try{		// Creamos el fichero de salida.
			ficheroSal = new PrintWriter(new FileWriter(argumentos[5]));
		} catch(Exception e){	// Capturamos las posibles excepciones.
			System.err.println("Error al crear el fichero: " + argumentos[5]);
		}
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
public static void buscarConsulta(IndexSearcher searcher, Query query,
		  String id) throws IOException {

  // Collect enough docs to show 5 pages
  TopDocs results = searcher.search(query, 30);
  ScoreDoc[] hits = results.scoreDocs;
  
// Se recorren los resultados y se almacenan en un fichero.
  for(int i=0; i<30 && i<results.totalHits; i++){
		Document doc = searcher.doc(hits[i].doc);
		ficheroSal.printf(i + "\t" + doc.get("path"));
		if(hits.length != i-1){
			ficheroSal.println();
		}
	}
}
}
