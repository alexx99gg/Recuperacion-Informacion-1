
/**
 * Clase que representa una abstracción de un par
 * formado por la precisión y el recall.
 */
public class Par {

	private double recall;	// Valor del recall.
	private double precision;	// Valor de la precisión.
	
	/*
	 * Constructor de un objeto par.
	 */
	public Par(double precision, double recall){
		this.recall = recall;
		this.precision = precision;
	}

	/*
	 * Método que devuelve el valor del recall.
	 */
	public double getRecall() {
		return recall;
	}

	/*
	 * Método que fija el valor del recall.
	 */
	public void setRecall(int recall) {
		this.recall = recall;
	}

	/*
	 * Método que devuelve el valor de la precisión.
	 */
	public double getPrecision() {
		return precision;
	}

	/*
	 * Método que fija el valor de la precisión.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
}
