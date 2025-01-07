package sintaxis;

import java.util.ArrayList;
import java.util.List;
import analizador_Lexico.Token;
import analizador_Lexico.AnalizadorLexico;

public class Parser {
	
    private List<Token> tokens;
    private int indiceActual = 0; 
    private List<String> errores = new ArrayList<>();
    
    public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public int getIndiceActual() {
		return indiceActual;
	}

	public void setIndiceActual(int indiceActual) {
		this.indiceActual = indiceActual;
	}

	public Parser() {
		String ruta= "C:\\Users\\laptop\\Documents\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\Compilador2\\src\\Analizador_Lexico\\Prueba.txt";
		AnalizadorLexico lexico =new AnalizadorLexico();
        this.tokens=lexico.lex(ruta);        
        this.errores.addAll(lexico.getErrores());
    }

    public void analizar() {
    	
    	 if (!this.errores.isEmpty()) {
    	        System.out.println("Errores encontrados durante el análisis léxico:");
    	        
    	        for (String error : this.errores) {
    	            System.err.println(error);
    	            throw new RuntimeException("Se encontraron errores léxicos, deteniendo ejecución.");
    	        }
    	    } else {
    	        System.out.println("No se encontraron errores en el análisis léxico.");
    	    }
    	
    	 
        try {
            programa(); 
            System.out.println("El análisis sintáctico fue exitoso.");
        } catch (RuntimeException e) {
            System.err.println("Error durante el análisis sintáctico: " + e.getMessage());
        }
    }

   
    private Token obtenerTokenActual() {
        return tokens.get(indiceActual);
    }
	
	
    private void programa() {
        if (match(Token.Tipos.PALABRA_RESERVADA_INICIO)) {
            while (!match(Token.Tipos.PALABRA_RESERVADA_FIN) && !finDeTokens()) {
                sentencia();
            }
            if (!match(Token.Tipos.PALABRA_RESERVADA_FIN)) {
            	reportarError("Se esperaba 'fin' al final del programa.");
                errores.add("Se esperaba 'fin' al final del programa.");
            }
        } else {
        	reportarError("Se esperaba 'inicio' al inicio del programa.");
            errores.add("Se esperaba 'inicio' al inicio del programa.");
        }
    }

    private void sentencia() {
        if (esDeclaracion()) {
            declaracion();
        } else if (esAsignacion()) {
            asignacion();
        } else if (esCondicional()) {
            condicional();
        } else {
        	reportarError("Sentencia desconocida en línea " + obtenerTokenActual().getLinea());
            errores.add("Sentencia desconocida en línea " + obtenerTokenActual().getLinea());
            avanzar();
        }
    }

	private boolean match(Token.Tipos tipoEsperado) {
        if (finDeTokens()) return false;
        if (tokens.get(indiceActual).getTipo() == tipoEsperado) {
            indiceActual++;
            return true;
        }
        return false;
    }

    private boolean finDeTokens() {
        return indiceActual >= tokens.size();
    }
    
    private void declaracion() {
        if (match(Token.Tipos.DATO_ENTERO) || match(Token.Tipos.DATO_DECIMAL) ||
            match(Token.Tipos.DATO_CADENA) || match(Token.Tipos.DATO_BOOLEANO)) {
            if (!match(Token.Tipos.IDENTIFICADOR)) {
                reportarError("Se esperaba un identificador después del tipo.");
            } else if (match(Token.Tipos.SIMBOLO_ASIGNACION)) { 
                expresion();
            }
            if (!match(Token.Tipos.DELIMITADOR)) {
                reportarError("Se esperaba ';' al final de la declaración.");
            }
        }
    }
    
    private void expresion() {
        termino();
        while (match(Token.Tipos.OPERADOR_ARITMETICO_SUMA) || 
               match(Token.Tipos.OPERADOR_ARITMETICO_RESTA)) {
            termino();
        }
    }

    private void termino() {
        factor();
        while (match(Token.Tipos.OPERADOR_ARITMETICO_MULTIPLICACION) || 
               match(Token.Tipos.OPERADOR_ARITMÉTICO_DIVISION)) {
            factor();
        }
    }

    private void factor() {
        if (match(Token.Tipos.ENTERO) || match(Token.Tipos.DECIMAL) ||
            match(Token.Tipos.CADENA) || match(Token.Tipos.IDENTIFICADOR)) {
            return; 
        } else {
            reportarError("Se esperaba un valor o identificador en línea " + obtenerTokenActual().getLinea());
            avanzar(); 
        }
    }
    private boolean esDeclaracion() {
        return matchSinAvanzar(Token.Tipos.DATO_ENTERO) ||
               matchSinAvanzar(Token.Tipos.DATO_DECIMAL) ||
               matchSinAvanzar(Token.Tipos.DATO_CADENA) ||
               matchSinAvanzar(Token.Tipos.DATO_BOOLEANO);
    }

    private boolean esAsignacion() {
        return matchSinAvanzar(Token.Tipos.IDENTIFICADOR);
    }

    private boolean esCondicional() {
        return matchSinAvanzar(Token.Tipos.PALABRA_RESERVADA_SI);
    }
    private boolean matchSinAvanzar(Token.Tipos tipoEsperado) {
        if (finDeTokens()) return false;
        return tokens.get(indiceActual).getTipo() == tipoEsperado;
    }
    
    private void asignacion() {
        if (match(Token.Tipos.IDENTIFICADOR)) {
            if (!match(Token.Tipos.SIMBOLO_ASIGNACION)) {
                reportarError("Se esperaba ':=' después del identificador.");
            } else {
                expresion();
                if (!match(Token.Tipos.DELIMITADOR)) {
                    reportarError("Se esperaba ';' al final de la asignación.");
                }
            }
        } else {
            reportarError("Se esperaba un identificador al inicio de la asignación.");
        }
    }
    
    private void condicional() {
        if (match(Token.Tipos.PALABRA_RESERVADA_SI)) {
            if (!match(Token.Tipos.PARENTESIS_ABIERTO)) {
                errores.add("Se esperaba '(' después de 'si'.");
            } else {
                expresion();
                if (!match(Token.Tipos.PARENTESIS_CERRADO)) {
                    errores.add("Se esperaba ')' después de la condición.");
                } else {
                    sentencia();
                }
            }
        } else {
            errores.add("Condicional mal formado.");
        }
    }
    
    private void avanzar() {
        if (!finDeTokens()) {
            indiceActual++; 
        } else {
            
            System.out.println("Se alcanzó el final de los tokens.");
        }
    }
    
    private void reportarError(String mensaje) {
        if(!finDeTokens()){
            errores.add(mensaje + " Token actual: " + obtenerTokenActual().getValor() + " Linea: " + obtenerTokenActual().getLinea() + " Columna: " + obtenerTokenActual().getColumna());
        }else{
            errores.add(mensaje + " Fin de archivo");
        }
        throw new RuntimeException("Error sintáctico");
    }
}
