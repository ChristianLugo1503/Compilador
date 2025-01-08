package analizador_Lexico;

import javax.swing.*;

import sintaxis.Parser;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class AnalizadorGUI {
    public static void main(String[] args) {
        JFrame ventana = new JFrame("DATOS");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(900, 600);

        String[] columnas = {"Lexema", "Tipo", "Fila", "Columna"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        String archivoSinProcesar = "C:\\Users\\laptop\\Documents\\workspace-spring-tool-suite-4-4.23.1.RELEASE\\Compilador2\\src\\Analizador_Lexico\\Prueba.txt";
              AnalizadorLexico lexico = new AnalizadorLexico();                
        
        Parser parse = new Parser();
        parse.analizar();
        
            ArrayList<Token> tokens = lexico.lex(archivoSinProcesar);
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
        

        JTable tabla = new JTable(modelo);
        JScrollPane panel = new JScrollPane(tabla);
        ventana.add(panel);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
