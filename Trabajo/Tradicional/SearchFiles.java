package Trabajo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Clase que realiza el parseo y la búsqueda de unas determinadas
 * necesidades de información sobre un índice dado. Guarda los
 * resultados en un cierto fichero.
 *
 */
public class SearchFiles {

	private static PrintWriter ficheroSal;	// Fichero de resultados.
	
	/*
	 * Método principal que parsea la consulta y la realiza sobre
	 * el índice.
	 */
	public static void main(String[] args) throws Exception {
    
		args = new String [6];
		args[0] = "-index";
		args[1] = "indexTrabajo";
		args[2] = "-infoNeeds";
		args[3] = "necesidadesInformacionElegidas.xml";
		args[4] = "-output";
		args[5] = "resultados.txt";
		
		comprobarArgumentos(args);	// Comprobamos los argumentos.
    
		// Creamos el índice para lectura.
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(args[1])));
		IndexSearcher searcher = new IndexSearcher(reader);
		// Usamos el modelo probabilístico.
		BM25Similarity simil = new BM25Similarity();
		searcher.setSimilarity(simil);
		// Creamos el analizador.
		Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_44);

		// Creamos el parser para las consultas.
		XMLParser parserXML = new XMLParser(args[3]);
		// Creamos lista de consultas.
		ArrayList<Consulta> consultas = parserXML.parserNeeds();
		
		// Creamos el parser para la consulta.
		QueryParser parser = new QueryParser(Version.LUCENE_44, "description", analyzer);
		
		for(int i=0; i<1/*consultas.size()*/; i++){	// Recorremos las consultas.
			 Consulta consulta = consultas.get(i); // Obtenemos la consulta.
			 String texto = consulta.getNecesidad().trim();	// Normalizamos texto.
			 Query query = parser.parse(texto);	// Creamos la query.
			 String [] tokens = obtenerTokens(query);	// Se obtienen los tokens.
			 // Creamos la lista de etiquetas a buscar.
			 ArrayList<Etiqueta> campos = new ArrayList<Etiqueta>();
			 campos.add(new Etiqueta("description",texto));
			 campos.add(new Etiqueta("title",texto));
			 hacerConsulta(tokens,campos);	// Crea la consulta.
			 // Método que realiza la consulta.
			 realizarConsulta(consulta.getIdentificador(),searcher,campos,analyzer);	
		}
		
		ficheroSal.close();	// Cerramos el fichero de salida.
		reader.close();	// Cerramos el reader.
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
    
    /*
     * Obtiene los tokens de una consulta.
     */
    private static String[] obtenerTokens(Query query) {
    	
        String[] tokens = query.toString().split(" ");
        for(int i = 0; i< tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].lastIndexOf(":")+1);
            System.out.println(tokens[i]);
        }
        return tokens;
    }
    
    /*
     * Método que realiza la consulta final y escribe los resultados en un fichero.
     */
    private static void realizarConsulta(String identificador, IndexSearcher searcher, 
    				ArrayList<Etiqueta> etiquetas, Analyzer analyzer){
    	
    	String [] campos = new String[etiquetas.size()];
    	String [] contenidos = new String[etiquetas.size()];
    	for(int i=0; i<campos.length; i++){
    		campos [i] = etiquetas.get(i).getContenido();
    		contenidos [i] = etiquetas.get(i).getTitulo();
    	}
    	try{
    		Query query = MultiFieldQueryParser.parse(Version.LUCENE_44, contenidos, campos, analyzer);
    		searcher.search(query, 100);
    		TopDocs results = searcher.search(query, 10);
    	    ScoreDoc[] hits = results.scoreDocs;
    	    
    	    for(int i=0; i<results.totalHits; i++){
        		Document doc = searcher.doc(hits[i].doc);
        		System.out.println(i + ". " + doc.get("path"));
        		ficheroSal.printf(identificador + "\t" + doc.get("path"));
        		if(hits.length != i-1){
        			ficheroSal.println();
        		}
        	}
    	} catch(Exception e){
    		System.err.println("Error al realizar la consulta");
    	}
    }
  
    /*
     * Método que añade a la consulta final algunos campos extra.
     */
    private static void hacerConsulta(String [] tokens, ArrayList<Etiqueta> campos){
    	for(int i=0; i<tokens.length; i++){
    		if(idioma(tokens[i])){
    			campos.add(new Etiqueta("language",tokens[i]));
    		} if(publisher(tokens[i])){
    			campos.add(new Etiqueta("publisher",tokens[i]));
    		}
    	}
    }
    
    /*
     * Método que comprueba si el token es de algún idioma.
     */
    private static boolean idioma(String token){
    	
    	switch(token){		// Comprueba si es algún idioma.
    		case "españ": return true;	// Español.
    		case "ingl": return true;	// Inglés.
    		case "italian": return true;	// Italiano.
    		case "franz": return true;	// Francés.
    		case "aleman": return true;	// Alemán.
    		case "portugu": return true;	// Portugués.
    		default: return false;	
    	}
    }
    
    /*
     * Método que comprueba si es la entidad que publica documentos.
     */
    private static boolean publisher(String token){
    	return (token.equals("zaragoz") || token.equals("universidad")
    			|| token.equals("prens"));
    }
    
}
