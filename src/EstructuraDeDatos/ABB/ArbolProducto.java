/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.ABB;

import Modelo.Producto;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author lee_j
 */
public class ArbolProducto {

    private NodoProducto raiz;

    public void insertar(Producto producto) {
        raiz = insertar(raiz, producto);
    }

    private NodoProducto insertar(NodoProducto nodo, Producto producto) {
        if (nodo == null) {
            return new NodoProducto(producto);
        }
        int cmp = producto.getCodigo().compareTo(nodo.getProducto().getCodigo());

        if (cmp < 0) {
            nodo.setIzq(insertar(nodo.getIzq(), producto));
        } else {
            nodo.setDer(insertar(nodo.getDer(), producto));
        }
        return nodo;
    }

    public Producto buscar(String codigo) {
        return buscar(raiz, codigo);
    }

    public Producto buscar(NodoProducto nodo, String codigo) {
        if (nodo == null) {
            return null;
        }
        int cmp = codigo.compareTo(nodo.getProducto().getCodigo());

        if (cmp == 0) {
            return nodo.getProducto();
        }
        if (cmp < 0) {
            return buscar(nodo.getIzq(), codigo);
        }
        return buscar(nodo.getDer(), codigo);

    }

    public void inOrden() {
        inOrden(raiz);
    }

    private void inOrden(NodoProducto nodo) {
        if (nodo != null) {
            inOrden(nodo.getIzq());
            System.out.println(nodo.getProducto());
            inOrden(nodo.getDer());
        }
    }

    public boolean actualizar(Producto producto) {
        Producto productoEnABB = buscar(producto.getCodigo());
        if (productoEnABB != null) {
            productoEnABB.setNombre(producto.getNombre());
            productoEnABB.setPrecio(producto.getPrecio());
            productoEnABB.setCodigo(producto.getCodigo());
            productoEnABB.setProveedor(producto.getProveedor());
            productoEnABB.setStock(producto.getStock());
            return true;
        }
        return false;
    }

    public boolean eliminar(String codigo) {
        if (raiz == null) {
            return false;
        }

        raiz = eliminar(raiz, codigo);
        return raiz != null;
    }

    private NodoProducto eliminar(NodoProducto raiz, String codigo) {
        if (raiz == null) {
            return raiz;
        }

        if (codigo.compareTo(raiz.getProducto().getCodigo()) < 0) {
            raiz.setIzq(eliminar(raiz.getIzq(), codigo));
        } else if (codigo.compareTo(raiz.getProducto().getCodigo()) > 0) {
            raiz.setDer(eliminar(raiz.getDer(), codigo));
        } else {
            if (raiz.getIzq() == null) {
                return raiz.getDer();
            } else if (raiz.getDer() == null) {
                return raiz.getIzq();
            }
            raiz.setProducto(obtenerMinimo(raiz.getDer()).getProducto());
            raiz.setDer(eliminar(raiz.getDer(), raiz.getProducto().getCodigo()));
        }
        return raiz;
    }

    private NodoProducto obtenerMinimo(NodoProducto raiz) {
        NodoProducto actual = raiz;
        while (actual.getIzq() != null) {
            actual = actual.getIzq();
        }
        return actual;
    }

    public void mostrarOrdenado(String option, DefaultTableModel modelo) {
        if (option.equals(OrdenamientoABB.PREORDEN.getNombre())) {
            preOrdenParaTabla(raiz, modelo);
        } else if (option.equals(OrdenamientoABB.INORDEN.getNombre())) {
            inOrdenParaTabla(raiz, modelo);
        } else if (option.equals(OrdenamientoABB.POSTORDEN.getNombre())) {
            postOrdenParaTabla(raiz, modelo);
        } else {
            inOrdenParaTabla(raiz, modelo);
        }
    }

    //ascendente
    private void inOrdenParaTabla(NodoProducto nodo, DefaultTableModel model) {
        if (nodo != null) {
            inOrdenParaTabla(nodo.getIzq(), model);
            cargarObjeto(nodo.getProducto(), model);
            inOrdenParaTabla(nodo.getDer(), model);
        }
    }

    //primero la raiz
    private void preOrdenParaTabla(NodoProducto nodo, DefaultTableModel model) {
        if (nodo != null) {
            cargarObjeto(nodo.getProducto(), model);
            preOrdenParaTabla(nodo.getIzq(), model);
            preOrdenParaTabla(nodo.getDer(), model);
        }
    }

    //descendente
    private void postOrdenParaTabla(NodoProducto nodo, DefaultTableModel model) {
        if (nodo != null) {
            postOrdenParaTabla(nodo.getIzq(), model);
            postOrdenParaTabla(nodo.getDer(), model);
            cargarObjeto(nodo.getProducto(), model);
        }
    }

    private void cargarObjeto(Producto p, DefaultTableModel modelo) {
        Object[] fila = new Object[6];
        fila[0] = p.getId();
        fila[1] = p.getCodigo();
        fila[2] = p.getNombre();
        fila[3] = p.getProveedor();
        fila[4] = p.getStock();
        fila[5] = p.getPrecio();
        modelo.addRow(fila);
    }

}
