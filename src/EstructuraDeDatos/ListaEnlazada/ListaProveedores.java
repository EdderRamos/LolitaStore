/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.ListaEnlazada;
import modelo.Proveedor;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author lee_j
 */
public class ListaProveedores {

    private NodoProveedor cabeza;

    public void agregar(Proveedor proveedor) {
        NodoProveedor nuevo = new NodoProveedor(proveedor);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoProveedor actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
    }

    public Proveedor buscarPorRuc(int ruc) {
        NodoProveedor actual = cabeza;
        while (actual != null) {
            if (actual.getProveedor().getRuc() == ruc) {
                return actual.getProveedor();
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

        NodoProveedor actual = cabeza;

        while (actual != null) {
            Object[] ob = new Object[5];
            Proveedor nCliente = actual.getProveedor();

            ob[0] = nCliente.getId();
            ob[1] = nCliente.getRuc();
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

    public boolean eliminarProveedor(int id) {
        if (cabeza == null) {
            return false;
        }

        if (cabeza.getProveedor().getId() == id) {
            cabeza = cabeza.getSiguiente();
            return true;
        }

        NodoProveedor actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getProveedor().getId() == id) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                return true;
            }
            actual = actual.getSiguiente();
        }

        return false;
    }

    public boolean actualizarProveedor(Proveedor proveedor) {
        NodoProveedor actual = cabeza;
        while (actual != null) {
            if (actual.getProveedor().getId() == proveedor.getId()) {
                actual.setProveedor(proveedor);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

}
