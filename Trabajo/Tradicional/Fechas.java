package Trabajo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

/*
 * Clase que contiene métodos para detectar patrones de fechas
 * en una cierta necesidad de información.
 */
public class Fechas {

	/*
     * Método que reconoce el patrón de un intervalo.
     */
    public static int[] intervalo(String texto){
    	// Crea el scanner.
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");	// Fija delimitadores.
    	String palabra = analizar.next();	// Obtiene la primera palabra.
    	int[] fecha = null;	// Crea el array para el intervalo.
    	int num1 = 0;
    	try{
	    	try{	
	    		// Obtiene primer año.
	    		num1 = Integer.parseInt(palabra);
	    		palabra = analizar.next();
	    		// Comprueba si se cumple el patrón.
	    		if(palabra.equals("y") || palabra.equals("hasta") 
	    				|| palabra.equals("a")){	// [entre/desde/de X hasta/a/y Z]
	   				try{
	    				palabra = analizar.next();
	   					int num2 = Integer.parseInt(palabra);	// Obtiene segundo año.
	   					fecha = new int[2];
	   					fecha[0] = num1;
	   					fecha[1] = num2;	// Guarda los años en el array.
	   				} catch(Exception e1){}
	   			} else{	// [de X]
	   				fecha = new int[2];
	   				fecha[0] = num1; fecha[1] = num1;
	   			}
	   		} catch(NumberFormatException e1){}
    	} catch(Exception e){	// Si no hay más tokens es que coincide con [de X]
			fecha = new int[2];
			fecha[0] = num1; fecha[1] = num1;
    	}
    	analizar.close();
		return fecha;
   	}
    
    /*
     * Método que reconece el patrón de años posteriores.
     */
    public static int[] posteriores(String texto){
    	// Crea el scanner.
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");	// Fija los delimitadores.
    	int[] fecha = null;
    	try{
    		// Se lee la primera palabra.
	    	String palabraPrimera = analizar.next();
	    	if(palabraPrimera.equals("a")){
	    		try{		// [posteriores a X]
	    			// Obtiene el año.
	    			int num = Integer.parseInt(analizar.next());
	    			fecha = new int[2];
	    			fecha[0] = num;
					fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
	    		} catch(Exception e){}
    		} else {	// [a partir del año X includio/excluido]
    			String palabraSegunda = analizar.next();
    			if(palabraPrimera.equals("partir") && palabraSegunda.equals("del")
	    			&& analizar.next().equals("año")){
    				try{	// Se lee el año.
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					fecha = new int[2];
    					// Se comprueba si es incluido o excluido.
    					if(siguiente.equals("(incluido)")){
    						fecha[0] = num;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					} else if(siguiente.equals("incluido")){
    						fecha[0] = num;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					} else{
    						fecha[0] = num+1;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					}
    				} catch(Exception e1){}
    			} else if(palabraPrimera.equals("partir") && palabraSegunda.equals("de")){
    				try{	// [a partir de X incluido/excluido]
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					fecha = new int[2];
    					// Se comprueba si es incluido o excluido.
    					if(siguiente.equals("(incluido)")){;
							fecha[0] = num;
							fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					} else if(siguiente.equals("incluido")){;
							fecha[0] = num;
							fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					} else{
    						fecha[0] = num+1;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    					}
    				} catch(Exception e1){}
    			}
    		}
	    	analizar.close();
    	} catch(Exception e){}
    	return fecha;
    }
    
    /*
     * Método que reconoce el patrón de años anteriores.
     */
    public static int[] anteriores(String texto){
    	// Se crea el scanner.
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");	// Fija los delimitadores.
    	String palabra = analizar.next();
    	int[] fecha = null;	// Crea el array para leer las fechas.
    	if(palabra.equals("a")){	// [anterior/anteriores a X]
    		try{
    			int num = Integer.parseInt(analizar.next());
    			fecha = new int[2];
				fecha[0] = 0;
				fecha[1] = num;
    		} catch(Exception e){}
    	} else{	// [últimos X años]
    		try{
    			if(analizar.next().equals("años")){
    				int num = Integer.parseInt(palabra);
            		fecha = new int[2];
					fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
					fecha[0] = fecha[1]-num;
    			}
    		} catch(Exception e){}
    	}
    	analizar.close();
    	return fecha;
    }
    
    /*
     * Método que comprueba si se cumple el patrón de una fecha exacta.
     */
    public static int[] exacta(String texto){
    	// Se crea el scanner.
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");	// Fija los delimitadores.
    	String palabra = analizar.next();
    	int[] fecha = null;	// Crea el array para leer las fechas.
    	if(palabra.equals("el")){	// [en el año X]
    		if(analizar.next().equals("año")){
    			try{
    				int num = Integer.parseInt(analizar.next());
    				fecha = new int[2];
    				fecha[0] = num; fecha[1] = num;
    			} catch(Exception e){}
    		}
    	} else{	// [en X]
    		try{
    			int num = Integer.parseInt(palabra);
    			fecha = new int [2];
    			fecha[0] = num; fecha[1] = num;
    		} catch(Exception e){}
    	}
    	analizar.close();
    	return fecha;
    }
}
