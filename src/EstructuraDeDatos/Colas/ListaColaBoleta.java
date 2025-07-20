package EstructuraDeDatos.Colas;

import modelo.Boleta;
import util.BoletaPDF;

public class ListaColaBoleta {

    private NodoBoleta inicio;
    private NodoBoleta fin;

    public ListaColaBoleta() {
        this.inicio = null;
        this.fin = null;
    }

    public boolean esVacio() {
        return inicio == null;
    }

    public NodoBoleta actual() {
        if (esVacio()) {
            System.out.println("La cola está vacía");
            return null;
        }
        return inicio;
    }

    public void eliminar() {
        if (esVacio()) {
            System.out.println("La cola está vacía");
            return;
        }

        if (inicio == fin) {
            inicio = fin = null;
        } else {
            NodoBoleta aux = inicio.getSig();
            inicio = aux;
        }
        System.out.println("Elemento eliminado");
    }

    public void agregar(Boleta dato) {
        NodoBoleta nuevo = new NodoBoleta(dato);
        if (esVacio()) {
            inicio = fin = nuevo;
        } else {
            fin.setSig(nuevo);
            fin = nuevo;
        }
        
        System.out.println("Elemento agregado: " + dato);
        procesarPrimerElemento();
    }

    public void recorrer() {
        if (esVacio()) {
            System.out.println("La lista esta vacia");
            return;
        }
        NodoBoleta  aux = inicio;
        while (aux != null) {
            System.out.print(aux.getDato() + "-->");
            aux = aux.getSig();
        }
        System.out.println("");

    }
    
   // fifo // el primero que ingreso tiene  que generar el boleta pdf
    public void procesarPrimerElemento(){
        Boleta boleta = inicio.getDato();
        eliminar();
        System.out.println("Generando pdf :D  : "+ BoletaPDF.buildFileName(boleta.getVenta().getId(), boleta.getVenta().getFecha()));
        BoletaPDF.generateBoletaPDF(boleta);    
    }

}
