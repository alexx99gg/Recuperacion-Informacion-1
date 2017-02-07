
import java.util.ArrayList;

/**
 * Clase que representa una encapsulación de las medidas promedio
 * de varias consultas sobre distintas necesidades de información.
 */
public class Medidas {

	// Variables de las medidas que se van a guardar.
	private double precision = 0.0;
	private double recall = 0.0;
	private double precision10 = 0.0;
	private double precisionProm = 0.0;
	private int totalConsultas = 0;
	private ArrayList<Double> precRecallInter = new ArrayList<Double>(11);
	
	/*
	 * Constructor de un objeto medidas.
	 */
	public Medidas(){
		for(int i=0; i< 11; i++){
			precRecallInter.add(0.0);
		}
	}
	
	/*
	 * Método que devuelve la precisión.
	 */
	public double getPrecision() {
		return precision;
	}
	
	/*
	 * Método que fija la precisión.
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	
	/*
	 * Método que devuelve el recall.
	 */
	public double getRecall() {
		return recall;
	}
	
	/*
	 * Método que fija el recall.
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}
	
	/*
	 * Método que devuelve la precision10.
	 */
	public double getPrecision10() {
		return precision10;
	}
	
	/*
	 * Método que fija la precisión10.
	 */
	public void setPrecision10(double precision10) {
		this.precision10 = precision10;
	}
	
	/*
	 * Método que devuelve la precisión promedio.
	 */
	public double getPrecisionProm() {
		return precisionProm;
	}
	
	/*
	 * Método que fija la precisión promedio.
	 */
	public void setPrecisionProm(double precisionProm) {
		this.precisionProm = precisionProm;
	}
	
	/*
	 * Método que actualiza el número total de consultas.
	 */
	public void actualizarTotal(){
		totalConsultas++;
	}
	
	/*
	 * Método que devuelve el número total de consultas.
	 */
	public int getTotalConsultas(){
		return totalConsultas;
	}
	
	/*
	 * Método que fija los valores de precisión-recall interpolados.
	 */
	public void setPrecRecInter(ArrayList<Double> precRecall){
		
		for(int i=0; i<precRecall.size(); i++){
			precRecallInter.set(i, precRecallInter.get(i)+precRecall.get(i));
		}
	}
	
	/*
	 * Método que devuelve los valores de precisión-recall interpolados.
	 */
	public ArrayList<Double> getPrecRecInter(){
		return precRecallInter;
	}
	
}
