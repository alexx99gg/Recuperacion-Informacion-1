
/**
 * Clase que representa una abstracción de una consulta
 * en SPARQL con su identificador y la consulta.
 */
public class Consulta {

	private String identificador;	// Identificador de la consulta.
	private String consulta;	// Texto de la consulta.
	
	/*
	 * Método constructor de un objeto consulta.
	 */
	public Consulta(String iden, String consul){
		
		identificador = iden;
		consulta = consul;
	}

	/*
	 * Método que devuelve el identificador de una consulta.
	 */
	public String getIdentificador() {
		return identificador;
	}

	/*
	 * Método que fija el identificador de una consulta.
	 */
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	/*
	 * Método que devuelve el texto de la consulta.
	 */
	public String getConsulta() {
		return consulta;
	}

	/*
	 * Método que fija el texto de la consulta.
	 */
	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}
	
	
}
