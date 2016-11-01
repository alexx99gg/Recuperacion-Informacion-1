package Trabajo;

/**
 * Clase que representa una abstracción de una consulta con su identificador
 * y una lista de las etiquetas que posee.
 */
public class Consulta {

	private String identificador;	// Identificador de la consulta.
	private String necesidad; // Lista de etiquetas.
	
	/*
	 * Método constructor de un objeto consulta a partir de un
	 * identificador y una lista de etiquetas.
	 */
	public Consulta(String identificador, String necesidad) {
		this.identificador = identificador;
		this.necesidad = necesidad;
	}

	/*
	 * Método que devuelve el identificador de la consulta.
	 */
	public String getIdentificador() {
		return identificador;
	}

	/*
	 * Método que fija el identificador de la consulta.
	 */
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	/*
	 * Método que devuelve la necesidad de información.
	 */
	public String getNecesidad() {
		return necesidad;
	}

	/*
	 * Método que fija la necesidad de información.
	 */
	public void setNecesidad(String necesidad) {
		this.necesidad = necesidad;
	}
	
}
