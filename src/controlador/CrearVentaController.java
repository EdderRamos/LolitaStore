/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import EstructuraDeDatos.ABB.ArbolProducto;
import EstructuraDeDatos.Arrays.CarritoDeCompras;
import EstructuraDeDatos.Arrays.CarritoItem;
import EstructuraDeDatos.Colas.ListaColaBoleta;
import modelo.Boleta;
import modelo.Cliente;
import modelo.ClienteDAO;
import modelo.Detalle;
import modelo.Producto;
import modelo.ProductosDAO;
import modelo.Venta;
import modelo.VentaDAO;
import util.BoletaPDF;
import vista.Home;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author lee_j
 */
public class CrearVentaController {

    private Home vista;

    private ProductosDAO productosDao;
    private VentaDAO ventasDao;
    private ClienteDAO clienteDao;

    private ArbolProducto arbolProductos;
    private CarritoDeCompras carrito;
    private ListaColaBoleta colaBoletas; 

    public CrearVentaController(Home vista) {
        this.vista = vista;
        this.colaBoletas = new ListaColaBoleta();

        productosDao = new ProductosDAO();
        ventasDao = new VentaDAO();
        clienteDao = new ClienteDAO();

        carrito = new CarritoDeCompras();
        arbolProductos = new ArbolProducto();

        for (Producto p : productosDao.obtenerProductos()) {
            arbolProductos.insertar(p);
        }
        enActualizarVentasRealizadas();
    }

    public void enAgregarAlCarrito(String codigoProducto, String cantidad) {
        int cantidadProducto = Integer.parseInt(cantidad);

        Producto productoSeleccionado = arbolProductos.buscar(codigoProducto);

        if (cantidadProducto > productoSeleccionado.getStock()) {
            vista.mostrarMensaje("No hay stock");
            return;
        }

        carrito.agregarProducto(productoSeleccionado, cantidadProducto);

        vista.actualizarCarritodeVenta(carrito);
        vista.actualizarProductoSeleccionadoEnVenta(null);

    }

    public void refreshListaDeProductos() {
        arbolProductos = new ArbolProducto();
        for (Producto p : productosDao.obtenerProductos()) {
            arbolProductos.insertar(p);
        }
    }

    public void enEliminarSeleccionado(int index) {
        carrito.eliminarPorIndice(index);
        vista.actualizarCarritodeVenta(carrito);
    }

    public void enBuscarProducto(String codigoProducto) {
        try {
            Producto producto = arbolProductos.buscar(codigoProducto);

            if (producto == null) {
                vista.mostrarMensaje("No se encontro el producto");
                return;
            }
            if (producto.getStock() == 0) {
                vista.mostrarMensaje("No hay stock disponible para este producto.");
                return;
            }

            vista.actualizarProductoSeleccionadoEnVenta(producto);

        } catch (Exception e) {
            vista.mostrarMensaje("Algo salio mal, intentalo otra vez");
        }

    }

    public void enBuscarCliente(String codigoCliente) {
        try {
            int dniORuc = Integer.parseInt(codigoCliente);
            Cliente cliente = clienteDao.buscarClientePorDniORuc(dniORuc);

            if (cliente == null) {
                vista.mostrarMensaje("No se encontro el cliente");
                return;
            }

            vista.actualizarClienteSeleccionadoEnVenta(cliente);

        } catch (Exception e) {
            vista.mostrarMensaje("Algo salio mal, intentalo otra vez");
        }
    }

    public void enGenerarVenta(String dniORucCliente, String vendedor) {
        if (carrito.getItems().isEmpty()) {
            vista.mostrarMensaje("No se ingresaron productos para la venta");
            return;
        }

        if (dniORucCliente.isEmpty()) {
            vista.mostrarMensaje("Debes ingresar un cliente");
            return;
        }
        Cliente cliente = clienteDao.buscarClientePorDniORuc(Integer.parseInt(dniORucCliente));

        if (cliente == null) {
            vista.mostrarMensaje("Cliente no encontrado");
            return;
        }

        //registrar venta
        Venta v = new Venta();
        v.setCliente(dniORucCliente);
        v.setVendedor(vendedor);
        v.setTotal(carrito.calcularTotal());
        v.setFecha(LocalDate.now().toString());
        ventasDao.registrarVenta(v);

        //registrar detalle
        ArrayList<Detalle> detalles = new ArrayList();
        int id = ventasDao.IdVenta();
        v.setId(id);
        for (CarritoItem item : carrito.getItems()) {
            Detalle dv = new Detalle();

            dv.setCodigoProducto(item.getProducto().getCodigo());
            dv.setCantidad(item.getCantidad());
            dv.setPrecio(item.calcularTotal());
            dv.setId(id);
            detalles.add(dv);
            ventasDao.registraDetalle(dv);
            ventasDao.actualizarStock(item.getResultadoStock(), item.getProducto().getCodigo());
        }

        vista.reiniciarRealizarCompra();
        carrito.limpiar();

        //agregamos a la cola la generacion de PDF 
        Boleta boleta = new Boleta();
        boleta.setCliente(cliente);
        boleta.setDetalles(detalles);
        boleta.setVenta(v);
        colaBoletas.agregar(boleta);
        
        enActualizarVentasRealizadas();
    }

    public void enActualizarVentasRealizadas() {
        int mes = ventasDao.obtenerVentasDelMes().size();
        int semana = ventasDao.obtenerVentasDeLaSemana().size();
        int hoy = ventasDao.obtenerVentasDeHoy().size();
        vista.actualizarVentasRealizadasSuperior(mes, semana, hoy);

    }

}
