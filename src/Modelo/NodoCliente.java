package Modelo;

public class NodoCliente {
    Cliente cliente;
    NodoCliente siguiente;

    public NodoCliente(Cliente cliente) {
        this.cliente = cliente;
        this.siguiente = null;
    }
}
