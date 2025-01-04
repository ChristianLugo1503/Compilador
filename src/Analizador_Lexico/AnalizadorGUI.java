package Analizador_Lexico;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;

public class AnalizadorGUI {
    public static void main(String[] args) {
        JFrame ventana = new JFrame("DATOS");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(900, 600);

        String[] columnas = {"Lexema", "Tipo", "Fila", "Columna"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        String archivoSinProcesar = "C:\\Users\\laptop\\Documents\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\Compilador2\\src\\Analizador_Lexico\\Prueba.txt";
                              
        String archivoProcesado=preprocesarArchivo(archivoSinProcesar);
        
        if (archivoProcesado != null) {
            ArrayList<Token> tokens = lex(archivoProcesado);
            System.out.println("GUI aqui");
            for (Token token : tokens) {
            	
            	System.out.println("Valor: "+ token.getValor()+" Tipo: "+token.getTipo()+" Linea: "+token.getLinea()+" Columna: "+token.getColumna());
                modelo.addRow(new Object[]{
                        token.getValor(),
                        token.getTipo(),
                        token.getLinea(),
                        token.getColumna()
                });
            }
        }

        JTable tabla = new JTable(modelo);
        JScrollPane panel = new JScrollPane(tabla);
        ventana.add(panel);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    public static String preprocesarArchivo(String rutaArchivo) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
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

            System.out.println("documento generado correctamente");
            return "C:\\Users\\laptop\\Documents\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\Compilador2\\codigoProcesado.txt";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static ArrayList<Token> lex(String rutaArchivo) {
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