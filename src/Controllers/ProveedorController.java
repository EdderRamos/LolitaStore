/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import EstructuraDeDatos.ListaEnlazada.ListaProveedores;

import Modelo.Proveedor;
import Modelo.ProveedorDAO;

import Vista.Home;

/**
 *
 * @author lee_j
 */
public class ProveedorController {

    private Home vista;

    private ProveedorDAO proveedorDao;

    // lista enlazada simple
    private ListaProveedores lesProveedor;

    public ProveedorController(Home vista) {
        this.vista = vista;

        proveedorDao = new ProveedorDAO();
        lesProveedor = new ListaProveedores();

        //cargamos a la estructura de datos :D
        for (Proveedor proveedor : proveedorDao.obtenerProveedores()) {
            lesProveedor.agregar(proveedor);
        }
        vista.actualizarTablaDeProveedores(lesProveedor);
    }

    public void enActualizarProveedor(Proveedor proveedor) {
        if (proveedor.getId() == 0) {
            vista.mostrarMensaje("Selecciona una fila para editar");
            return;
        }

        if (proveedor.getRuc() == 0 || proveedor.getNombre().isEmpty() || proveedor.getTelefono() == 0 || proveedor.getDireccion().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }

        boolean actualizado = proveedorDao.actualizarProveedor(proveedor);

        if (actualizado) {
            boolean actualizadoEnLista = lesProveedor.actualizarProveedor(proveedor);

            if (actualizadoEnLista) {
                vista.mostrarMensaje("Datos del proveedor actualizados");
                vista.actualizarTablaDeProveedores(lesProveedor);
                vista.actualizarImputsEnProveedores(null);
            } else {
                vista.mostrarMensaje("Proveedor no encontrado en la lista enlazada.");
            }
        } else {
            vista.mostrarMensaje("Hubo un error al actualizar el Proveedor en la base de datos.");
        }
    }

    public void enEliminarProveedor(int id) {
        if (id == 0) {
            vista.mostrarMensaje("Selecciona una fila para eliminar (usa el buscador)");
            return;
        }
        if (proveedorDao.buscarProveedorPorId(id).getId() == 0) {
            vista.mostrarMensaje("El Proveedor no existe en BD");
            return;
        }
        boolean eliminadoEnBD = proveedorDao.eliminarProveedorPorId(id);

        if (!eliminadoEnBD) {
            vista.mostrarMensaje("Algo salio mal, no se pudo eliminar en BD");
            return;
        }
        boolean eliminadoEnLes = lesProveedor.eliminarProveedor(id);

        if (eliminadoEnLes) {
            vista.mostrarMensaje("Proveedor eliminado exitosamente");
            vista.actualizarTablaDeProveedores(lesProveedor);
            vista.actualizarImputsEnProveedores(null);
        } else {
            vista.mostrarMensaje("Algo salio mal!");
        }
    }

    public void enAgregarProveedor(Proveedor proveedor) {
        if (proveedor.getRuc() == 0 || proveedor.getNombre().isEmpty() || proveedor.getTelefono() == 0 || proveedor.getDireccion().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }
        int id = proveedorDao.agregarProveedor(proveedor);

        boolean registrado = id != -1;
        if (registrado) {
            proveedor.setId(id);
            lesProveedor.agregar(proveedor);
            vista.mostrarMensaje("Proveedor registrado exitosamente");
            vista.actualizarTablaDeProveedores(lesProveedor);
            vista.actualizarImputsEnProveedores(null);
        } else {
            vista.mostrarMensaje("Hubo un error al registrar el Proveedor en la base de datos.");
        }

    }

    public void enBuscarProveedor(int ruc) {
        if (ruc == 0) {
            vista.mostrarMensaje("Ingrese RUC para buscar");
            return;
        }

        Proveedor proveedor = lesProveedor.buscarPorRuc(ruc);

        if (proveedor != null) {
            vista.actualizarTablaDeProveedores(lesProveedor);
            vista.actualizarImputsEnProveedores(proveedor);
        } else {
            vista.mostrarMensaje("Proveedor no encontrado");
        }
    }

}
