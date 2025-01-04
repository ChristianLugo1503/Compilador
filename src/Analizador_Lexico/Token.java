package Analizador_Lexico;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    public enum Tipos {
        PALABRA_RESERVADA_INICIO("(?i)\\binicio\\b"),
        PALABRA_RESERVADA_FIN("(?i)\\bfin\\b"),
        PALABRA_RESERVADA_SI("(?i)\\bsi\\b"),
        PALABRA_RESERVADA_PARA("(?i)\\bpara\\b"),
        PALABRA_RESERVADA_MIENTRAS("(?i)\\bmientras\\b"),
        PALABRA_RESERVADA_HASTA("(?i)\\bhasta\\b"),
        PALABRA_RESERVADA_HACER("(?i)\\bhacer\\b"),
        DECIMAL("[-+]?(?:[0-9]+\\.[0-9]+|\\.[0-9]+)"),
        ENTERO("[-+]?[0-9]+"),
        CADENA("\"[^\"]*\""),
        BOOLEANO_TRUE_TEXTUAL("verdadero"),
        BOOLEANO_TRUE_NUMERICO("1"),
        BOOLEANO_FALSE_TEXTUAL("falso"),
        BOOLEANO_FALSE_NUMERICO("0"),
        OPERADOR_ARITMÉTICO_DIVISION("/"),
        OPERADOR_ARITMETICO_RESTA("-"),
        OPERADOR_ARITMETICO_SUMA("\\+"),
        OPERADOR_ARITMETICO_MULTIPLICACION("\\*"),
        OPERADOR_MAYOR_IGUAL(">="),
        OPERADOR_MENOR_IGUAL("<="),
        OPERADOR_DISTINTO("<>"),
        OPERADOR_MAYOR(">"),
        OPERADOR_MENOR("<"),
        OPERADOR_IGUAL("=="),
        OPERADOR_OR("\\|"),
        
        OPERADOR_AND("&&"),
        DATO_ENTERO("entero"),
        DATO_DECIMAL("decimal"),
        DATO_CADENA("cadena"),
        DATO_BOOLEANO("booleano"),
        FUNCION_ESCRIBIR("Escribir"),
        FUNCION_LEER("Leer"),
        FUNCION_LIMPIAR_PANTALLA("LimpiarPantalla"),
        DELIMITADOR(";"),
        IDENTIFICADOR("[a-zA-Z_][a-zA-Z0-9_]*"),
        SIMBOLO_ESPECIAL("\\(|\\)"),
        SIMBOLO_ASIGNACION(":=");

        public final String patron;

        Tipos(String patron) {
            this.patron = patron;
        }

           	public Matcher matcher(String texto) {
            Pattern p = Pattern.compile(this.patron);
            return p.matcher(texto);
        }
    }

    private final Tipos tipo;
    private final String valor;
    private final int linea;
    private final int columna;

    public Token(Tipos tipo, String valor, int linea, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
}