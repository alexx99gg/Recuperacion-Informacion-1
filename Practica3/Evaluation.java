package practica3;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * Clase con métodos necesarios para realizar la evaluación de un cierto
 * sistema de información.
 */
public class Evaluation {

	private static Scanner docRecuperados;	// Fichero para leer resultados.
	private static Scanner relevancia;	// Fichero con juicios.
	private static PrintWriter resultados;	// Fichero de resultados.
	private static Map<Integer, Map<Integer, Integer>> juicios = 
			new HashMap<Integer, Map<Integer, Integer>> (35); 	// Map con los juicios de relevancia.
	private static Medidas medidas = new Medidas();
	
	/*
	 * Método principal que lanza la evaluación del sistema de información
	 * y comprueba los parámetros con los que es llamado.
	 */
	public static void main (String [] args){
		
		args = new String[6];
		args[0] = "-qrels";
		args[1] = "qrels.txt";
		args[2] = "-results";
		args[3] = "results.txt";
		args[4] = "-output";
		args[5] = "salida.txt";
		
		comprobarArgumentos(args);	// Comprueba los argumentos.
		
		recuperarJuicios();	// Recupera los juicios de relevancia.
		
		calcularMedidas();	// Calculas las diferentes medidas.
		
		medidasGlobales();	// Escribe las medidas globales.
		
		resultados.close();	// Se cierra el fichero.
		
	}
	
	/*
	 * Método que comprueba si los argumentos de la invocación son correctos.
	 */
	private static void comprobarArgumentos(String [] argumentos){
		
		if(argumentos.length != 6){	// Comprueba el número de argumentos.
			System.err.println("Usar: java Evaluation -qrels <qrelsFileName>"
					+ " -results <resultsFileName> -output <outputFileName>");
			System.exit(-1);
		}
		if(!argumentos[0].equals("-qrels")){		// Comprueba primer argumento.
			System.err.println("Primer argumento: -qrels");
			System.exit(-1);
		}
		if(!argumentos[2].equals("-results")){		// Comprueba segundo argumento.
			System.err.println("Segundo argumento: -results");
			System.exit(-1);
		}
		if(!argumentos[4].equals("-output")){		// Comprueba cuarto argumento.
			System.err.println("Segundo argumento: -output");
			System.exit(-1);
		}
		
		try{		// Creamos el fichero de salida y entrada.
			resultados = new PrintWriter(new FileWriter(argumentos[5]));
			docRecuperados = new Scanner(new File(argumentos[3]));
			relevancia = new Scanner(new File(argumentos[1]));
		} catch(Exception e){	// Capturamos las posibles excepciones.
			System.err.println("Error con alguno de los ficheros");
		}
	}
	
	/*
	 * Método que recupera los juicios de relevancia de los documentos y los guarda
	 * en una estructura de tipo hash.
	 */
	private static void recuperarJuicios(){
		
		int necesidad, docId, rele, necAnterior = 0;		// Datos a leer.
		boolean primero = true;
		Map<Integer, Integer> docRelevancia = new HashMap<Integer, Integer>(40);
		while(relevancia.hasNextLine()){	// Se recorren todas las líneas.
			// Se leen los tres datos.
			necesidad = relevancia.nextInt();
			docId = relevancia.nextInt();
			rele = relevancia.nextInt();
			if(primero){necAnterior = necesidad; primero = false;}
			if(necAnterior != necesidad){
				juicios.put(necAnterior, docRelevancia);
				necAnterior = necesidad;
				docRelevancia = new HashMap<Integer, Integer>(40);
			}
			docRelevancia.put(docId, rele);
			relevancia.nextLine();	// Se pasa a la siguiente línea.
		}
		juicios.put(necAnterior, docRelevancia);
	}
	
	/*
	 * Método que realiza los distintos cálculos para las necesidades de información.
	 */
	private static void calcularMedidas(){
		
		double relevante = 0, total = 0;		// Variables para el cálculo.
		double totalRelevante = 0;	// Número total de documentos relevantes.
		int necesidad = 0, docId, necAnterior = 0;	// Variables para leer del fichero.
		// Variables para la evaluación.
		double precision10 = 0.0;
		double precisionProm = 0.0;
		boolean primero = true;	// Booleano para saber si es el primero.
		ArrayList<Par> precisionRecall = null;
		while(docRecuperados.hasNextLine()){	// Se recorre todo el fichero.
			necesidad = docRecuperados.nextInt();	// Se leen las variables del fichero.
			docId = docRecuperados.nextInt();
			if(primero){
				necAnterior = necesidad; primero = false;
				Map <Integer, Integer> recallDoc = juicios.get(necAnterior);
				totalRelevante = obtenerRelevantes(recallDoc);
				precisionRecall = new ArrayList<Par>(recallDoc.size());
			}
			if(necAnterior != necesidad){	// Comprueba si se ha terminado la primera necesidad.
				// Calcular medidas.
				calcularMedidas(relevante, total, totalRelevante, precisionRecall, 
						precision10, precisionProm, Integer.toString(necAnterior));
				necAnterior = necesidad;
				relevante = 0; total = 0; precisionProm = 0.0;
				Map <Integer, Integer> recallDoc = juicios.get(necesidad);
				totalRelevante = obtenerRelevantes(recallDoc);
				precisionRecall = new ArrayList<Par>(recallDoc.size());
			}
			try{
				// Acumular
				int rele = juicios.get(necesidad).get(docId);
				relevante = relevante + rele;
				total++;
				if(rele == 1){	// El documento es relevante.
					precisionProm = precisionProm + relevante/total;
					precisionRecall.add(new Par(relevante/total,
							relevante/totalRelevante));
				}
				if(total==10){	// Se comprueba para el precision10.
					precision10 = relevante/total;
				}
			} catch(Exception e){}
			docRecuperados.nextLine();
		}
		// Calcular medidas para el último.
		calcularMedidas(relevante, total, totalRelevante, precisionRecall, 
				precision10, precisionProm, Integer.toString(necesidad));
	}
	
