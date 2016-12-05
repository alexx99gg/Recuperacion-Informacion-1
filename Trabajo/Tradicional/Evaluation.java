package Trabajo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase con métodos necesarios para realizar la evaluación de un cierto
 * sistema de información.
 */
public class Evaluation {

	private static Scanner docRecuperados;	// Fichero para leer resultados.
	private static Scanner relevancia;	// Fichero con juicios.
	private static PrintWriter resultados;	// Fichero de resultados.
	private static Map<String, Map<String, Integer>> juicios = 
			new HashMap<String, Map<String, Integer>> (35); 	// Map con los juicios de relevancia.
	private static Medidas medidas = new Medidas();	// Creamos el objeto para las medidas globales.
	private static ArrayList<String> ficherosJuicios = new ArrayList<String>();	// Identificadores de ficheros en juicios.
	private static ArrayList<String> necesidades = new ArrayList<String>();	// Identificadores de las necesidades.
	
	/*
	 * Método principal que lanza la evaluación del sistema de información
	 * y comprueba los parámetros con los que es llamado.
	 */
	public static void main (String [] args){
		
		args = new String[6];
		args[0] = "-qrels";
		args[1] = "zaguanRels.txt";
		args[2] = "-results";
		args[3] = "resultados.txt";
		args[4] = "-output";
		args[5] = "evaluacion.txt";
		
		comprobarArgumentos(args);	// Comprueba los argumentos.
		
		System.out.println("Comenzando proceso de evaluación...");
		
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
		
		int rele = 0;		// Datos a leer.
		String docId, necesidad, necAnterior = null;
		boolean primero = true;
		// Map para la necesidad y si es o no relevante.
		Map<String, Integer> docRelevancia = new HashMap<String, Integer>(40);
		while(relevancia.hasNextLine()){	// Se recorren todas las líneas.
			// Se leen los tres datos.
			necesidad = relevancia.next();	// Se lee la necesidad.
			docId = relevancia.next();		// Se lee el id del documento.
			ficherosJuicios.add(docId);		// Se añade a la lista el id del documento.
			rele = relevancia.nextInt();	// Se obtiene la relevancia.
			if(primero){	// Si es el primero se inicializan las variables.
				necAnterior = necesidad; 
				primero = false;
				necesidades.add(necesidad);	// Se añade al lista de necesidades.
			}
			if(!necAnterior.equals(necesidad)){	// Si se lee una necesidad nueva...
				// Se añade el map de documento y relevancia asociado a la necesidad.
				juicios.put(necAnterior, docRelevancia);	
				necAnterior = necesidad;	// Se inicializa la variable.
				necesidades.add(necesidad);	// Se añade la necesidad a la lista de necesidades.
				docRelevancia = new HashMap<String, Integer>(40);	// Se crea el nuevo map.
			}
			docRelevancia.put(docId, rele);	// Se añade al map el documento y su relevancia.
			relevancia.nextLine();	// Se pasa a la siguiente línea.
		}
		juicios.put(necAnterior, docRelevancia);	// Si se ha leido todo, se añade la última necesidad.
	}
	
