/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.Pilas;

import EstructuraDeDatos.Colas.NodoVenta;
import modelo.Venta;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author lee_j
 */
public class ListaPilaVentas {

    private NodoVenta inicio, fin;

    public ListaPilaVentas() {
        inicio = fin = null;
    }

    public boolean esVacio() {
        return (inicio == null);
    }

    public NodoVenta agregar(Venta dato) { // push
        NodoVenta nuevo = new NodoVenta(dato);
        if (esVacio()) {
            inicio = nuevo;
        } else {
            fin.setSig(nuevo);
        }
        fin = nuevo;
        return fin;
    }

    public NodoVenta actual() {
        if (esVacio()) {
            System.out.println("La lista esta vacia");
            return null;
        }
        return fin;
    }

    public void eliminar() {
        if (esVacio()) {
            System.out.println("La lista esta vacia");
            return;
        }

        if (inicio == fin) {
            inicio = fin = null;
        }
        NodoVenta aux = inicio;
        while (aux != null) {
            System.out.print(aux.getDato() + "-->");
            if (aux.getSig() == fin) {
                aux.setSig(null);
                fin = aux;
                break;
            }
            aux = aux.getSig();
        }
        System.out.println("");

    }

    public void recorrerATabla(DefaultTableModel modelo) {
        if (esVacio()) {
            System.out.println("La lista esta vacia");
            return;
        }
        NodoVenta aux = inicio;
        while (aux != null) {
            Venta venta = aux.getDato();
            Object[] ob = new Object[5];
            ob[0] = venta.getId();
            ob[1] = venta.getCliente();
            ob[2] = venta.getVendedor();
            ob[3] = venta.getFecha();
            ob[4] = venta.getTotal();
            modelo.addRow(ob);
            aux = aux.getSig();
        }
        System.out.println("");

    }
}
