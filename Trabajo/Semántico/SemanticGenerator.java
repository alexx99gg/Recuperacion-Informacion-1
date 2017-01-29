import java.io.File;
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

	/*
	 * Método principal que lanza toda la ejecución.
	 */
	public static void main(String[] args){
		
		args = new String[6];
		
		args[5] = "recordsdc";
		
		//comprobarArgumentos(args);	// Se comprueban los argumentos.
		
		obtenerEtiquetas(args[5]);		// Se obtienen las etiquetas de los documentos.
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
	private static void obtenerEtiquetas(String direc){
		
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

		for(int i=0; i<199/*files.length*/; i++){		// Se recorre la lista...
			
			File fichero = new File(directorio,files[i]);
			XMLParser p = new XMLParser(fichero.getPath());		// Se crea el parser.

			ArrayList<Etiqueta> etiq = p.parserDocs();		// Se obtiene el contenido.
						
			for(int j = 0; j<etiq.size(); j++) {	
				ArrayList<String> contenido = etiq.get(j).getContenido();
				switch(etiq.get(j).getTitulo()){		// Se identifica cada etiqueta.
				case "Autores":
					// Se crean los recursos para los autores.
        			for(int k=0; k<contenido.size(); k++){
        				model.createResource(prefijo + contenido.get(k))
        					.addLiteral(nombrePer, contenido.get(k));
        				model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
        					.addProperty(autor, contenido.get(k));
        			}
        			break;
        		case "Fechas":
        			/*
        			 * Hay tipo fijo de fecha pero no se ponerlo.
        			 */
        			model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
        				.addLiteral(fecha, contenido.get(0));
        			break;
        		case "Descripciones":
        			model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
    					.addLiteral(descripcion, contenido.get(0));
        			break;
        		case "Identificadores":
        			// Se crea el recurso para el trabajo.
        			model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
						.addLiteral(identificador, contenido.get(etiq.get(0).getContenido().size()-1));
        			break;
        		case "Idiomas":
        			// Se crea el idioma.
        			model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
        				.addLiteral(idioma, contenido.get(0));
        			break;
        		case "Publicadores":
        			// Se crea el recurso para el publicador.
    				model.createResource(prefijo + contenido.get(0))
    					.addLiteral(nombreOrg, contenido.get(0));
    				model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
						.addProperty(organizacion, contenido.get(0));
        			break;
        		case "Titulos":
        			model.getResource(prefijo + etiq.get(0).getContenido().get(etiq.get(0).getContenido().size()-1))
						.addLiteral(titulo, contenido.get(0));
        			break;
        		default:
        			break;
        		}
			}
			
		}
		model.write(System.out);
		ResIterator iter = model.listSubjectsWithProperty(autor,"Pedro");
		while (iter.hasNext()) {
		    Resource r = iter.nextResource();
		    System.out.println(r);
		}
		
	}
}