	/*
	 * Método que realiza los distintos cálculos para las necesidades de información.
	 */
	private static void calcularMedidas(){
		
		double relevante = 0, total = 0;		// Variables para el cálculo.
		double totalRelevante = 0;	// Número total de documentos relevantes.
		String necesidad = null, necAnterior = null;	// Variables para leer del fichero.
		String docId = null;
		// Variables para la evaluación.
		double precision10 = 0.0;
		double precisionProm = 0.0;
		boolean primero = true;	// Booleano para saber si es el primero.
		ArrayList<Par> precisionRecall = null;	// Array para la curva precisión-recall.
		int indiceNecesidad = 0, indiceJuicio = 0;		// Índices para recorrer las listas.
		while(docRecuperados.hasNextLine()){	// Se recorre todo el fichero.
			// Se leen las variables del fichero.
			necesidad = docRecuperados.next();		// Se lee la necesidad.
			docId = docRecuperados.next();		// Se lee el id del documento.
			if(primero){		// Si es el primero, se inicializan las variables.
				System.out.println("Realizando evaluación de la necesidad " + necesidad);
				necAnterior = necesidad;
				primero = false;
				// Se obtiene el map de documentos y juicios de la necesidad.
				Map <String, Integer> recallDoc = juicios.get(necesidades.get(indiceNecesidad));
				// Se obtienen el total de documentos relevantes para la necesidad.
				totalRelevante = obtenerRelevantes(recallDoc,indiceJuicio);
				// Actualiza el índice de juicios para colocarlo en la siguiente necesidad.
				indiceJuicio += recallDoc.size();
				indiceNecesidad++;	// Actualiza el índice de la necesidad.
				// Crea el array para la curva precision-recall.
				precisionRecall = new ArrayList<Par>(recallDoc.size());
			}
			if(!necAnterior.equals(necesidad)){	// Comprueba si se ha terminado la primera necesidad.
				// Calcular medidas.
				calcularMedidas(relevante, total, totalRelevante, precisionRecall, 
						precision10, precisionProm, necAnterior);
				necAnterior = necesidad;	// Actualiza las variables.
				System.out.println("Realizando evaluación de la necesidad " + necesidad);
				relevante = 0; total = 0; precisionProm = 0.0; precision10 = 0.0;
				// Obtiene el map documento-relevancia de la siguiente necesidad.
				Map <String, Integer> recallDoc = juicios.get(necesidades.get(indiceNecesidad));
				indiceNecesidad++;		// Actualiza el índice de la necesidad.
				// Obtiene el número de documentos relevantes para la necesidad.
				totalRelevante = obtenerRelevantes(recallDoc,indiceJuicio);
				// Actualiza el índice de juicios para colocarlo en la siguiente necesidad.
				indiceJuicio += recallDoc.size();
				// Crea el array para la curva precision-recall.
				precisionRecall = new ArrayList<Par>(recallDoc.size());
			}
			// Acumula las medidas.
			int rele = juicios.get(necesidad).get(docId);	// Obtiene si es relevante el documento.
			relevante = relevante + rele;	// Actualiza el número de relevantes.
			total++;		// Actualiza el número de documentos leídos.
			if(rele == 1){	// Si el documento es relevante...
				// Se calcula la precisión promedio.
				precisionProm = precisionProm + relevante/total;
				// Se añade a la curva precisión-recall.
				precisionRecall.add(new Par(relevante/total,
						relevante/totalRelevante));
			}
			if(total==10){	// Se comprueba si van 10 leídos para el precision10.
				precision10 = relevante/total;
			}
			docRecuperados.nextLine();		// Se pasa a la siguiente línea.
		}
		// Calcular medidas para el último leído.
		calcularMedidas(relevante, total, totalRelevante, precisionRecall, 
				precision10, precisionProm, necesidad);
	}
	
