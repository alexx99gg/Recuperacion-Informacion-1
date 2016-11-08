package practica3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
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
		int totalRelevante = 0;	// Número total de documentos relevantes.
		int necesidad, docId, necAnterior = 0;	// Variables para leer del fichero.
		// Variables para la evaluación.
		double precision, recall, f1, precision10 = 0.0;
		double precisionProm = 0.0;
		boolean primero = true;	// Booleano para saber si es el primero.
		boolean prec10 = true;
		while(docRecuperados.hasNextLine()){	// Se recorre todo el fichero.
			necesidad = docRecuperados.nextInt();	// Se leen las variables del fichero.
			docId = docRecuperados.nextInt();
			if(primero){necAnterior = necesidad; primero = false;}
			if(necAnterior != necesidad){	// Comprueba si se ha terminado la primera necesidad.
				// Calcular medidas.
				precision = relevante/total;	// Se calcula la precisión.
				Map <Integer, Integer> recallDoc = juicios.get(necAnterior);
				totalRelevante = obtenerRelevantes(recallDoc);
				recall = relevante/totalRelevante;	// Se calcula el recall.
				f1 = 2*precision*recall/(precision+recall);	// Se calculal f1.
				precisionProm = precisionProm / relevante;	// Se calcula precisión promedio.
				System.out.println("Precision: " + precision);
				System.out.println("Recall: " + recall);
				System.out.println("F1: " + f1);
				if(!prec10){
					System.out.println("Precision10: " + precision10);
				}
				System.out.println("Precisión promedio: " + precisionProm);
				necAnterior = necesidad;
				relevante = 0; total = 0;
				prec10 = true;
				precisionProm = 0.0;
			}
			try{
				// Acumular
				int rele = juicios.get(necesidad).get(docId);
				relevante = relevante + rele;
				total++;
				if(rele == 1){	// El documento es relevante.
					precisionProm = precisionProm + relevante/total;
				}
				if(prec10 && total==10){	// Se comprueba para el precision10.
					prec10 = false;
					precision10 = relevante/total;
				}
			} catch(Exception e){}
			docRecuperados.nextLine();
		}
		// Calcular medidas para el último.
		precision = relevante/total;	// Se calcula la precisión.
		Map <Integer, Integer> recallDoc = juicios.get(necAnterior);
		totalRelevante = obtenerRelevantes(recallDoc);
		recall = relevante/totalRelevante;	// Se calcula el recall.
		f1 = 2*precision*recall/(precision+recall);	// Se calculal f1.
		precisionProm = precisionProm / relevante;	// Se calcula precisión promedio.

		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F1: " + f1);
		if(!prec10 || total==10){
			precision10 = relevante/total;
			System.out.println("Precision10: " + precision10);
		}
		System.out.println("Precisión promedio: " + precisionProm);
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
}
