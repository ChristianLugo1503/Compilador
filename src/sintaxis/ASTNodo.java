package sintaxis;

import java.util.ArrayList;
import java.util.List;

public class ASTNodo {
	 private String tipo; 
	    private String valor;
	    private List<ASTNodo> hijos; 

	    public ASTNodo(String tipo) {
	        this.tipo = tipo;
	        this.hijos = new ArrayList<>();
	    }

	    public ASTNodo(String tipo, String valor) {
	        this.tipo = tipo;
	        this.valor = valor;
	        this.hijos = new ArrayList<>();
	    }

	    public String getTipo() {
	        return tipo;
	    }

	    public String getValor() {
	        return valor;
	    }

	    public List<ASTNodo> getHijos() {
	        return hijos;
	    }

	    public void agregarHijo(ASTNodo hijo) {
	        this.hijos.add(hijo);
	    }

	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append(tipo);
	        if (valor != null) {
	            sb.append(" (").append(valor).append(")");
	        }
	        if (!hijos.isEmpty()) {
	            sb.append(" -> [");
	            for (ASTNodo hijo : hijos) {
	                sb.append(hijo.toString()).append(", ");
	            }
	            sb.delete(sb.length() - 2, sb.length());
	            sb.append("]");
	        }
	        return sb.toString();
	    }
}
