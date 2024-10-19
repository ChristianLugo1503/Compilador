package Analizador_Lexico;

public class Token {
    private String valor;
    private Tipos tipo;

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    enum Tipos{
        NUMERO ("[0-9]+"),
        OPERADOR ("[-+*/]");

        public final String patron;

        Tipos(String s){
            this.patron = s;
        }
    }
}
