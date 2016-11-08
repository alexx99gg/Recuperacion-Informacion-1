package practica3;

/**
 * Clase que representa una abstracción de una serie
 * de coordenadas geográficas.
 */
public class Coordenadas {

	// Nombre de las variables de las coordenadas.
	private double norte;
	private double sur;
	private double este;
	private double oeste;
	
	/*
	 * Método constructor de un objeto Coordenadas.
	 */
	public Coordenadas(double supDerecha1, double supDerecha2,
				double infIzquierda1, double infIzquierda2){
		
		oeste = infIzquierda1;
		sur = infIzquierda2;
		este = supDerecha1;
		norte = supDerecha2;
	}

	/*
	 * Método que devuelve la coordenada norte.
	 */
	public double getNorte() {
		return norte;
	}

	/*
	 * Método que fija la coordenada norte.
	 */
	public void setNorte(double norte) {
		this.norte = norte;
	}

	/*
	 * Método que devuelve la coordenada sur.
	 */
	public double getSur() {
		return sur;
	}

	/*
	 * Método que fija la coordenada sur.
	 */
	public void setSur(double sur) {
		this.sur = sur;
	}

	/*
	 * Método que devuelve la coordenada este.
	 */
	public double getEste() {
		return este;
	}

	/*
	 * Método que fija la coordenada este.
	 */
	public void setEste(double este) {
		this.este = este;
	}

	/*
	 * Método que devuelve la coordenada oeste.
	 */
	public double getOeste() {
		return oeste;
	}

	/*
	 * Método que fija la coordenada oeste.
	 */
	public void setOeste(double oeste) {
		this.oeste = oeste;
	}

}
