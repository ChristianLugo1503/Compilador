package Analizador_Lexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/*
    Desarrolla una función que lea un archivo de texto
    caracter por caracter y lo imprima en pantalla. Modo consola.
*/
import java.io.BufferedReader; // Optimizar el proceso de lectura
import java.io.FileReader; // Leer el archivo de texto
import java.io.IOException; // Excepciones para el catch

public class RecorrerArchivo {
    public static void recorrerArchivo(String rutaArchivo){
        //Intenta leer el archivo de texto con FileReader y optimiza el proceso de lectura con BufferedReader
        try (BufferedReader read = new BufferedReader(new FileReader(rutaArchivo))) {
            int caracter;
            while((caracter = read.read()) != -1){ //Leer caracter por caracter e imprimirlo en la consola
                System.out.print((char) caracter);
            }
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String rutaArchivo = "/home/chris/IdeaProjects/Edgar/src/prueba.txt";
        recorrerArchivo(rutaArchivo);
    }
}
