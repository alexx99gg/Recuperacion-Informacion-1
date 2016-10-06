
import java.io.FileInputStream;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {

	public static void main(String[] args) {

	      // create a new DocumentBuilderFactory
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	      try {
	         // use the factory to create a documentbuilder
	         DocumentBuilder builder = factory.newDocumentBuilder();

	         // create a new document from input source
	         FileInputStream fis = new FileInputStream("dublinCore/04-mdt200.xml");
	         InputSource is = new InputSource(fis);
	         Document doc = builder.parse(is);

	         // get the first element
	         Element element = doc.getDocumentElement();

	         // get all child nodes
	         NodeList nodes = element.getChildNodes();

	         // print the text content of each child
	         for (int i = 1; i < nodes.getLength(); i = i + 2) {
	        	String nombre = nodes.item(i).getNodeName().substring(nodes.item(i).getNodeName().lastIndexOf(":")+1);
	        	System.out.println(nombre);
	        	//System.out.println(nodes.item(i).getNodeName());
	        	String titulo = nodes.item(i).getNodeName();
	        	String contenido = nodes.item(i).getTextContent();
	        	Etiqueta etiq = new Etiqueta (titulo,contenido);
	            System.out.println("" + nodes.item(i).getTextContent());
	         }
	      } catch (Exception ex) {
	         ex.printStackTrace();
	      }
	   }
}
