
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {
	
	private static final String[] LISTA = {"creator","description","format", "identifier",
			"language","publisher","subject","title","type"};
    private static FileInputStream fis;

	public XMLParser(String nombre) {
		try {
			fis = new FileInputStream(nombre);
		} catch (Exception ex) {
	         ex.printStackTrace();
	      }
	}
	public ArrayList<Etiqueta> crearEtiquetas() {

	      // create a new DocumentBuilderFactory
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	      ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();
	      ArrayList<String> titulos = new ArrayList<String>();
	      try {
	         // use the factory to create a documentbuilder
	         DocumentBuilder builder = factory.newDocumentBuilder();

	         // create a new document from input source
	         InputSource is = new InputSource(fis);
	         Document doc = builder.parse(is);

	         // get the first element
	         Element element = doc.getDocumentElement();

	         // get all child nodes
	         NodeList nodes = element.getChildNodes();

	         // print the text content of each child
	         int i =1, limite = 18;
	         boolean finalizado = false;
	        while(!finalizado) {
	        	String nombre = nodes.item(i).getNodeName().substring(nodes.item(i).getNodeName().lastIndexOf(":")+1);
	        	//System.out.println(nombre);
	        	//System.out.println(nodes.item(i).getNodeName());
	        	//String titulo = nodes.item(i).getNodeName();
	        	String contenido = nodes.item(i).getTextContent();
        		i = i + 2;
	        	if(estaEnLista(nombre)) {
		        	if(titulos.contains(nombre)) {
		        		int indice = titulos.indexOf(nombre);
		        		/*etiq.get(indice).setContenido(etiq.get(indice).
		        				getContenido()+"\n"+contenido);*/
		        		limite = limite + 2;;
		        	} else {
		        		titulos.add(nombre);
		        		etiq.add(new Etiqueta (nombre,contenido));
		        	}
		        	if ( i == limite || i >= nodes.getLength()) {
		        		finalizado = true;
		        	}
	        	} else {
	        		limite = limite +2;
	        		if (i >= nodes.getLength()) {
		        		finalizado = true;
		        	}
	        	}
	         }
	        return etiq;
	      } catch (Exception ex) {
	         ex.printStackTrace();
	      }
	      return etiq;
	   }
	
	private boolean estaEnLista(String titulo) {
		for(int i = 0; i < LISTA.length; i++) {
			if(titulo.compareTo(LISTA[i])<0) {
				return false;
			} else if(titulo.compareTo(LISTA[i])==0) {
				return true;
			}
		}
		return false;
	}
}
