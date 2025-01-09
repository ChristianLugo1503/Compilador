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
    	        }
    	        throw new RuntimeException("Se encontraron errores léxicos, deteniendo ejecución.");
    	    } else {
    	        System.out.println("No se encontraron errores en el análisis léxico.");
    	    }
    	
    	 
        try {
            raiz = programa(); 
            System.out.println("El análisis sintáctico ha terminado");
            
            if(errores.isEmpty()) {
            	System.out.println("En un exito!");
            }
            else {
            System.out.println("En fracaso!");
            System.out.println("Errores:");
            for(String error: errores){
            	 System.err.println(error);
            }
            }
        } catch (RuntimeException e) {
            System.err.println("Error durante el análisis sintáctico: " + e.getMessage());
        }
    }
   
	
    private ASTNodo programa() {
    	  ASTNodo nodoPrograma = new ASTNodo("Programa");
        if (indiceActual==0&&match(Token.Tipos.PALABRA_RESERVADA_INICIO)) {
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
        if (obtenerTokenActual() != null 
        		&& (obtenerTokenActual().getTipo() == Token.Tipos.PALABRA_RESERVADA_FIN 
        		&& (obtenerTokenActual(1).getTipo() == Token.Tipos.PALABRA_RESERVADA_SI 
        		|| obtenerTokenActual(1).getTipo() == Token.Tipos.PALABRA_RESERVADA_PARA 
        		|| obtenerTokenActual(1).getTipo() == Token.Tipos.PALABRA_RESERVADA_MIENTRAS))) {
            return null; 
        } else if (esDeclaracion()) {
            return declaracion();
        } else if (esAsignacion()) {
            return asignacion();
        } else if (esCondicional()) {
            return condicional();
        } else if (esPara()) {
            return para();
        } else if (esMientras()) {
            return mientras();
        } else if (esLlamadaFuncion()) {
            return llamadaFuncion();
        } else if (!finDeTokens()){
        	Token tokenActual = obtenerTokenActual();
        	String mensajeError = "Sentencia desconocida en línea ";
            if (tokenActual != null) { 
                mensajeError += tokenActual.getLinea() + ", Tipo: " + tokenActual.getTipo() + ", Valor: " + tokenActual.getValor();
            } else {
                mensajeError += "(final de tokens)"; 
            }
            reportarError(mensajeError);
            avanzar();
            return null; 
        } else {
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
                while (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_SI) && 
                       !finDeTokens()) {
                    ASTNodo sentencia = sentencia(); 
                    if (sentencia != null) {
                        nodoCondicional.agregarHijo(sentencia);
                    }
                }

                if (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_SI)) {
                    reportarError("Se esperaba 'fin si' para cerrar el condicional.");
                }
            } else {
                reportarError("Se esperaba 'entonces' después de la condición.");
            }
            
            return nodoCondicional;
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
        if (match(Token.Tipos.ENTERO)) {
            Token numero = obtenerTokenActual(-1);
            return new ASTNodo("Literal", numero.getValor());
        } else if (match(Token.Tipos.DECIMAL)) { 
            Token numero = obtenerTokenActual(-1);
            return new ASTNodo("Literal", numero.getValor());
        } else if (match(Token.Tipos.CADENA)) { 
            Token cadena = obtenerTokenActual(-1);
            return new ASTNodo("Literal", cadena.getValor());
        } else if (match(Token.Tipos.BOOLEANO_TRUE_TEXTUAL)) { 
            return new ASTNodo("Literal", "verdadero"); 
        } else if (match(Token.Tipos.BOOLEANO_FALSE_TEXTUAL)) { 
            return new ASTNodo("Literal", "falso");
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
            if (!finDeTokens()) {
                reportarError("Se esperaba un número, identificador, cadena, booleano, o una expresión entre paréntesis.");
                avanzar();
            }
            return null;
        }
    }

   
    
    private ASTNodo para() {
        if (match(Token.Tipos.PALABRA_RESERVADA_PARA)) {
            ASTNodo nodoPara = new ASTNodo("Para");
            if (!match(Token.Tipos.IDENTIFICADOR)) {
                reportarError("Se esperaba un identificador después de 'para'.");
            } else {
                Token idToken = obtenerTokenActual(-1);
                nodoPara.agregarHijo(new ASTNodo("Identificador", idToken.getValor()));
                if (!match(Token.Tipos.SIMBOLO_ASIGNACION)) {
                    reportarError("Se esperaba ':=' después del identificador.");
                } else {
                    nodoPara.agregarHijo(expresion());
                    if (!match(Token.Tipos.PALABRA_RESERVADA_HASTA)) {
                        reportarError("Se esperaba 'hasta' después de la asignación.");
                    } else {
                        nodoPara.agregarHijo(expresion());
                        if (!match(Token.Tipos.PALABRA_RESERVADA_HACER)) {
                            reportarError("Se esperaba 'hacer' después del límite superior.");
                        } else {
                            while (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_PARA) && !finDeTokens()) {
                                ASTNodo sentencia = sentencia();
                                if(sentencia != null){
                                    nodoPara.agregarHijo(sentencia);
                                }
                            }
                             if (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_PARA)) {
                                reportarError("Se esperaba 'fin para' para cerrar el bucle 'para'.");
                            }
                        }
                    }
                }
            }
            return nodoPara;
        }
        return null;
    }
    
    private ASTNodo mientras() {
        if (match(Token.Tipos.PALABRA_RESERVADA_MIENTRAS)) {
            ASTNodo nodoMientras = new ASTNodo("Mientras");
            if (!match(Token.Tipos.PARENTESIS_ABIERTO)) {
                reportarError("Se esperaba '(' después de 'mientras'.");
            } else {
                nodoMientras.agregarHijo(expresion());
                if (!match(Token.Tipos.PARENTESIS_CERRADO)) {
                    reportarError("Se esperaba ')' después de la condición.");
                } else if (!match(Token.Tipos.PALABRA_RESERVADA_HACER)) {
                    reportarError("Se esperaba 'hacer' después de la condición.");
                } else {
                     while (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_MIENTRAS) && !finDeTokens()) {
                        ASTNodo sentencia = sentencia();
                        if(sentencia != null){
                            nodoMientras.agregarHijo(sentencia);
                        }
                    }
                     if (!matchSecuencia(Token.Tipos.PALABRA_RESERVADA_FIN, Token.Tipos.PALABRA_RESERVADA_MIENTRAS)) {
                        reportarError("Se esperaba 'fin mientras' para cerrar el bucle 'mientras'.");
                    }
                }
            }
            return nodoMientras;
        }
        return null;
    }
    
    private boolean esLlamadaFuncion() {
        Token tokenActual = obtenerTokenActual();
        return tokenActual != null && (tokenActual.getTipo() == Token.Tipos.FUNCION_LIMPIAR_PANTALLA ||
                                       tokenActual.getTipo() == Token.Tipos.FUNCION_ESCRIBIR ||
                                       tokenActual.getTipo() == Token.Tipos.FUNCION_LEER);
    }

    private ASTNodo llamadaFuncion() {
        Token nombreFuncion = obtenerTokenActual();
        avanzar(); 
        ASTNodo nodoLlamada = new ASTNodo("LlamadaFuncion", nombreFuncion.getValor());

        if (match(Token.Tipos.PARENTESIS_ABIERTO)) {
            if (nombreFuncion.getTipo() == Token.Tipos.FUNCION_ESCRIBIR) {
                nodoLlamada.agregarHijo(expresion()); 
            } else if (nombreFuncion.getTipo() == Token.Tipos.FUNCION_LEER) {
                if (!match(Token.Tipos.IDENTIFICADOR)) {
                    reportarError("Se esperaba un identificador dentro de Leer().");
                } else {
                    Token identificador = obtenerTokenActual(-1);
                    nodoLlamada.agregarHijo(new ASTNodo("Identificador", identificador.getValor()));
                }
            }
            if (!match(Token.Tipos.PARENTESIS_CERRADO)) {
                reportarError("Se esperaba ')' después de la llamada a la función.");
            }
        } else {
            reportarError("Se esperaba '(' después del nombre de la función.");
        }

        if (!match(Token.Tipos.DELIMITADOR)) {
            reportarError("Se esperaba ';' al final de la llamada a la función.");
        }

        return nodoLlamada;
    }
    
    private boolean match(Token.Tipos tipoEsperado) {
    	boolean validar=obtenerTokenActual().getTipo() == tipoEsperado;
        if (finDeTokens()&&validar==true) {
        	return true;
        }else if(finDeTokens()&&validar==false){ 
        	return false;      
        }else {
        
        if (obtenerTokenActual().getTipo() == tipoEsperado) {
            avanzar();
            return true;
        }
        return false;
        }
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
        return indiceActual >= tokens.size()-1;
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
        return obtenerTokenActual() != null 
        		&& indiceActual + 1 < tokens.size() 
        		&& obtenerTokenActual().getTipo() == Token.Tipos.IDENTIFICADOR 
        		&& tokens.get(indiceActual + 1).getTipo() == Token.Tipos.SIMBOLO_ASIGNACION;
    }

    private boolean esCondicional() {
        return obtenerTokenActual() != null && obtenerTokenActual().getTipo() == Token.Tipos.PALABRA_RESERVADA_SI;
    }
    
    private boolean matchSecuencia(Token.Tipos... tiposEsperados) {
        int indiceOriginal = indiceActual; 
        for (int i = 0; i < tiposEsperados.length; i++) {
            if (indiceActual + i >= tokens.size() || tokens.get(indiceActual + i).getTipo() != tiposEsperados[i]) {
                indiceActual = indiceOriginal; 
                return false;
            }
        }
        indiceActual += tiposEsperados.length;
        return true;
    }
    
    private boolean esPara() {
        return obtenerTokenActual() != null && obtenerTokenActual().getTipo() == Token.Tipos.PALABRA_RESERVADA_PARA;
    }

    private boolean esMientras() {
        return obtenerTokenActual() != null && obtenerTokenActual().getTipo() == Token.Tipos.PALABRA_RESERVADA_MIENTRAS;
    }
}
