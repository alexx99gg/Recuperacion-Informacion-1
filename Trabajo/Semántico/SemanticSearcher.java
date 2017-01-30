package trabajo;

import java.io.File;
import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

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
		args[5] = "necesidades.txt";
		args[6] = "-output";
		args[7] = "salida.txt";
		
		if(true/*comprobarArgumentos(args)*/){ // Se comprueban los argumentos.
			//Obtenemos el modelo del fichero.
			FileManager.get().addLocatorClassLoader(SemanticSearcher.class.getClassLoader());
			Model model = FileManager.get().loadModel(args[1]);
			//Se obtiene la consulta.
			ArrayList<Consulta> consultas = ConsultaParser.obtenerConsulta(new File(args[5]));
			for(int i=0; i<1; i++){
				Query query = QueryFactory.create(consultas.get(i).getConsulta());
				//Se ejecuta la consulta sobre el modelo.
				QueryExecution qexec = QueryExecutionFactory.create(query,model);
				ResultSet resultados = qexec.execSelect();
				if(resultados != null) {
					//Si hay resultados se muestran.
					while(resultados.hasNext()) {
						QuerySolution sol = resultados.next();
						Resource nombre = sol.getResource("x");
						System.out.println(nombre.getURI());
					}
				}
				// Escribir resultados en fichero salida.
				/*PrintWriter ficheroSal = new PrintWriter(new FileWriter(args[7]));
				ficheroSal.printf(identificador + "\t" + doc.get("path"));*/
			}
			
			
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
