
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/*
 * Clase que parsea un documento XML y devuelve una lista con 
 * las etiquetas y su contenido.
 */
public class XMLParser {
		
	// Lista de las etiquetas a leer para indexar.
	private static final String[] LISTADOCS = {"creator", "date", "description",
			"identifier", "language", "publisher", "title"};
	// Fichero de entrada.
    private static FileInputStream fis;
    private static String fichero;

    /*
     * Método constructor de un objeto XMLParser.
     */
	public XMLParser(String nombre) {
		
		try {
			fis = new FileInputStream(nombre);
			fichero = nombre;
		} catch (Exception ex) {
	         System.err.println("Fichero " + nombre + " no encontrado.");
		}	
	}
	
	/*
	 * Método que recorre la estructurra devuelta por el parser
	 * y crea la lista de etiquetas de un documento a indexar.
	 */
	public ArrayList<Etiqueta> parserDocs() {

		// Crea el factory para parsear el documento.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Lista con las etiquetas.
		ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
	    ArrayList<String> titulos = new ArrayList<String>();
	    ArrayList<String> identificadores = new ArrayList<String>();
	    ArrayList<String> descripciones = new ArrayList<String>();
	    ArrayList<String> idiomas = new ArrayList<String>();
	    ArrayList<String> autores = new ArrayList<String>();
	    ArrayList<String> publicadores = new ArrayList<String>();
	    ArrayList<String> fechas = new ArrayList<String>();
	    try {
	    	// Crea el objeto que hará el parser del XML.
	    	DocumentBuilder builder = factory.newDocumentBuilder();

	    	// Crea el nuevo documento.
	    	InputSource is = new InputSource(fis);
	    	Document doc = builder.parse(is);

	    	// Obtenemos el primer elemento.
	    	Element element = doc.getDocumentElement();

	    	// Obtenemos una lista con las etiquetas y contenidos.
	    	NodeList nodes = element.getChildNodes();

	    	// Recorremos todos los nodos hijos.
	    	int i = 0;		// Indice para el recorrido.
	    	boolean finalizado = false;	// Booleano para terminar.	
	    	while(!finalizado) {	// Recorremos los nodos.
	        	// Obtenemos el nombre y el contenido.
	        	String nombre = nodes.item(i).getNodeName()
	        			.substring(nodes.item(i).getNodeName().lastIndexOf(":")+1);
	        	String contenido = nodes.item(i).getTextContent();
	        	i = i + 1;
	        	if(estaEnLista(nombre, LISTADOCS)) {	// Si está en la lista...
	        		// Se añade a su respectiva lista.
	        		switch(nombre){		// Se comprueba el nombre de la etiqueta.
	        		case "creator":
	        			if(!autores.contains(contenido)){
	        				autores.add(contenido);
	        			}
	        			break;
	        		case "date":
	        			if(!fechas.contains(contenido)){
	        				fechas.add(contenido);
	        			}
	        			break;
	        		case "description":
	        			if(!descripciones.contains(contenido)){
	        				descripciones.add(contenido);
	        			}
	        			break;
	        		case "identifier":
	        			if(!identificadores.contains(contenido)){
	        				identificadores.add(contenido);
	        			}
	        			break;
	        		case "language":
	        			String idioma = obtenerIdioma(contenido);
	        			if(!idiomas.contains(idioma)){
	        				idiomas.add(idioma);
	        			}
	        			break;
	        		case "publisher":
	        			if(!publicadores.contains(contenido)){
	        				publicadores.add(contenido);
	        			}
	        			break;
	        		case "title":
	        			if(!titulos.contains(contenido)){
	        				titulos.add(contenido);
	        			}
	        			break;
	        		default:
	        			break;
	        		}
	        		if ( i == nodes.getLength()) {
	        			finalizado = true;	// Se comprueba el límite.
	        		}
	        	}
	        	if ( i == nodes.getLength()) {
	        		finalizado = true;	// Se comprueba el límite.
	        	}
	        }
	        if(idiomas.isEmpty()){	// Miramos si no tenía idioma.
	        	idiomas.add("español");
	        }
	        etiq.add(new Etiqueta("Identificadores", identificadores));
	        etiq.add(new Etiqueta("Autores", autores));
	        etiq.add(new Etiqueta("Publicadores", publicadores));
	        etiq.add(new Etiqueta("Fechas", fechas));
	        etiq.add(new Etiqueta("Descripciones", descripciones));
	        etiq.add(new Etiqueta("Idiomas", idiomas));	        
	        etiq.add(new Etiqueta("Titulos", titulos));
	        return etiq;	// Se devuelve la etiqueta.
	    } catch (Exception e) {	// Se captura la posible excepción.
	    	System.err.println("Error en el parser "+ fichero + ": " + e.getMessage());
	    }
	    return etiq;
	}
	
	/*
	 * Método que realiza una búsqueda dicotómica sobre la lista
	 * para saber si una cierta etiqueta existe en la misma o no.
	 */
	private boolean estaEnLista(String titulo, String[] lista) {
		
		int n = lista.length;	// Obtiene el tamaño.
		int centro,inf=0,sup=n-1;	// Inicializa límites.
		while(inf<=sup){	// Bucle que recorre los elementos.
			centro=(sup+inf)/2;		// Actualiza el centro.
			if(lista[centro].equals(titulo)){
				return true;		// Si coincide con el centro...
			}
			else if(titulo.compareTo(lista[centro]) < 0){
				sup=centro-1;		// Si es menor, se actualiza superior...
			}
			else {
				inf=centro+1;		// Si es mayor, se actualiza inferior...
			}
		}
		return false;
	}
	
	/*
	 * Obtiene el nombre del idioma a partir de una abreviatura del mismo.
	 */
	private String obtenerIdioma(String abreviatura) {
		
		String idioma = "";
		switch(abreviatura) {
			case "spa":
				idioma = "español";
				break;
			case "eng":
				idioma = "ingles";
				break;
			case "ita":
				idioma = "italiano";
				break;
			case "fre":
				idioma = "frances";
				break;
			case "ger":
				idioma = "aleman";
				break;
			case "por":
				idioma = "portugues";
				break;
			case "en":
				idioma = "ingles";
				break;
			default:
				idioma = "";
		}
		return idioma;
	}
	
}
