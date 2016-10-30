package Trabajo;

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
	
	// Lista de las etiquetas a leer.
	private static final String[] LISTA = {"creator", "date", "description",
			"identifier", "language", "publisher", "title"};
	// Fichero de entrada.
    private static FileInputStream fis;

    /*
     * Método constructor de un objeto XMLParser.
     */
	public XMLParser(String nombre) {
		
		try {
			fis = new FileInputStream(nombre);
		} catch (Exception ex) {
	         ex.printStackTrace();
		}
		
	}
	
	/*
	 * Método que recorre la estructurra devuelta por el parser
	 * y crea la lista de etiquetas.
	 */
	public ArrayList<Etiqueta> crearEtiquetas() {

		// Crea el factory para parsear el documento.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Lista con las etiquetas.
		ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
	    ArrayList<String> titulos = new ArrayList<String>();
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

	    	// print the text content of each child
	    	int i = 0;
	    	boolean finalizado = false;
	        while(!finalizado) {	// Recorremos las etiquetas.
	        	// Obtenemos el nombre y el contenido.
	        	String nombre = nodes.item(i).getNodeName()
	        			.substring(nodes.item(i).getNodeName().lastIndexOf(":")+1);
	        	String contenido = nodes.item(i).getTextContent();
	        	i = i + 1;
	        	if(estaEnLista(nombre)) {	// Si está en la lista...
	        		// Si ya está en la lista, se concatena al final.
	        		if(titulos.contains(nombre)) {
	        			int indice = titulos.indexOf(nombre);
	        			etiq.get(indice).setContenido(etiq.get(indice).
	        					getContenido()+"\n"+contenido);
	        		} else {	// Si no está en la lista, se añade.
	        			titulos.add(nombre);
	        			etiq.add(new Etiqueta (nombre,contenido));
	        		}
	        		if ( i == nodes.getLength()) {
	        			finalizado = true;	// Se comprueba el límite.
	        		}
	        	}
	        	if ( i == nodes.getLength()) {
	        		finalizado = true;	// Se comprueba el límite.
	        	}
	        }
	        return etiq;	// Se devuelve la etiqueta.
	    } catch (Exception e) {	// Se captura la posible excepción.
	    	System.err.println("Error en el parser: " + e.getMessage());
	    }
	    return etiq;
	}
	
	/*
	 * Método que realiza una búsqueda dicotómica sobre la lista
	 * para saber si una cierta etiqueta existe en la misma o no.
	 */
	private boolean estaEnLista(String titulo) {
		
		int n = LISTA.length;	// Obtiene el tamaño.
		int centro,inf=0,sup=n-1;	// Inicializa límites.
		while(inf<=sup){	// Bucle que recorre los elementos.
			centro=(sup+inf)/2;		// Actualiza el centro.
			if(LISTA[centro].equals(titulo)){
				return true;		// Si coincide con el centro...
			}
			else if(titulo.compareTo(LISTA[centro]) < 0){
				sup=centro-1;		// Si es menor, se actualiza superior...
			}
			else {
				inf=centro+1;		// Si es mayor, se actualiza inferior...
			}
		}
		return false;
	}
}
