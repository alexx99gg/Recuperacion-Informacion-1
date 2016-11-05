package Trabajo;

import java.util.Scanner;

public class Fechas {

	/*
     * Método que reconoce el patrón de un intervalo.
     */
    public static void intervalo(String texto){
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	String palabra = analizar.next();
    	try{
    		int num1 = Integer.parseInt(palabra);
    		palabra = analizar.next();
    		if(palabra.equals("y") || palabra.equals("hasta") 
    				|| palabra.equals("a")){
   				try{
    				palabra = analizar.next();
   					int num2 = Integer.parseInt(palabra);
   					System.out.println(num1+"-"+num2);
   				} catch(Exception e1){}
   			}
   		} catch(Exception e){}
    	analizar.close();
   	}
    
    /*
     * Método que reconece el patrón de años posteriores.
     */
    public static void posteriores(String texto){
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	try{
	    	String palabraPrimera = analizar.next();
	    	if(palabraPrimera.equals("a")){
	    		try{
	    			int num = Integer.parseInt(analizar.next());
	    			System.out.println("probando");
	    			// Tratar desde num hasta infinito.
	    		} catch(Exception e){}
    		} else {
    			String palabraSegunda = analizar.next();
    			if(palabraPrimera.equals("partir") && palabraSegunda.equals("del")
	    			&& analizar.next().equals("año")){
    				try{
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					if(siguiente.equals("(incluido)")){
    						// Está el año incluido.
    						System.out.println("hola2");
    					} else if(siguiente.equals("incluido")){
    						// Está el año incluido.
    						System.out.println("hola3");
    					} else{
    						// Año excluido en todos los casos.
    						System.out.println("hola23");
    					}
    				} catch(Exception e1){}
    			} else if(palabraPrimera.equals("partir") && palabraSegunda.equals("de")){
    				try{
    					System.out.println("jaja");
    					int num = Integer.parseInt(analizar.next());
    					String siguiente = analizar.next();
    					if(siguiente.equals("(incluido)")){
    						// Está el año incluido.
    						System.out.println("hola4");
    					} else if(siguiente.equals("incluido")){
    						// Está el año incluido.
    						System.out.println("hola5");
    					} else{
    						// Año excluido en todos los casos.
    						System.out.println("hola403");
    					}
    				} catch(Exception e1){}
    			}
    		}
	    	analizar.close();
    	} catch(Exception e){}
    }
    
    /*
     * Método que reconoce el patrón de años anteriores.
     */
    public static void anteriores(String texto){
    	Scanner analizar = new Scanner(texto);
    	analizar.useDelimiter(" |\\.|\\n|,");
    	String palabra = analizar.next();
    	if(palabra.equals("a")){
    		try{
    			int num = Integer.parseInt(analizar.next());
        		System.out.println(num);
        		// Tratar desde num hacia abajo
    		} catch(Exception e){}
    	} else{
    		try{
    			if(analizar.next().equals("años")){
    				int num = Integer.parseInt(palabra);
            		System.out.println(num);
            		// Tratar desde actualidad hasta actualidad-num.
    			}
    		} catch(Exception e){}
    	}
    	analizar.close();
    }
}
