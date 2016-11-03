package Trabajo;

/**
 * Clase que representa una abstracción de una etiqueta de 
 * un documento XML con título y contenido.
 */
public class Etiqueta {

	private String titulo;	// Título del documento.
	private String contenido;	// Contenido del documento.

	/*
	 * Constructor de un objeto Etiqueta.
	 */
	public Etiqueta (String titulo, String contenido){
		this.titulo = titulo;
		this.contenido = contenido;
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
}
