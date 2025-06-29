package Modelo;

public class ListaClientes {
    private NodoCliente cabeza;

    public void agregar(Cliente cliente) {
        NodoCliente nuevo = new NodoCliente(cliente);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoCliente actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        NodoCliente actual = cabeza;
        while (actual != null) {
            sb.append("DNI: ").append(actual.cliente.getDni())
              .append(" - Nombre: ").append(actual.cliente.getNombre()).append("\n");
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    public Cliente buscarPorDni(int dni) {
        NodoCliente actual = cabeza;
        while (actual != null) {
            if (actual.cliente.getDni() == dni) {
                return actual.cliente;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public void reiniciar() {
        cabeza = null;
    }
}
