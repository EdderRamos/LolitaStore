package EstructuraDeDatos.ListaEnlazada;

import modelo.Cliente;
import javax.swing.table.DefaultTableModel;

public class ListaClientes {

    private NodoCliente cabeza;

    public void agregar(Cliente cliente) {
        NodoCliente nuevo = new NodoCliente(cliente);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoCliente actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
    }

    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        NodoCliente actual = cabeza;
        while (actual != null) {
            sb.append("DNI: ").append(actual.getCliente().getDni())
                    .append(" - Nombre: ").append(actual.getCliente().getNombre()).append("\n");
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    public Cliente buscarPorDni(int dni) {
        NodoCliente actual = cabeza;
        while (actual != null) {
            if (actual.getCliente().getDni() == dni) {
                return actual.getCliente();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public void reiniciar() {
        cabeza = null;
    }

    public void mostrarEnTabla(DefaultTableModel modelo) {
       if (this.cabeza == null) {
           return;
        }
            
        NodoCliente actual = cabeza;

        while (actual != null) {
            Object[] ob = new Object[5];
            Cliente nCliente = actual.getCliente();

            ob[0] = nCliente.getId();
            ob[1] = nCliente.getDni();
            ob[2] = nCliente.getNombre();
            ob[3] = nCliente.getTelefono();
            ob[4] = nCliente.getDireccion();
            modelo.addRow(ob);
            actual = actual.getSiguiente();
        }

    }
    public void limpiar() {
        cabeza = null;
    }

    public boolean eliminarCliente(int id) {
        if (cabeza == null) {
            return false;
        }

        if (cabeza.getCliente().getId() == id) {
            cabeza = cabeza.getSiguiente();
            return true;
        }

        NodoCliente actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getCliente().getId() == id) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                return true;
            }
            actual = actual.getSiguiente();
        }

        return false;
    }

    public boolean actualizarCliente(Cliente cliente) {
        NodoCliente actual = cabeza;
        while (actual != null) {
            if (actual.getCliente().getId()== cliente.getId()) {
                actual.setCliente(cliente);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

}
