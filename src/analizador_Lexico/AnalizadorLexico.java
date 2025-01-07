package analizador_Lexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorLexico {
	
	private List<String> errores = new ArrayList<>();
	
	public AnalizadorLexico() {
	}
		
	public List<String> getErrores() {
		return errores;
	}

	public void setErrores(List<String> errores) {
		this.errores = errores;
	}



	public static String preprocesarArchivo(String ruta) {
		
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ruta));
            List<String> lineasCodigo = new ArrayList<>();
            String linea;

            while ((linea = reader.readLine()) != null) {
                lineasCodigo.add(linea);
            }
            reader.close();

            List<String> lineasProcesadas = new ArrayList<>();

            for (String lineaa : lineasCodigo) {
                int indiceComentario = lineaa.indexOf("//");
                if (indiceComentario != -1) {
                    lineaa = lineaa.substring(0, indiceComentario);
                }

                lineaa = lineaa.trim(); 

                if (!lineaa.isEmpty()) {
                 
                    lineaa = lineaa.replaceAll("\\s+", " "); 
                    lineasProcesadas.add(lineaa);
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("codigoProcesado.txt"));
            for (String lineaProcesada : lineasProcesadas) {
                writer.write(lineaProcesada);
                writer.newLine();
            }
            writer.close();

            System.out.println("documento generado correctamente en: \"C:\\\\Users\\\\laptop\\\\Documents\\\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\\\Compilador2\\\\codigoProcesado.txt\"");
           
             String rutaLex ="C:\\Users\\laptop\\Documents\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\Compilador2\\codigoProcesado.txt";
             return rutaLex;
        } catch (IOException e) {
            e.printStackTrace();
            return "Ha habido un error!!!: "+ e;
        }
		
        
        
    }
    
    public ArrayList<Token> lex(String ruta) {
    	
    	String rutaArchivo=preprocesarArchivo(ruta);
        ArrayList<Token> tokens = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numLinea = 0;

            while ((linea = reader.readLine()) != null) {
                numLinea++;
                int columna = 0;

                while (columna < linea.length()) { 
                	
                    if (Character.isWhitespace(linea.charAt(columna))) {
                        columna++;
                        continue; 
                    }
                    
                    boolean matchFound = false;

                    
                    if (linea.startsWith(":=", columna)) {
                        tokens.add(new Token(Token.Tipos.SIMBOLO_ASIGNACION, ":=", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    } else if (linea.startsWith(">=", columna)) {
                        tokens.add(new Token(Token.Tipos.OPERADOR_MAYOR_IGUAL, ">=", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    } else if (linea.startsWith("<=", columna)) {
                        tokens.add(new Token(Token.Tipos.OPERADOR_MENOR_IGUAL, "<=", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    } else if (linea.startsWith("<>", columna)) {
                        tokens.add(new Token(Token.Tipos.OPERADOR_DISTINTO, "<>", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    } else if (linea.startsWith("==", columna)) {
                        tokens.add(new Token(Token.Tipos.OPERADOR_IGUAL, "==", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    } else if (linea.startsWith("&&", columna)) {
                        tokens.add(new Token(Token.Tipos.OPERADOR_AND, "&&", numLinea, columna + 1));
                        columna += 2;
                        matchFound = true;
                    }
                    else {
                        for (Token.Tipos tipo : Token.Tipos.values()) {
                            Pattern pattern = Pattern.compile("^" + tipo.patron);
                            Matcher matcher = pattern.matcher(linea.substring(columna)); 

                            if (matcher.find()) {
                                String match = matcher.group();
                                tokens.add(new Token(tipo, match, numLinea, columna + 1));
                                columna += match.length();
                                matchFound = true;
                                break;
                            }
                        }
                    }
                    
                    if (!matchFound) {
                        System.err.println("Error léxico en la línea " + numLinea + ", columna " + (columna + 1) + ": Caracter no reconocido '" + linea.charAt(columna) + "'");
                       char lexema=linea.charAt(columna);
                       String columnaStr = String.valueOf(columna);
                       String lineaStr = String.valueOf(numLinea);
                       errores.add("Línea: " + lineaStr + ", Columna: " + columnaStr + ", Lexema: " + lexema);
                        columna++; 
                        
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }
}
