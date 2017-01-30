package trabajo;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class SemanticSearcher {
	
	private static Model model;

	public static void main(String[] args) {
		crearGrafoSkos();
		model.write(System.out);
	}
	
	/*
	 * Crea un grafo RDF a partir de un vocabulario de conceptos SKOS.
	 */
	public static void crearGrafoSkos() {
		XMLParser parser = new XMLParser("data.skos");
		ArrayList<SkosEtiq> etiquetas = parser.parserSkos();		//Obtiene eiquetas del fichero.
		model =  ModelFactory.createDefaultModel();
		String skos = "http://trabajos_skos.com/skos#";
		//Se crean las propiedades altLabel y prefLabel.
		Property prefLabel = model.createProperty(skos + "prefLabel");
		Property altLabel = model.createProperty(skos + "altLabel");
		for(int i = 0; i< etiquetas.size(); i++) {
			//Se crea el recurso.
			Resource concepto = model.createResource(skos + etiquetas.get(i).getPrefLabel());
			//Se añaden las propiedades altLabel y prefLabel.
			concepto.addProperty(prefLabel,etiquetas.get(i).getPrefLabel());	
			ArrayList<String> alternativas = etiquetas.get(i).getAltLabel();
			for(int j = 0; j < alternativas.size(); j++) {
				concepto.addProperty(altLabel,alternativas.get(j));
			}
		}
	}
}
