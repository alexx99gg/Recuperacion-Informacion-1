import java.util.ArrayList;

/**
 * Clase que representa una abstracción de etiquetas
 * del modelo terminológico SKOS.
 */
public class SkosEtiq {

	private String prefLabel = null;	// Etiqueta prefLabel.
	private ArrayList<String> altLabel = null;	// Etiquetas altLabel.
	
	/*
	 * Método constructor de un objeto SkosEtiq.
	 */
	public SkosEtiq(String pref){
		
		prefLabel = pref;
		altLabel = new ArrayList<String>();
	}

	/*
	 *	Método que devuelve la etiqueta prefLabel.
	 */
	public String getPrefLabel() {
		return prefLabel;
	}

	/*
	 * Método que fija la etiqueta prefLabel.
	 */
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}

	/*
	 * Método que devuelve la etiqueta altLabel.
	 */
	public ArrayList<String> getAltLabel() {
		return altLabel;
	}

	/*
	 * Método que fija las etiquetas altLabel.
	 */
	public void setAltLabel(ArrayList<String> altLabel) {
		this.altLabel = altLabel;
	}
	
	/*
	 * Método que añade un label a la lista.
	 */
	public void anadirAltLabel(String label){
		altLabel.add(label);
	}
	
}
