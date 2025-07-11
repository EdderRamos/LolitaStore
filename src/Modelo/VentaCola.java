/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author laptop
 */
public class VentaCola {

    private VentaNodo inicio, fin;

    public VentaCola() {
        inicio = fin = null;
    }

    public boolean esVacia() {
        return inicio == null;
    }

    public void agregar(Venta venta) {
        VentaNodo nuevo = new VentaNodo(venta);
        if (esVacia()) {
            inicio = nuevo;
        } else {
            fin.setSig(nuevo);
        }
        fin = nuevo;
    }

    public Venta eliminar() {
        if (esVacia()) {
            throw new RuntimeException("La cola está vacía");
        }

        Venta dato = inicio.getDato();

        if (inicio == fin) {
            inicio = fin = null;
        } else {
            inicio = inicio.getSig();
        }
        return dato;
    }

    public boolean modificar(int id, Venta nuevaTienda) {
        if (esVacia()) {
            return false;
        }

        VentaNodo aux = inicio;
        while (aux != null) {
            if (aux.getDato().getId() == id) {
                aux.setDato(nuevaTienda);
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    public Venta actual() {
        if (esVacia()) {
            return null;
        }
        return inicio.getDato();
    }

    public void recorrer() {

        System.out.println("Recorriendo");
        if (esVacia()) {
            System.out.println("La cola esta vacia");
            return;
        }

        VentaNodo aux = inicio;

        while (aux != null) {
            System.out.print(aux.getDato() + "-->");
            aux = aux.getSig();
        }
        System.out.println("");

    }


    
    public void mostrar(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        if (esVacia()) {
            JOptionPane.showMessageDialog(null, "Las ventas estan vacía");
            return;
        }

        VentaNodo aux = inicio;
        while (aux != null) {
            Venta v = aux.getDato();
            modelo.addRow(new Object[]{v.getId(), v.getCliente(), v.getVendedor(), v.getTotal()});
            aux = aux.getSig();
        }
    }

    public Venta buscarPorId(int id) {
        if (esVacia()) {
            return null;
        }

        VentaNodo aux = inicio;
        while (aux != null) {
            if (aux.getDato().getId() == id) {
                return aux.getDato();
            }
            aux = aux.getSig();
        }
        return null;
    }
}
