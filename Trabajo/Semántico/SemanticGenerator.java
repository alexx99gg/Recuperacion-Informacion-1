import java.io.File;
import java.util.ArrayList;


/**
 * Clase encargada de generar el modelo RDF a partir de los documentos
 * XML.
 */
public class SemanticGenerator {

	/*
	 * Método principal que lanza toda la ejecución.
	 */
	public static void main(String[] args){
		
		args = new String[6];
		
		args[5] = "recordsdc";
		
		comprobarArgumentos(args);	// Se comprueban los argumentos.
		
		obtenerEtiquetas(args[5]);		// Se obtienen las etiquetas de los documentos.
	}
	
	/*
	 * Método que comprueba los argumentos con los que es llamado el
	 * programa.
	 */
	private static void comprobarArgumentos(String [] args){
		
	}
	
	/*
	 * Método que obtiene las etiquetas de los documentos.
	 */
	private static void obtenerEtiquetas(String direc){
		
		File directorio = new File(direc);	// Se crea el objeto con el directorio.
		
		String[] files = directorio.list();	// Se obtiene la lista de ficheros.

		for(int i=0; i<files.length; i++){		// Se recorre la lista...
			
			File fichero = new File(directorio,files[i]);
			XMLParser p = new XMLParser(fichero.getPath());		// Se crea el parser.

			ArrayList<Etiqueta> etiq = p.parserDocs();		// Se obtiene el contenido.
			
			for(int j = 0; j<etiq.size(); j++) {
				System.out.println(etiq.get(j).getTitulo() + "\n");
				ArrayList<String> contenido = etiq.get(j).getContenido();
				for(int k=0; k<contenido.size(); k++){
					String content = contenido.get(k);
					System.out.println(content + "\n");
				}
			}
		}
	}
}
