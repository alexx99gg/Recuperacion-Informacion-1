package practica2;

import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Clase que realiza un parseo de un documento XML obteniendo todas las
 * etiquetas y sus contenidos.
 */
public class XMLParser {
	
	// Lista de etiquetas a obtener.
	private static final String[] LISTA = {"BoundingBox", "created", "creator","description","format",
		"identifier", "issued", "language","publisher","subject","temporal", "title","type",};
	// Ficheros a analizar.
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
	 * Método que recorre los documentos y devuelve una lista con
	 * las etiquetas y su contenido.
	 */
	public ArrayList<Etiqueta> crearEtiquetas() {

		// create a new DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Array con las etiquetas y su contenido.
		ArrayList<Etiqueta> etiq = new ArrayList<Etiqueta>();

		try {
			// Creamos un objeto para tratar los documentos.
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Creamos el documento del fichero de entrada.
			InputSource is = new InputSource(fis);
			Document doc = builder.parse(is);

			// Obtenemos el primer elemento.
			Element element = doc.getDocumentElement();

			// Obtenemos una lista con las etiquetas y contenidos.
			NodeList nodes = element.getChildNodes();

			// Bucle que recorre la lista.
			for(int i=1; i!= nodes.getLength(); i=i+2){
				// Obtenemos nombre y contenido.
				String nombre = nodes.item(i).getNodeName().substring(
							nodes.item(i).getNodeName().lastIndexOf(":")+1);
				String contenido = nodes.item(i).getTextContent();
				if(estaEnLista(nombre)) {	// Comprobamos si es etiqueta requerida.
					if(nombre.equals("BoundingBox")){	// Si es etiqueta espacial...
						// Obtenemos las coordendas.
						String [] coord = contenido.split(" +");
						double [] coordenadas = new double [4];
						int indice = 0;
						for(int j=0; j<coord.length; j++){
							if(coord[j].length()>1){
								coordenadas[indice] = Double.parseDouble(coord[j]);
								indice++;
							}
						}
						// Introducimos la etiqueta.
						Coordenadas coordenada = new Coordenadas(coordenadas[0],
								coordenadas[1],coordenadas[2],coordenadas[3]);
						etiq.add(new Etiqueta (nombre,coordenada));
					} else if((nombre.equals("issued") || nombre.equals("created"))
							&& contenido.length()>1){
						// Si es etiqueta temporal...
						// Obtenemos las partes de la fecha.
						String año = contenido.substring(0,contenido.indexOf("-"));
						String mes = contenido.substring(contenido.indexOf("-")+1,
									contenido.lastIndexOf("-"));
						String dia = contenido.substring(contenido.lastIndexOf("-")+1,
								contenido.length());
						// Creamos la fecha.
						String fecha = año+mes+dia;
						// Introducimos la etiqueta.
						etiq.add(new Etiqueta (nombre,fecha));
					} else if(nombre.equals("temporal")){
						// Si es temporal con contenido...
						if(contenido.indexOf("=")>-1){
							String begin = contenido.substring(contenido.indexOf("=")+1,
									contenido.indexOf(";"));
							String end = contenido.substring(contenido.lastIndexOf("=")+1,
									contenido.lastIndexOf(";"));
							// Partimos la fecha.
							String año = begin.substring(0,begin.indexOf("-"));
							String mes = begin.substring(begin.indexOf("-")+1,
										begin.lastIndexOf("-"));
							String dia = begin.substring(begin.lastIndexOf("-")+1,
									begin.length());
							// Creamos la fecha.
							String fecha = año+mes+dia;
							// Introducimos la etiqueta.
							etiq.add(new Etiqueta ("begin",Integer.parseInt(fecha)));
							// Partimos la fecha.
							año = end.substring(0,end.indexOf("-"));
							mes = end.substring(end.indexOf("-")+1,
										end.lastIndexOf("-"));
							dia = end.substring(end.lastIndexOf("-")+1,
									end.length());
							// Creamos la fecha.
							fecha = año+mes+dia;
							// Introducimos la etiqueta.
							etiq.add(new Etiqueta ("end",Integer.parseInt(fecha)));
						}
					} else{		// Si es etiqueta normal...
						// Introducimos la etiqueta.
						etiq.add(new Etiqueta (nombre,contenido));
					}
				}
			}
			return etiq;		// Devolvemos la lista de etiquetas.
		} catch (Exception ex) {	// Capturamos la excepción.
			ex.printStackTrace();
			return null;
		}
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
