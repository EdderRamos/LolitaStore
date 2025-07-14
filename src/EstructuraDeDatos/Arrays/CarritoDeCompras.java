/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.Arrays;

import modelo.Producto;
import java.util.ArrayList;

/**
 *
 * @author lee_j
 */
public class CarritoDeCompras {

    private ArrayList<CarritoItem> items;

    public CarritoDeCompras() {

        items = new ArrayList<>();
    }

    public void agregarProducto(Producto producto, int cantidad) {
        for (CarritoItem item : items) {
            if (item.getProducto().getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new CarritoItem(producto, cantidad));
    }

    public boolean eliminarPorIndice(int index) {
        try {
            items.remove(index);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public ArrayList<CarritoItem> getItems() {
        return items;
    }

    public double calcularTotal() {
        double total = 0;
        for (CarritoItem item : items) {
            total += item.getProducto().getPrecio() * item.getCantidad();
        }
        return total;
    }

    public void limpiar() {
        items.clear();
    }

}
