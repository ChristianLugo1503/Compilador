import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        
        
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
                    
                    lineaa = lineaa.replaceAll("\\s+", "");
                    lineasProcesadas.add(lineaa);
                }
            }

           
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
}