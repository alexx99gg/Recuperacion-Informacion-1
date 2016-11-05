package Trabajo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Fechas {

	/*
     * Método que reconoce el patrón de un intervalo.
     */
    public static int[] intervalo(String texto){
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	String palabra = analizar.next();
    	int[] fecha = new int[2];
    	try{
    		int num1 = Integer.parseInt(palabra);
    		palabra = analizar.next();
    		if(palabra.equals("y") || palabra.equals("hasta") 
    				|| palabra.equals("a")){
   				try{
    				palabra = analizar.next();
   					int num2 = Integer.parseInt(palabra);
   					fecha[0] = num1;
   					fecha[1] = num2;
   					System.out.println(num1+"-"+num2);
   				} catch(Exception e1){}
   			} else {
					fecha[0] = num1;
					fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
   			}
   		} catch(Exception e){}
    	analizar.close();
		return fecha;
   	}
    
    /*
     * Método que reconece el patrón de años posteriores.
     */
    public static int[] posteriores(String texto){
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	int[] fecha = new int[2];
    	try{
	    	String palabraPrimera = analizar.next();
	    	if(palabraPrimera.equals("a")){
	    		try{
	    			int num = Integer.parseInt(analizar.next());
	    			fecha[0] = num;
					fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
	    		} catch(Exception e){}
    		} else {
    			String palabraSegunda = analizar.next();
    			if(palabraPrimera.equals("partir") && palabraSegunda.equals("del")
	    			&& analizar.next().equals("año")){
    				try{
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					if(siguiente.equals("(incluido)")){
    						fecha[0] = num;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    						System.out.println("hola2");
    					} else if(siguiente.equals("incluido")){
    						fecha[0] = num;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    						System.out.println("hola3");
    					} else{
    						fecha[0] = num+1;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    						System.out.println("hola23");
    					}
    				} catch(Exception e1){}
    			} else if(palabraPrimera.equals("partir") && palabraSegunda.equals("de")){
    				try{
    					System.out.println("jaja");
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					if(siguiente.equals("(incluido)")){;
							fecha[0] = num;
							fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    						System.out.println("hola4");
    					} else if(siguiente.equals("incluido")){;
							fecha[0] = num;
							fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
	    					System.out.println("hola5");
    					} else{
    						fecha[0] = num+1;
    						fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
    						System.out.println("hola403");
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
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	String palabra = analizar.next();
    	int[] fecha = new int[2];
    	if(palabra.equals("a")){
    		try{
    			int num = Integer.parseInt(analizar.next());
				fecha[0] = 0;
				fecha[1] = num;
        		System.out.println(num);
        		// Tratar desde num hacia abajo
    		} catch(Exception e){}
    	} else{
    		try{
    			if(analizar.next().equals("años")){
    				int num = Integer.parseInt(palabra);
            		System.out.println(num);
					fecha[1] = (new GregorianCalendar()).get(Calendar.YEAR);
					fecha[0] = fecha[1]-num;
    			}
    		} catch(Exception e){}
    	}
    	analizar.close();
    	return fecha;
    }
}
