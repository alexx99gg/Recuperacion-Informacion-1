import java.io.File;

/**
 * Clase que realiza las consultas en SPARQL sobre
 * el modelo RDF pasado como parámetro.
 */
public class SemanticSearcher {

	/*
	 * Método principal que lanza toda la ejecución.
	 */
	public static void main(String[] args){
		
		args = new String[8];
		args[0] = "-rdf";
		args[1] = "grafo.rdf";
		args[2] = "-rdfs";
		args[3] = "";
		args[4] = "-infoNeeds";
		args[5] = "recordsdc";
		args[6] = "-output";
		args[7] = "salida.txt";
		
		if(comprobarArgumentos(args)){ // Se comprueban los argumentos.
			
		}
		
	}
	
	/*
	 * Método que comprueba los argumentos con los que es llamado el
	 * programa.
	 */
	private static boolean comprobarArgumentos(String [] args){
		
		if(args.length != 8 || !args[0].equals("-rdf") || !args[2].equals("-rdfs")
				|| !args[4].equals("-infoNeeds") || !args[6].equals("-output")){		// Se comprueban parámetros.
			System.err.println("Usar: java SemanticGenerator -rdf <rdfPath> "
					+ "-skos <skosPath> -docs <docsPath>");
			return false;
		}
		File fileRDFS = new File(args[3]);			// Se mira fichero de rdfs.
		if(!fileRDFS.isFile() || !fileRDFS.canRead()){
			System.err.println("El archivo de rdfs no existe o no se puede leer.");
			return false;
		}
		File fileRDF = new File(args[1]);			// Se mira fichero de rdf.
		if(!fileRDF.isFile() || !fileRDF.canRead()){
			System.err.println("El archivo de rdf no existe o no se puede leer.");
			return false;
		}
		File fileNeeds = new File(args[5]);			// Se mira fichero de necesidades.
		if(!fileNeeds.isFile() || !fileNeeds.canRead()){
			System.err.println("El archivo de consultas no existe o no se puede leer.");
			return false;
		}
		return true;
	}
}
