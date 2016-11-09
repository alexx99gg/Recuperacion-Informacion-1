package practica3;

import java.util.ArrayList;

public class Medidas {

	private double precision = 0.0;
	private double recall = 0.0;
	private double f1 = 0.0;
	private double precision10 = 0.0;
	private double precisionProm = 0.0;
	private int totalConsultas = 0;
	private ArrayList<Double> precRecallInter = new ArrayList<Double>(11);
	int numMaxInterpoladas = 11;
	
	public Medidas(){
		
		for(int i=0; i< 11; i++){
			precRecallInter.add(0.0);
		}
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	
	public double getRecall() {
		return recall;
	}
	
	public void setRecall(double recall) {
		this.recall = recall;
	}
	
	public double getF1() {
		return f1;
	}
	
	public void setF1(double f1) {
		this.f1 = f1;
	}
	
	public double getPrecision10() {
		return precision10;
	}
	
	public void setPrecision10(double precision10) {
		this.precision10 = precision10;
	}
	
	public double getPrecisionProm() {
		return precisionProm;
	}
	
	public void setPrecisionProm(double precisionProm) {
		this.precisionProm = precisionProm;
	}
	
	public void actualizarTotal(){
		totalConsultas++;
	}
	
	public int getTotalConsultas(){
		return totalConsultas;
	}
	
	public void setPrecRecInter(ArrayList<Double> precRecall){
		
		for(int i=0; i<precRecall.size(); i++){
			precRecallInter.set(i, precRecallInter.get(i)+precRecall.get(i));
		}
		if(precRecall.size() < numMaxInterpoladas){
			numMaxInterpoladas = precRecall.size();
		}
	}
	
	public ArrayList<Double> getPrecRecInter(){
		return precRecallInter;
	}
	
	public int getNumInterpoladas(){
		return numMaxInterpoladas;
	}
}
