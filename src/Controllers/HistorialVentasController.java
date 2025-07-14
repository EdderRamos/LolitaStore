/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import EstructuraDeDatos.Pilas.ListaPilaVentas;
import Modelo.Venta;
import Modelo.VentaDAO;
import Vista.Home;

/**
 *
 * @author lee_j
 */
public class HistorialVentasController {

    private Home vista;
    private VentaDAO ventaDao;

    private ListaPilaVentas lpVentas;

    public HistorialVentasController(Home vista) {
        this.vista = vista;

        ventaDao = new VentaDAO();

        lpVentas = new ListaPilaVentas();
        for (Venta venta : ventaDao.obtenerVentas()) {
            lpVentas.agregar(venta);
        }
        vista.actualizarTablaDeVentas(lpVentas);
    }

    public void enEliminarUltimaVenta() {
        Venta actual = lpVentas.actual().getDato();
        Venta vent = ventaDao.buscarPorId(actual.getId());

        if (vent == null) {
            vista.mostrarMensaje("La venta no existe en DB");
            return;
        }
        ventaDao.eliminarDetallePorIdVenta(actual.getId());
        ventaDao.eliminarPorId(actual.getId());
        lpVentas.eliminar();

        vista.actualizarTablaDeVentas(lpVentas);

    }

}
