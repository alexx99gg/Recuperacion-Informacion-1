package Trabajo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

/** 
 * Clase que indexa todos los ficheros en un cierto directorio.
 */
public class IndexFiles {

	private static File docDir; // Directorio de los documentos a indexar.
	private static Directory dir; // Directorio donde se guarda el índice.
	
	/*
	 * Método principal que indexa todos los ficheros en un directorio dado.
	 */
	public static void main(String[] args) {
		
		args = new String [4];
		args[0] = "-index";
		args[1] = "indexTrabajo";
		args[2] = "-docs";
		args[3] = "recordsdc";
		
		comprobarArgumentos(args);	// Se comprueban los argumentos.
		
		Date start = new Date();	// Obtiene la fecha de inicio.
	    try {
	      System.out.println("Indexando en directorio " + args[1] + "...");

	      // Crea el analizador en español.
	      Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_44);
	      
	      // Crea el objeto para configurar el indexador de documentos.
	      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);

	      // Indexa en el directorio borrando cualquier índice anterior.
	      iwc.setOpenMode(OpenMode.CREATE);

	      // Crea el objeto índice para indexar los documentos.
	      IndexWriter writer = new IndexWriter(dir, iwc);
	      indexarDocumentos(writer, docDir);		// Indexa los documentos.

	      writer.close();	// Cierra el indexador.

	      Date end = new Date();		// Obtiene la fecha de finalización.
	      // Muestra el tiempo dedicado a indexar.
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	    } catch (IOException e) {
	      System.err.println(" Error al indexar los documentos:\n"
	    		  +e.getMessage());
	    }
	}

	/*
	 * Método que comprueba si los argumentos de la invocación son correctos.
	 */
	public static void comprobarArgumentos(String [] argumentos){
		
		if(argumentos.length != 4){	// Comprueba el número de argumentos.
			System.err.println("Usar: java IndexFiles -index <indexPath>"
					+ " -docs <docsPath>");
			System.exit(-1);
		}
		if(!argumentos[0].equals("-index")){		// Comprueba primer argumento.
			System.err.println("Primer argumento: -index");
			System.exit(-1);
		}
		if(!argumentos[2].equals("-docs")){		// Comprueba segundo argumento.
			System.err.println("Segundo argumento: -docs");
			System.exit(-1);
		}
		docDir = new File(argumentos[3]);
		try{
			dir = FSDirectory.open(new File(argumentos[1]));
			if(!docDir.exists() || !docDir.canRead()){
				System.err.println("El directorio "+ docDir.getAbsolutePath()
						+ " no existe o no se puede leer");
				System.exit(-1);
			}
		} catch(IOException e){
			System.err.println("Error con el directorio " + argumentos[1]
					+ " a indexar");
			System.exit(-1);
		}
		
	}

	/*
	* Método que indexa todos los documentos a partir de un cierto
	* índice pasado como parámetro.
	*/
	private static void indexarDocumentos(IndexWriter writer, File file)
			throws IOException {
		
		if (file.canRead()) {		// Comprueba si se puede leer el directorio.
			if (file.isDirectory()) {	// Comprueba si es directorio.
				// Si es directorio...
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexarDocumentos(writer, new File(file, files[i]));
					}
				}
			} else {	// Si es fichero...
				FileInputStream fis;	// Creamos el objeto para leer.
				
				try {	// Asignamos el objeto al fichero.
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					// Capturamos la posible excepción.
					System.err.println("El archivo " + file.getAbsolutePath()
							+ " ha dado error.");
					return;
				}
				
				try {
					Document doc = new Document();	// Crea un objeto documento.

					// Añaidmos el path.
					Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
					doc.add(pathField);

					// Añade la fecha de la última modificación.
					doc.add(new LongField("modified", file.lastModified(), Field.Store.YES));

					// Crea el parser para el documento.
					XMLParser p = new XMLParser(file.getPath());
					// Obtiene las etiquetas del documento.
					ArrayList<Etiqueta> etiq = p.parserDocs();
					// Recorre las etiquetas indexando el contenido.
					for(int i = 0; i<etiq.size(); i++) {
						// Indica por pantalla que se está indexando.
						System.out.println("Indexando etiqueta" + file.getPath()+" : "
										+etiq.get(i).getTitulo());
						// Indexa la etiqueta.
						if(etiq.get(i).getTitulo().equals("date")){
							// Comprueba si es fecha.
							doc.add(new IntField(etiq.get(i).getTitulo(), 
									etiq.get(i).getFecha(),Field.Store.YES));
						} else{	// Si es otro caso...
							doc.add(new TextField(etiq.get(i).getTitulo(), 
									new BufferedReader(new StringReader(etiq.get(i).getContenido()))));
						}
					}
					// Indica por pantalla el documento indexado.
					System.out.println("Indexando documento: " +  file);
					writer.addDocument(doc);    	// Indexa el documento.  
				} finally {
					fis.close();		// Se cierra el canal.
				}
			}
		} else{	// Se indica que le directorio no se puede leer.
			System.err.println("El directorio " + file.getAbsolutePath() 
					+ " no se puede leer.");		
		}
	}

}
