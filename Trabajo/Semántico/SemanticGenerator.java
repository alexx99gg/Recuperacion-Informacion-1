
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Clase encargada de generar el modelo RDF a partir de los documentos
 * XML.
 */
public class SemanticGenerator {
	
	private static final int IDENTIFICADOR = 0, AUTOR = 1, PUBLICADOR = 2, FECHA = 3, DESCRIPCION = 4,
			IDIOMA = 5, TITULO = 6;

	/*
	 * Método principal que lanza toda la ejecución.
	 */
	public static void main(String[] args){
		
		args = new String[6];
		
		args[1] = "grafo.rdf";
		args[5] = "recordsdc";
		
		//comprobarArgumentos(args);	// Se comprueban los argumentos.
		
		obtenerEtiquetas(args[5], args[1]);		// Se obtienen las etiquetas de los documentos.
	}
	
	/*
	 * Método que comprueba los argumentos con los que es llamado el
	 * programa.
	 */
	private static void comprobarArgumentos(String [] args){
		
		if(args.length != 6 || !args[0].equals("-rdf") || !args[2].equals("-skos")
				|| !args[4].equals("-docs")){
			System.err.println("Usar: java SemanticGenerator -rdf <rdfPath> "
					+ "-skos <skosPath> -docs <docsPath>");
		}
		File directorio = new File(args[5]);
		if(!directorio.isDirectory() || !directorio.canRead()){
			System.err.println("El directorio no existe o no se puede leer.");
		}
	}
	
	/*
	 * Método que obtiene las etiquetas de los documentos.
	 */
	private static void obtenerEtiquetas(String direc, String salida){
		
		File directorio = new File(direc);	// Se crea el objeto con el directorio.
		
		String[] files = directorio.list();	// Se obtiene la lista de ficheros.
		
		// Se crea un modelo vacío.
        Model model = ModelFactory.createDefaultModel();
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
        	
			contenido = etiq.get(AUTOR).getContenido();
			// Se crean los recursos para los autores.
        	for(int k=0; k<contenido.size(); k++){
        		model.createResource(prefijo + contenido.get(k))
        			.addLiteral(nombrePer, contenido.get(k));
        		model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        				.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        					.addProperty(autor, prefijo + contenido.get(k));
        	}
        	
        	contenido = etiq.get(PUBLICADOR).getContenido();
        	// Se crea el recurso para el publicador.
    		model.createResource(prefijo + contenido.get(0))
    			.addLiteral(nombreOrg, contenido.get(0));
    		model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
    				.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
						.addProperty(organizacion, prefijo + contenido.get(0));
        	
        	contenido = etiq.get(FECHA).getContenido();
        	/*
        	 * Hay tipo fijo de fecha pero no se ponerlo.
        	 */
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        				.addLiteral(fecha, contenido.get(0));
        	
        	contenido = etiq.get(DESCRIPCION).getContenido();
        			model.getResource(prefijo + etiq.get(IDENTIFICADOR)
        			.getContenido().get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
    					.addLiteral(descripcion, contenido.get(0));

        	contenido = etiq.get(IDIOMA).getContenido();
        	// Se crea el idioma.
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
        				.addLiteral(idioma, contenido.get(0));

    		contenido = etiq.get(TITULO).getContenido();
        	model.getResource(prefijo + etiq.get(IDENTIFICADOR).getContenido()
        			.get(etiq.get(IDENTIFICADOR).getContenido().size()-1))
						.addLiteral(titulo, contenido.get(0));
			
		}
		//model.write(System.out);
		ResIterator iter = model.listResourcesWithProperty(autor, "Pedro");
		while (iter.hasNext()) {
		    Resource r = iter.nextResource();
		    System.out.println(r);
		}
		
		try {		// Se guarda en un fichero el rdf.
			model.write(new FileOutputStream(new File(salida)));
		} catch (FileNotFoundException e) {		// Se muestra el posible error.
			System.err.println("Error al escribir el rdf.");
		}
	}
}
