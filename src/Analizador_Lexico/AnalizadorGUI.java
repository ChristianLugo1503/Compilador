package Analizador_Lexico;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;

public class AnalizadorGUI {
    private JPanel panel1;
    private JTable table1;

    public static void main(String[] args) {
        // Crear un JFrame
        JFrame ventana = new JFrame("DATOS");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(900, 600);

        String[] columnas = {"Lexema", "Tipo", "Fila", "Columna"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        String archivoSinProcesar = "/home/chris/Documentos/Edgar Trabajos/Compilador/src/Prueba.txt";
        String archivoProcesado = preprocesarArchivo(archivoSinProcesar);

        // Solo procesamos si el archivo sin procesar existe y se puede leer
        if (archivoProcesado != null) {
            ArrayList<Token> tokens = lex(archivoSinProcesar);
            for (Token token : tokens) {
                modelo.addRow(new Object[]{
                        token.getValor(),
                        token.getTipo(),
                        token.getLinea(),
                        token.getColumna()
                });
            }
        }

        // Crear el JTable con el modelo
        JTable tabla = new JTable(modelo);

        // Agregar la tabla a un JScrollPane para permitir el desplazamiento
        JScrollPane panel = new JScrollPane(tabla);
        ventana.add(panel); // Agregar el panel a la ventana

        // Hacer visible la ventana
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
                // Eliminar comentarios después de //
                int indiceComentario = lineaa.indexOf("//");
                if (indiceComentario != -1) {
                    lineaa = lineaa.substring(0, indiceComentario);
                }

                // Eliminar espacios y tabulaciones al principio y al final
                lineaa = lineaa.trim();

                // Si la línea no está vacía después de eliminar los comentarios y espacios
                if (!lineaa.isEmpty()) {
                    // Eliminar espacios y tabulaciones adicionales dentro de la línea
                    lineaa = lineaa.replaceAll("\\s+", "");
                    lineasProcesadas.add(lineaa);
                }
            }

            // Guardar el resultado en un nuevo archivo
            BufferedWriter writer = new BufferedWriter(new FileWriter("codigoProcesado.txt"));
            for (String lineaProcesada : lineasProcesadas) {
                writer.write(lineaProcesada);
                writer.newLine();
            }
            writer.close();

            System.out.println("Código preprocesado guardado en 'codigoProcesado.txt'");
            return lineasProcesadas.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Token> lex(String rutaArchivo) {
        ArrayList<Token> tokens = new ArrayList<>();
        StringBuilder entrada = new StringBuilder();

        // Leer el archivo sin procesar
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                entrada.append(linea).append("\n"); // Mantener el contenido original
            }
        } catch (IOException e) {
            e.printStackTrace();
            return tokens; // Retornar lista vacía en caso de error
        }

        String patrones = String.join("|",
                Token.Tipos.PALABRA_RESERVADA.patron,
                Token.Tipos.DECIMAL.patron,
                Token.Tipos.ENTERO.patron,
                Token.Tipos.CADENA.patron,
                Token.Tipos.BOOLEANO.patron,
                Token.Tipos.OPERADOR_ARITMÉTICO.patron,
                Token.Tipos.OPERADOR_RELACIONAL.patron,
                Token.Tipos.OPERADOR_LOGICO.patron,
                Token.Tipos.TIPO_DE_DATO.patron,
                Token.Tipos.FUNCIÓN_IO.patron,
                Token.Tipos.DELIMITADOR.patron,
                Token.Tipos.IDENTIFICADOR.patron,
                Token.Tipos.SIMBOLO_ESPECIAL.patron
        );
        //System.out.println(patrones);
        //Como resultado se optiene una cadena con todas las expresiones regulares separadas con un |
        //Ejemplo= "inicio|fin|[-+]?(?:[0-9]+\\.[0-9]+|\\.[0-9]+)|..."

        Matcher matcher = Pattern.compile(patrones).matcher(entrada.toString());
        int lineaActual = 1; // Contador de líneas

        // Inicializa el índice de búsqueda
        int ultimoInicio = 0;

        // Se buscan los tokens que coinciden con el patrón
        while (matcher.find()) {
            String match = matcher.group();
            Token.Tipos tipo = determinarTipo(match);
            //System.out.println(match);

            // Calcular la posición de la columna
            int columna = matcher.start() - entrada.toString().lastIndexOf('\n', matcher.start() - 1);

            // Contar cuántas líneas han pasado desde la última coincidencia
            int numSaltosLinea = 0;
            for (int i = ultimoInicio; i < matcher.start(); i++) { //si es un salto de linea se incrementa el marcador
                if (entrada.charAt(i) == '\n') {
                    numSaltosLinea++;
                }
            }

            // Actualizar el número de línea actual
            lineaActual += numSaltosLinea;

            tokens.add(new Token(tipo, match, lineaActual, columna));

            // Actualizar el índice de búsqueda
            ultimoInicio = matcher.start() + match.length();
        }

        return tokens;
    }


    private static Token.Tipos determinarTipo(String match) {
        for (Token.Tipos tipo : Token.Tipos.values()) {
            if (match.matches(tipo.patron)) {
                return tipo;
            }
        }
        throw new RuntimeException("Token inválido: " + match);
    }
}
