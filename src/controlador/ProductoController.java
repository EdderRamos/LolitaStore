/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import EstructuraDeDatos.ABB.ArbolProducto;
import modelo.Producto;
import modelo.ProductosDAO;
import modelo.Proveedor;
import vista.Home;
import java.util.List;

/**
 *
 * @author eramos
 */
public class ProductoController {

    private Home vista;

    private ProductosDAO productoDao;

    // abb
    private ArbolProducto arbolProducto;

    public ProductoController(Home vista) {
        this.vista = vista;

        productoDao = new ProductosDAO();
        arbolProducto = new ArbolProducto();

        //cargamos a la estructura de datos :D
        for (Producto producto : productoDao.obtenerProductos()) {
            arbolProducto.insertar(producto);
        }

        vista.actualizarTablaDeProductos(arbolProducto);
    }

    public void enActualizarProducto(Producto producto) {
        if (producto.getId() == 0) {
            vista.mostrarMensaje("Selecciona una fila para editar");
            return;
        }

        if (producto.getCodigo().isEmpty() || producto.getNombre().isEmpty() || producto.getPrecio() == 0 || producto.getProveedor().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }

        boolean actualizado = productoDao.actualizarProducto(producto);

        if (actualizado) {
            boolean actualizadoEnLista = arbolProducto.actualizar(producto);

            if (actualizadoEnLista) {
                vista.mostrarMensaje("Datos del producto actualizados");
                vista.actualizarTablaDeProductos(arbolProducto);
                vista.actualizarImputsEnProductos(null);
            } else {
                vista.mostrarMensaje("Producto no encontrado en la lista enlazada.");
            }
        } else {
            vista.mostrarMensaje("Hubo un error al actualizar el producto en la base de datos.");
        }
    }

    public void enEliminarProducto(String codigo) {
        if (codigo.isEmpty()) {
            vista.mostrarMensaje("Selecciona una fila para eliminar (usa el buscador)");
            return;
        }

        if (productoDao.buscarProductoPorCodigo(codigo).getId() == 0) {
            vista.mostrarMensaje("El Producto no existe en BD");
            return;
        }
        boolean eliminadoEnBD = productoDao.eliminarProductoPorCodigo(codigo);

        if (!eliminadoEnBD) {
            vista.mostrarMensaje("Algo salio mal, no se pudo eliminar en BD");
            return;
        }
        boolean eliminadoEnABB = arbolProducto.eliminar(codigo);

        if (eliminadoEnABB) {
            vista.mostrarMensaje("Producto eliminado exitosamente");
            vista.actualizarTablaDeProductos(arbolProducto);
            vista.actualizarImputsEnProductos(null);
        } else {
            vista.mostrarMensaje("Algo salio mal!");
        }
    }

    public void enAgregarProducto(Producto producto) {
        if (producto.getCodigo().isEmpty() || producto.getNombre().isEmpty() || producto.getPrecio() == 0 || producto.getProveedor().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }
        int id = productoDao.registrarProducto(producto);

        boolean registrado = id != -1;

        if (registrado) {
            producto.setId(id);
            arbolProducto.insertar(producto);
            vista.mostrarMensaje("Producto registrado exitosamente");
            vista.actualizarTablaDeProductos(arbolProducto);
            vista.actualizarImputsEnProductos(null);
        } else {
            vista.mostrarMensaje("Hubo un error al registrar el producto en la base de datos.");
        }

    }

    public void enBuscarProducto(String codigo) {
        if (codigo.isEmpty()) {
            vista.mostrarMensaje("Ingrese Codigo para buscarr");
            return;
        }

        Producto producto = arbolProducto.buscar(codigo);

        if (producto != null) {
            vista.actualizarTablaDeProductos(arbolProducto);
            vista.actualizarImputsEnProductos(producto);
        } else {
            vista.mostrarMensaje("Producto  no encontrado");
        }
    }

    public List<Proveedor> obtenerOpcionesDeProveedores() {
        return productoDao.obtenerProveedorIdYNombre();
    }

    public void enOrdenamientoCambio() {
        vista.actualizarTablaDeProductos(arbolProducto);
    }

}
