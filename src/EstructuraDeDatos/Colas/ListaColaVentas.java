package EstructuraDeDatos.Colas;

import modelo.Venta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ListaColaVentas {

    private NodoVenta inicio;
    private NodoVenta fin;

    public ListaColaVentas() {
        this.inicio = null;
        this.fin = null;
    }

    public boolean esVacio() {
        return inicio == null;
    }

    public NodoVenta actual() {
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
            NodoVenta aux = inicio.getSig();
            inicio = aux;
        }
        System.out.println("Elemento eliminado");
    }

    public void agregar(Venta dato) {
        NodoVenta nuevo = new NodoVenta(dato);
        if (esVacio()) {
            inicio = fin = nuevo;
        } else {
            fin.setSig(nuevo);
            fin = nuevo;
        }
        System.out.println("Elemento agregado: " + dato);
    }

    public void recorrer() {
        if (esVacio()) {
            System.out.println("La lista esta vacia");
            return;
        }
        NodoVenta aux = inicio;
        while (aux != null) {
            System.out.print(aux.getDato() + "-->");
            aux = aux.getSig();
        }
        System.out.println("");

    }

    public void recorrerAdelante(DefaultTableModel tblModelo) {
        if (esVacio()) {
            JOptionPane.showMessageDialog(null, "No hay elementos en la lista");
            return;
        }

        NodoVenta aux = inicio;

        while (aux != null) {
            /*
            tblModelo.addRow(new Object[]{
                aux.getDato().getMarca(),
                aux.getDato().getModelo(),
                aux.getDato().getRam(),
                aux.getDato().getAlmacenamiento(),
                aux.getDato().getPrecio()
            });
            aux = aux.getSig();
            */
        }

    }

}