	/*
	 * Método que calcula el número total de documentos relevantes de una
	 * necesidad de información.
	 */
	private static int obtenerRelevantes(Map<String, Integer> relDoc, int inicial){
		
		int total = 0;		// Variable para calcular el total.
		for(int i=inicial; i<=relDoc.size()+inicial-1; i++){
			// Se obtiene si el documento es relevante o no.
			total = total + relDoc.get(ficherosJuicios.get(i));
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
		precisionProm = precisionProm / relevante;	// Se calcula precisión promedio.
		medidas.setPrecisionProm(medidas.getPrecisionProm()+precisionProm);
		resultados.printf("precision\t%1.3f%n", precision);
		resultados.printf("recall\t%1.3f%n", recall);
		resultados.printf("F1\t%1.3f%n", f1);
		if(precision10==0){		// Si se han recuperado menos de 10 relevantes...
			resultados.printf("prec@10\t%1.3f%n", relevante/10);
		} else{
			resultados.printf("prec@10\t%1.3f%n", precision10);
			medidas.setPrecision10(medidas.getPrecision10()+precision10);
		}
		if(total==0){	// Si la consulta no devuelve ningún resultado...
			resultados.printf("average_precision\t0", precisionProm);
		} else{
			resultados.printf("average_precision\t%1.3f%n", precisionProm);
		}
		precRecall(precisionRecall);
		precRecallInterp(precisionRecall);
		medidas.actualizarTotal();
	}
	
	/*
	 * Método que escribe los datos de precisión y recall para la curva.
	 */
	private static void precRecall(ArrayList<Par> datos){
		
		// Indica el título de precision-recall.
		resultados.println("recall_precision");
		for(int i=0; i<datos.size(); i++){	// Recorre los datos guardados y los va escribiendo.
			resultados.printf("%1.3f\t%1.3f%n",datos.get(i).getRecall(),
					datos.get(i).getPrecision());
		}
	}
	
	/*
	 * Método que escribe los puntos de precisión y recall interpolados.
	 */
	private static void precRecallInterp(ArrayList<Par> datos){
		
		// Crea el array para los puntos interpolados.
		ArrayList<Double> interpoladas = new ArrayList<Double> (11);
		resultados.println("interpolated_recall_precision");	// Escribe el título.
		for(int i=0; i<11; i++){
			double punto = (double)(i);
			punto = punto/10;	// Obtenemos el punto en el rango [0-1]
			boolean terminado = false;
			int recallSuperior = -1;
			// Recorremos los datos para encontrar el primer recall mayor al punto.
			for(int j=0; !terminado && j<datos.size(); j++){
				if(datos.get(j).getRecall() > punto){ // Si es el mayor lo guardamos.
					recallSuperior = j;
					terminado = true;		// Booleano para salir del bucle.
				}
			}
			if(recallSuperior != -1){		// Si se ha encontrado alguna mayor...
				double maxPrecision = 0.0;	// Se calcula la mayor precisión a su izquierda...
				for(int j=recallSuperior; j<datos.size(); j++){
					if(datos.get(j).getPrecision() > maxPrecision){
						// Si es mayor se guarda.
						maxPrecision = datos.get(j).getPrecision();
					}
				}
				interpoladas.add(maxPrecision);	// Se añade a los datos interpolados.
				// Se escribe por pantalla el resultado.
				resultados.printf("%1.3f\t%1.3f%n", punto, maxPrecision);
			} else{	// Si no hay ningún recall mayor que el punto, la precisión es 0.
				interpoladas.add(0.0);
				resultados.printf("%1.3f\t%1.3f%n", punto, 0.0);
			}
		}
		resultados.println();	// Se escribe un salto de línea.
		medidas.setPrecRecInter(interpoladas); // Se añaden los datos a las medidas globales.
	}
	
	/*
	 * Método que escribe las medidas globales calculadas.
	 */
	private static void medidasGlobales(){
		
		System.out.println("Calculando medidas globales...");
		resultados.println("TOTAL");	// Indicamos que son las medidas totales.
		// Escribimos la precisión media de las consultas.
		resultados.printf("precision\t%1.3f%n", medidas.getPrecision()/medidas.getTotalConsultas());
		// Escribimos el recall medio de las consultas.
		resultados.printf("recall\t%1.3f%n", medidas.getRecall()/medidas.getTotalConsultas());
		// Calculamos y escribimos el f1-score medio de las consultas.
		double f1 = (2*(medidas.getPrecision()/medidas.getTotalConsultas())*
				(medidas.getRecall()/medidas.getTotalConsultas()))/
				(medidas.getPrecision()/medidas.getTotalConsultas()+medidas.getRecall()/medidas.getTotalConsultas());
		resultados.printf("F1\t%1.3f%n", f1);
		// Escribimos el prec10 medio de las consultas.
		resultados.printf("prec@10\t%1.3f%n", medidas.getPrecision10()/medidas.getTotalConsultas());
		// Escribimos el MAP medio de las consultas.
		resultados.printf("MAP\t%1.3f%n", medidas.getPrecisionProm()/medidas.getTotalConsultas());
		// Obtenemos el valor de la curva precisión-recall interpolada.
		ArrayList<Double> interpolada = medidas.getPrecRecInter();
		resultados.println("interpolated_recall_precision");
		double indice;
		for(int i=0; i<11; i++){	// Recorremos la curva y escribimos los valores.
			indice = (double)(i);
			resultados.printf("%1.3f\t%1.3f%n", indice/10, interpolada.get(i)/medidas.getTotalConsultas());
		}
		System.out.println("Evaluación finalizada.");
	}
}
