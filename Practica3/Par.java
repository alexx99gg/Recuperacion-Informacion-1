package practica3;

public class Par {

	private double recall;
	private double precision;
	
	public Par(double precision, double recall){
		this.recall = recall;
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(int recall) {
		this.recall = recall;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
}
