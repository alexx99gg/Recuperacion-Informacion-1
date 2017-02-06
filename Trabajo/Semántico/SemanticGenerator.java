package trabajo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Clase encargada de generar el modelo RDF a partir de los documentos
 * XML.
 */
public class SemanticGenerator {
	
	private static final int IDENTIFICADOR = 0, AUTOR = 1, PUBLICADOR = 2, FECHA = 3, DESCRIPCION = 4,
			IDIOMA = 5, TITULO = 6, TEMA = 7;
	private static Model model;

	/*
	 * Método principal que lanza toda la ejecución.
	 */
	public static void main(String[] args){
		
		args = new String[6];
		args[0] = "-rdf";
		args[1] = "grafo.rdf";
		args[2] = "-skos";
		args[3] = "data.skos";
		args[4] = "-docs";
		args[5] = "recordsdc";
		
		if(comprobarArgumentos(args)){ // Se comprueban los argumentos.
			
			obtenerDocs(args[5]);		// Se obtienen los documentos.

			obtenerSkos(args[3], args[1]);		// Se obtene el skos.
			
		}
		
	}
	
	/*
	 * Método que comprueba los argumentos con los que es llamado el
	 * programa.
	 */
	private static boolean comprobarArgumentos(String [] args){
		
		if(args.length != 6 || !args[0].equals("-rdf") || !args[2].equals("-skos")
				|| !args[4].equals("-docs")){		// Se comprueban parámetros.
			System.err.println("Usar: java SemanticGenerator -rdf <rdfPath> "
					+ "-skos <skosPath> -docs <docsPath>");
			return false;
		}
		File dirDocs = new File(args[5]);		// Se mira el directorio de documentos.
		if(!dirDocs.isDirectory() || !dirDocs.canRead()){
			System.err.println("El directorio de documentos no existe o no se puede leer.");
			return false;
		}
		File fileSkos = new File(args[3]);			// Se mira fichero de skos.
		if(!fileSkos.isFile() || !fileSkos.canRead()){
			System.err.println("El archivo de skos no existe o no se puede leer.");
			return false;
		}
		return true;
	}
	
	/*
	 * Método que obtiene las etiquetas de los documentos.
	 */
	private static void obtenerDocs(String direc){
		
		File directorio = new File(direc);	// Se crea el objeto con el directorio.
		
		String[] files = directorio.list();	// Se obtiene la lista de ficheros.
		
		// Se crea un modelo vacío.
        model = ModelFactory.createDefaultModel();
        // Se crean las distintas propiedades.
        String prefijo = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
     	Property nombreOrg = model.createProperty(prefijo + "nombreOrg");
     	Property nombrePer = model.createProperty(prefijo + "nombrePer");
     	Property autor = model.createProperty(prefijo + "autor");
     	Property organizacion = model.createProperty(prefijo + "organizacion");
     	Property identificador = model.createProperty(prefijo + "identificador");
     	Property descripcion = model.createProperty(prefijo + "descripcion");
     	Property titulo = model.createProperty(prefijo + "titulo");
     	Property fecha = model.createProperty(prefijo + "fecha");
     	Property idioma = model.createProperty(prefijo + "idioma");
     	Property tema = model.createProperty(prefijo + "tema");
     	
     	/*
		 * FALTA TEMAS QUE VA CON EL SKOS
		 */

		for(int i=0; i<files.length; i++){		// Se recorre la lista...
			
			File fichero = new File(directorio,files[i]);
			XMLParser p = new XMLParser(fichero.getPath());		// Se crea el parser.

			ArrayList<Etiqueta> etiq = p.parserDocs();		// Se obtienen las etiquetas.

			ArrayList<String> contenido = etiq.get(IDENTIFICADOR).getContenido();
        	// Se crea el recurso para el trabajo.
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
				.addLiteral(identificador, 
						contenido.get(etiq.get(IDENTIFICADOR).getContenido().size()-1));
        	
        	// Se crean los recursos para los autores.
			contenido = etiq.get(AUTOR).getContenido();
        	for(int k=0; k<contenido.size(); k++){
        		model.createResource(prefijo + contenido.get(k))
        			.addLiteral(nombrePer, contenido.get(k));
        		model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        				.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        					.addProperty(autor, prefijo + contenido.get(k));
        	}
        	
        	// Se crea el publicador.
        	contenido = etiq.get(PUBLICADOR).getContenido();
        	// Se crea el recurso para el publicador.
    		model.createResource(prefijo + contenido.get(0))
    			.addLiteral(nombreOrg, contenido.get(0));
    		model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
    				.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
						.addProperty(organizacion, prefijo + contenido.get(0));
        	
    		// Se crea la fecha.
    		contenido = etiq.get(FECHA).getContenido();  		
    		for(int k=0; k<contenido.size(); k++){
    			Literal literal = model.createTypedLiteral(contenido.get(k), XSDDatatype.XSDgYear);
        		// Se inserta la fecha.
            	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
            			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
            				.addProperty(fecha, literal);
    		}
        	
    		// Se crea la descripción.
        	contenido = etiq.get(DESCRIPCION).getContenido();
        			model.getResource(prefijo + etiq.get(IDENTIFICADOR)
        			.getContenido().get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
    					.addLiteral(descripcion, contenido.get(0));

        	// Se crea el idioma.
        	contenido = etiq.get(IDIOMA).getContenido();
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        				.addLiteral(idioma, contenido.get(0));

        	// Se crea el título.
    		contenido = etiq.get(TITULO).getContenido();
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
						.addLiteral(titulo, contenido.get(0));
        	
        	//Se crean temas.
        	contenido = etiq.get(TEMA).getContenido();
        	for(int k=0; k<contenido.size(); k++){
        		model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        				.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        					.addProperty(tema, prefijo + contenido.get(k));
        	}
			
		}
		
	}
	
	/*
	 * Método que obtiene el modelo terminológico del fichero y lo añade
	 * al sistema.
	 */
	private static void obtenerSkos(String fich, String salida){
		
		File fichero = new File(fich);	// Se crea el objeto con el fichero.	
		XMLParser parser = new XMLParser(fichero.getPath());	// Se crea el parser
		
		ArrayList<SkosEtiq> etiquetas = parser.parserSkos();		// Obtiene eiquetas del fichero.

		String skos = "http://trabajos_skos.com/skos#";	// Prefijo del skos.
		// Se crean las propiedades altLabel y prefLabel.
		Property prefLabel = model.createProperty(skos + "prefLabel");
		Property altLabel = model.createProperty(skos + "altLabel");
		
		for(int i = 0; i< etiquetas.size(); i++) {		// Se recorren las etiquetas.
			// Se crea el recurso.
			Resource concepto = model.createResource(skos + etiquetas.get(i).getPrefLabel());
			// Se añaden las propiedades altLabel y prefLabel.
			concepto.addProperty(prefLabel,etiquetas.get(i).getPrefLabel());	
			ArrayList<String> alternativas = etiquetas.get(i).getAltLabel();
			
			for(int j = 0; j < alternativas.size(); j++) {
				concepto.addProperty(altLabel,alternativas.get(j));
			}
		}
        
        model.write(System.out);
        
		try {		// Se guarda en un fichero el rdf.
			model.write(new FileOutputStream(new File(salida)));
		} catch (FileNotFoundException e) {		// Se muestra el posible error.
			System.err.println("Error al escribir el rdf.");
		}
	}
	
	
}
