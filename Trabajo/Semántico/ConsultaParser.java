package trabajo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Clase con los métodos necesarios para obtener las
 * consultas en SPARQL de un fichero.
 */
public class ConsultaParser {

	/*
	 * Método que lee el fichero creando una lista con las distintas
	 * consultas a realizar en SPARQL.
	 */
	public static ArrayList<Consulta> obtenerConsulta(File fichero){
		
		// Lista con las consultas.
		ArrayList<Consulta> consultas = new ArrayList<Consulta>();
		
		try{
			// Objetos para leer del fichero.
			FileReader fr = new FileReader(fichero);
			BufferedReader br = new BufferedReader(fr);
			
			String linea;
			while((linea = br.readLine()) != null){	// Se recorren las líneas.
				// Se lee identificador y texto.
				String identificador = linea.substring(0,linea.indexOf(" "));
				String texto = linea.substring(linea.indexOf(" ")+1);
				consultas.add(new Consulta(identificador, texto));
			}
			
			br.close();	// Se cierra el fichero.
			
		} catch(IOException e){
			System.err.println("Error al leer del fichero " + fichero.getPath());
		}
		
		return consultas;
	}
}
