package practica2;

/**
 * Clase que representa una abstracción de una etiqueta de 
 * un documento XML con título y contenido.
 */
public class Etiqueta {

	private String titulo;	// Título del documento.
	private String contenido;	// Contenido del documento.
	private Coordenadas coordenadas; // Coordenadas si es bounding box.
	private int fecha;	// Fecha temporal.

	/*
	 * Constructor de un objeto Etiqueta.
	 */
	public Etiqueta (String titulo, String contenido){
		this.titulo = titulo;
		this.contenido = contenido;
	}
	
	/*
	 * Constructor de un objeto Etiqueta.
	 */
	public Etiqueta (String titulo, Coordenadas coordenadas){
		this.titulo = titulo;
		this.coordenadas = coordenadas;
	}
	
	/*
	 * Constructor de un objeto Etiqueta.
	 */
	public Etiqueta (String titulo, int fecha){
		this.titulo = titulo;
		this.fecha = fecha;
	}

	/*
	 * Método que devuelve el título de la etiqueta.
	 */
	public String getTitulo() {
		return titulo;
	}

	/*
	 * Método que fija el título de la etiqueta.
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/*
	 * Método que devuelve el contenido de la etiqueta.
	 */
	public String getContenido() {
		return contenido;
	}

	/*
	 * Método que fija el contenido de la etiqueta.
	 */
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	/*
	 * Método que devuelve el array de coordenadas de la etiqueta.
	 */
	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

	/*
	 * Método que fija las coordenadas de la etiqueta.
	 */
	public void setCoordenadas(Coordenadas coordenadas) {
		this.coordenadas = coordenadas;
	}
	
	/*
	 * Método que devuelve la fecha temporal de la etiqueta.
	 */
	public int getFecha() {
		return fecha;
	}

	/*
	 * Método que fija la fecha temporal de la etiqueta.
	 */
	public void setFecha(int fecha) {
		this.fecha = fecha;
	}
}
