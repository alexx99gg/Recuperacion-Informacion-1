package practica2;

public class Coordenadas {

	private double norte;
	private double sur;
	private double este;
	private double oeste;
	
	public Coordenadas(double supDerecha1, double supDerecha2,
				double infIzquierda1, double infIzquierda2){
		
		oeste = infIzquierda1;
		sur = infIzquierda2;
		este = supDerecha1;
		norte = supDerecha2;
	}

	
	public double getNorte() {
		return norte;
	}

	public void setNorte(double norte) {
		this.norte = norte;
	}

	public double getSur() {
		return sur;
	}

	public void setSur(double sur) {
		this.sur = sur;
	}

	public double getEste() {
		return este;
	}

	public void setEste(double este) {
		this.este = este;
	}

	public double getOeste() {
		return oeste;
	}

	public void setOeste(double oeste) {
		this.oeste = oeste;
	}

	public boolean intersecta(Coordenadas coord){
		
		return false;
	}
}
