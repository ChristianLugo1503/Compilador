package sintaxis;

import java.util.ArrayList;
import java.util.List;
import analizador_Lexico.Token;
import analizador_Lexico.AnalizadorLexico;

public class Parser {
	
    private List<Token> tokens;
    private int indiceActual = 0; 
    private List<String> errores = new ArrayList<>();
    
    private ASTNodo raiz;
    
    public ASTNodo getRaiz() {
    	return raiz;
    }
    
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
            raiz = programa(); 
            System.out.println("El análisis sintáctico fue exitoso.");
        } catch (RuntimeException e) {
            System.err.println("Error durante el análisis sintáctico: " + e.getMessage());
        }
    }
   
	
    private ASTNodo programa() {
    	  ASTNodo nodoPrograma = new ASTNodo("Programa");
        if (match(Token.Tipos.PALABRA_RESERVADA_INICIO)) {
            while (!match(Token.Tipos.PALABRA_RESERVADA_FIN) && !finDeTokens()) {
                nodoPrograma.agregarHijo(sentencia());
            }
            if (!match(Token.Tipos.PALABRA_RESERVADA_FIN)) {
            	reportarError("Se esperaba 'fin' al final del programa.");
                errores.add("Se esperaba 'fin' al final del programa.");
            }
        } else {
        	reportarError("Se esperaba 'inicio' al inicio del programa.");
            errores.add("Se esperaba 'inicio' al inicio del programa.");
        }
		return nodoPrograma;
    }

    private ASTNodo sentencia() {
        if (esDeclaracion()) {
            return declaracion();
        } else if (esAsignacion()) {
            return asignacion();
        } else if (esCondicional()) {
            return condicional();
        } else {
            reportarError("Sentencia desconocida en línea " + obtenerTokenActual().getLinea());
            avanzar();
            return null;
        }
    }

    private ASTNodo condicional() {
        Token tipoToken = obtenerTokenActual();
        
        if (match(Token.Tipos.PALABRA_RESERVADA_SI)) {
            ASTNodo nodoCondicional = new ASTNodo("Condicional", tipoToken.getValor());
          
            if (match(Token.Tipos.PARENTESIS_ABIERTO)) {
  
                ASTNodo nodoCondicion = expresion();
                nodoCondicional.agregarHijo(nodoCondicion);

                if (!match(Token.Tipos.PARENTESIS_CERRADO)) {
                    reportarError("Se esperaba ')' después de la condición.");
                }
            } else {
                reportarError("Se esperaba '(' después de 'si'.");
            }
            
            if (match(Token.Tipos.PALABRA_RESERVADA_ENTONCES)) {
                while (!(match(Token.Tipos.PALABRA_RESERVADA_FIN) && 
                         match(Token.Tipos.PALABRA_RESERVADA_SI)) && 
                         !finDeTokens()) {
                    ASTNodo sentencia = sentencia(); 
                    nodoCondicional.agregarHijo(sentencia);
                }

                if (!(match(Token.Tipos.PALABRA_RESERVADA_FIN) && 
                      match(Token.Tipos.PALABRA_RESERVADA_SI))) {
                    reportarError("Se esperaba 'fin si' para cerrar el condicional.");
                }
            } else {
                reportarError("Se esperaba 'entonces' después de la condición.");
            }
            
            return nodoCondicional; // Devolver el nodo del condicional completo
        } else {
            reportarError("Se esperaba 'si' al inicio del condicional.");
            return null;
        }
    }

    
    private ASTNodo declaracion() {
    	 Token tipoToken = obtenerTokenActual();
         if (match(Token.Tipos.DATO_ENTERO) || match(Token.Tipos.DATO_DECIMAL) ||
             match(Token.Tipos.DATO_CADENA) || match(Token.Tipos.DATO_BOOLEANO)) {
             ASTNodo nodoDeclaracion = new ASTNodo("Declaracion", tipoToken.getValor());
             if (match(Token.Tipos.IDENTIFICADOR)) {
                 Token idToken = obtenerTokenActual(-1); 
                 ASTNodo nodoIdentificador = new ASTNodo("Identificador", idToken.getValor());
                 nodoDeclaracion.agregarHijo(nodoIdentificador);
                 if (match(Token.Tipos.SIMBOLO_ASIGNACION)) {
                     nodoDeclaracion.agregarHijo(expresion());
                 }
                 if (!match(Token.Tipos.DELIMITADOR)) {
                     reportarError("Se esperaba ';' al final de la declaración.");
                 }
             } else {
                 reportarError("Se esperaba un identificador después del tipo.");
             }
             return nodoDeclaracion;
         }
         return null;
    }
    
    private ASTNodo asignacion() {
        if (match(Token.Tipos.IDENTIFICADOR)) {
            Token idToken = obtenerTokenActual(-1);
            ASTNodo nodoAsignacion = new ASTNodo("Asignacion");
            nodoAsignacion.agregarHijo(new ASTNodo("Identificador", idToken.getValor()));
            if (!match(Token.Tipos.SIMBOLO_ASIGNACION)) {
                reportarError("Se esperaba ':=' después del identificador.");
            } else {
                nodoAsignacion.agregarHijo(expresion());
                if (!match(Token.Tipos.DELIMITADOR)) {
                    reportarError("Se esperaba ';' al final de la asignación.");
                }
            }
            return nodoAsignacion;
        } else {
            reportarError("Se esperaba un identificador al inicio de la asignación.");
        }
        return null;
    }
    
    private ASTNodo expresion() {
    	  ASTNodo nodoExpresion = termino();
          while (match(Token.Tipos.OPERADOR_ARITMETICO_SUMA) || match(Token.Tipos.OPERADOR_ARITMETICO_RESTA)) {
              Token operador = obtenerTokenActual(-1);
              ASTNodo nodoOperador = new ASTNodo("Operador", operador.getValor());
              nodoOperador.agregarHijo(nodoExpresion);
              nodoOperador.agregarHijo(termino());
              nodoExpresion = nodoOperador;
          }
          return nodoExpresion;
    }

    private ASTNodo termino() {
        ASTNodo nodoTermino = factor();
        while (match(Token.Tipos.OPERADOR_ARITMETICO_MULTIPLICACION) ||
        		match(Token.Tipos.OPERADOR_ARITMETICO_DIVISION)){
        	Token operador = obtenerTokenActual(-1);
        	ASTNodo nodoOperador = new ASTNodo("Operador", operador.getValor()); nodoOperador.agregarHijo(nodoTermino);
        	nodoOperador.agregarHijo(factor()); nodoTermino = nodoOperador; 
        	} 
        return nodoTermino;
    }

    private ASTNodo factor() {
    	if (match(Token.Tipos.DATO_ENTERO) || match(Token.Tipos.DATO_DECIMAL)) {
            Token numero = obtenerTokenActual(-1);
            return new ASTNodo("Literal", numero.getValor());
        } else if (match(Token.Tipos.IDENTIFICADOR)) {
            Token identificador = obtenerTokenActual(-1);
            return new ASTNodo("Identificador", identificador.getValor());
        } else if (match(Token.Tipos.PARENTESIS_ABIERTO)) {
            ASTNodo nodoExpresion = expresion();
            if (!match(Token.Tipos.PARENTESIS_CERRADO)) {
                reportarError("Se esperaba ')' para cerrar la expresión.");
            }
            return nodoExpresion;
        } else {
            reportarError("Se esperaba un número, identificador, o una expresión entre paréntesis.");
            avanzar();
            return null;
        }
    }
    
    private boolean match(Token.Tipos tipoEsperado) {
        if (finDeTokens()) return false;
        if (obtenerTokenActual().getTipo() == tipoEsperado) {
            avanzar();
            return true;
        }
        return false;
    }

    private Token obtenerTokenActual() {
        return obtenerTokenActual(0);
    }

    private Token obtenerTokenActual(int offset) {
        int indice = indiceActual + offset;
        if (indice >= 0 && indice < tokens.size()) {
            return tokens.get(indice);
        }
        return null;
    }

    private void avanzar() {
        if (!finDeTokens()) {
            indiceActual++;
        }
    }

    private boolean finDeTokens() {
        return indiceActual >= tokens.size();
    }

    private void reportarError(String mensaje) {
        errores.add(mensaje);
        System.err.println(mensaje);
    }

    private boolean esDeclaracion() {
        return match(Token.Tipos.DATO_ENTERO) || match(Token.Tipos.DATO_DECIMAL) ||
               match(Token.Tipos.DATO_CADENA) || match(Token.Tipos.DATO_BOOLEANO);
    }

    private boolean esAsignacion() {
        return obtenerTokenActual() != null && obtenerTokenActual().getTipo() == Token.Tipos.IDENTIFICADOR &&
               tokens.get(indiceActual + 1).getTipo() == Token.Tipos.SIMBOLO_ASIGNACION;
    }

    private boolean esCondicional() {
        return obtenerTokenActual() != null && obtenerTokenActual().getTipo() == Token.Tipos.PALABRA_RESERVADA_SI;
    }
}
