package modelo;

import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    Connection con;
    ConexionDB cn = ConexionDB.getInstancia();
    PreparedStatement ps;
    ResultSet rs;
    int r;

    public int IdVenta() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }

    public int registrarVenta(Venta v) {
        String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getFecha());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {

        }
        return r;
    }

    public int registraDetalle(Detalle dv) {
        String sql = "INSERT INTO detalle (cod_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, dv.getCodigoProducto());
            ps.setInt(2, dv.getCantidad());
            ps.setDouble(3, dv.getPrecio());
            ps.setInt(4, dv.getId());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {

        }
        return r;
    }

    public boolean actualizarStock(int cant, String cod) {
        String sql = "UPDATE productos SET stock=? WHERE codigo=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cant);
            ps.setString(2, cod);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public List<Venta> obtenerVentas() {
        List<Venta> ListaVenta = new ArrayList();
        String sql = "SELECT * FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Venta vent = new Venta();
                vent.setId(rs.getInt("id"));
                vent.setCliente(rs.getString("cliente"));
                vent.setVendedor(rs.getString("vendedor"));
                vent.setTotal(rs.getDouble("total"));
                vent.setFecha(rs.getString("fecha"));
                ListaVenta.add(vent);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaVenta;
    }

    public Venta buscarPorId(int id) {
        Venta vent = new Venta();
        String sql = "SELECT * FROM ventas WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                vent.setId(rs.getInt("id"));
                vent.setCliente(rs.getString("cliente"));
                vent.setVendedor(rs.getString("vendedor"));
                vent.setTotal(rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return vent;
    }

    public void eliminarPorId(int id) {
        try {
            String sql = "DELETE FROM ventas where id=?";
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("venta eliminado exitosamente: ");
            } else {
                System.out.println("venta : error al eliminado el detalle.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar voleta por id de: " + e.getMessage());
        }
    }

    public void eliminarDetallePorIdVenta(int idVenta) {
        try {
            String sqlboleta = "DELETE FROM detalle where id_vent=?";
            con = cn.getConnection();
            ps = con.prepareStatement(sqlboleta);
            ps.setInt(1, idVenta);
            int rowsAffected = ps.executeUpdate();
            // Confirmación en consola
            if (rowsAffected > 0) {
                System.out.println("detalle por boletaid eliminado exitosamente: ");
            } else {
                System.out.println("detalle por boleta id: error al eliminado el detalle.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar detalle por id de: " + e.getMessage());
        }
    }
}