	/*
	 * Método que calcula el número total de documentos relevantes de una
	 * necesidad de información.
	 */
	private static int obtenerRelevantes(Map<Integer, Integer> relDoc){
		
		int total = 0;
		for(int i=1; i<=relDoc.size(); i++){
			total = total + relDoc.get(i);
		}
		return total;
	}
	
	/*
	 * Método que realiza el cálculo de todas las medidas y las escribe.
	 */
	private static void calcularMedidas(double relevante, double total, 
			double totalRelevante, ArrayList<Par> precisionRecall,
			double precision10, double precisionProm, String necesidad){
		
		resultados.println("INFORMATION_NEED\t" + necesidad);
		double precision = relevante/total;	// Se calcula la precisión.
		medidas.setPrecision(medidas.getPrecision()+precision);
		double recall = relevante/totalRelevante;	// Se calcula el recall.
		medidas.setRecall(medidas.getRecall()+recall);
		double f1 = 2*precision*recall/(precision+recall);	// Se calculal f1.
		medidas.setF1(medidas.getF1()+f1);
		precisionProm = precisionProm / relevante;	// Se calcula precisión promedio.
		medidas.setPrecisionProm(medidas.getPrecisionProm()+precisionProm);
		resultados.printf("precision\t%1.3f%n", precision);
		resultados.printf("recall\t%1.3f%n", recall);
		resultados.printf("F1\t%1.3f%n", f1);
		precision10 = relevante/total;
		medidas.setPrecision10(medidas.getPrecision10()+precision10);
		resultados.printf("prec@10\t%1.3f%n", precision10);
		resultados.printf("average_precision\t%1.3f%n", precisionProm);
		precRecall(precisionRecall);
		precRecallInterp(precisionRecall);
		medidas.actualizarTotal();
	}
	
	/*
	 * Método que escribe los datos de precisión y recall para la curva.
	 */
	private static void precRecall(ArrayList<Par> datos){
		
		resultados.println("recall_precision");
		for(int i=0; i<datos.size(); i++){
			resultados.printf("%1.3f\t%1.3f%n",datos.get(i).getRecall(),
					datos.get(i).getPrecision());
		}
	}
	
	/*
	 * Método que escribe los puntos de precisión y recall interpolados.
	 */
	private static void precRecallInterp(ArrayList<Par> datos){
		
		ArrayList<Double> interpoladas = new ArrayList<Double> (11);
		resultados.println("interpolated_recall_precision");
		int indice = 0;
		for(int i=0; i<11 && indice<datos.size(); i++){
			double punto = (double)(i);
			punto = punto/10;
			double precision = datos.get(indice).getPrecision();
			double recall = datos.get(indice).getRecall();
			for(int j=indice; j<datos.size(); j++){
				if(datos.get(j).getPrecision() > precision){
					precision = datos.get(j).getPrecision();
				}
			}
			interpoladas.add(precision);
			resultados.printf("%1.3f\t%1.3f%n", punto, precision);
			if(punto+0.1>recall){indice++;}
		}
		resultados.println();
		medidas.setPrecRecInter(interpoladas);
	}
	
	/*
	 * Método que escribe las medidas globales calculadas.
	 */
	private static void medidasGlobales(){
		
		resultados.println("TOTAL");
		resultados.printf("precision\t%1.3f%n", medidas.getPrecision()/medidas.getTotalConsultas());
		resultados.printf("recall\t%1.3f%n", medidas.getRecall()/medidas.getTotalConsultas());
		resultados.printf("F1\t%1.3f%n", medidas.getF1()/medidas.getTotalConsultas());
		resultados.printf("prec@10\t%1.3f%n", medidas.getPrecision10()/medidas.getTotalConsultas());
		resultados.printf("MAP\t%1.3f%n", medidas.getPrecisionProm()/medidas.getTotalConsultas());
		ArrayList<Double> interpolada = medidas.getPrecRecInter();
		resultados.println("interpolated_recall_precision");
		double indice;
		int numInterpoladas = medidas.getNumInterpoladas();
		for(int i=0; i<numInterpoladas; i++){
			indice = (double)(i);
			resultados.printf("%1.3f\t%1.3f%n", indice/10, interpolada.get(i)/medidas.getTotalConsultas());
		}
	}
}
