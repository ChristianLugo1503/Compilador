package Analizador_Lexico;

public class Token {
    //Atributos
    private String valor;
    private Tipos tipo;
    private int linea;
    private int columna;

    //Constructor
    public Token(Tipos tipo, String valor, int linea, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
    }

    //Getters
    public String getValor() {
        return valor;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    //Enumeracion -> Expresiones regulares
    enum Tipos{
        PALABRA_RESERVADA("inicio|fin|si|fin|para|mientras|hasta|hacer"),
        DECIMAL("[-+]?(?:[0-9]+\\.[0-9]+|\\.[0-9]+)"), // Números decimales
        ENTERO("[-+]?[0-9]+"),           // Números enteros (positivos y negativos)
        CADENA("\"[^\"]*\""),            // Cadenas de texto entre comillas
        BOOLEANO("(true|false)"),        // Valores booleanos
        OPERADOR_ARITMÉTICO ("[-+*/]"),
        OPERADOR_RELACIONAL("(==|<>|<=|>=|<|>)"),
        OPERADOR_LOGICO("(\\|\\||&&)"),
        TIPO_DE_DATO("entero|decimal|cadena|booleano"),
        FUNCIÓN_IO("Escribir|Leer|LimpiarPantalla"),
        DELIMITADOR(";"),
        IDENTIFICADOR("[a-zA-Z_][a-zA-Z0-9_]*"),
        SIMBOLO_ESPECIAL(":=|\\(|\\)");

        public final String patron;

        Tipos(String s){
            this.patron = s;
        }
    }
}
