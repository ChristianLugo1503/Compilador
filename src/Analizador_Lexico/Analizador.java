package Analizador_Lexico;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizador {

    public static void main(String[] args){
        String entrada = "11 + 22 - 33";
        ArrayList<Token> tokens = lex(entrada);

        for (Token token: tokens){
            System.out.println(token.getTipo() + " : " + token.getValor());
        }
    }

    private static ArrayList<Token> lex (String entrada){
        final ArrayList<Token> tokens = new ArrayList();
        final StringTokenizer st = new StringTokenizer(entrada);

        while(st.hasMoreTokens()){
            String palabra = st.nextToken();
            boolean banderas = false;

            for (Token.Tipos tokenTipo: Token.Tipos.values()){
                Pattern patron = Pattern.compile(tokenTipo.patron);
                Matcher busqueda = patron.matcher(palabra);

                if (busqueda.find()){
                    Token token = new Token();

                    token.setTipo(tokenTipo);
                    token.setValor(palabra);
                    tokens.add(token);
                    banderas = true;
                }
            }
            if (!banderas){
                throw new RuntimeException("Token invalido");
            }
        }
        return tokens;
    }

}
