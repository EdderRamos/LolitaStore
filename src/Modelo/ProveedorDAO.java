package Modelo;

import ConexionBaseDeDatos.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    Connection con;
    ConexionDB cn = ConexionDB.getInstancia();
    PreparedStatement ps;
    ResultSet rs;

    public int agregarProveedor(Proveedor pr) {
        String sql = "INSERT INTO proveedor (ruc, nombre, telefono, direccion) VALUES (?,?,?,?)";
        int idGenerado = -1;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("boleta registrado exitosamente: ");
                ResultSet llavegenerada = ps.getGeneratedKeys();
                if (llavegenerada.next()) {
                    idGenerado = llavegenerada.getInt(1);
                }
            } else {
                System.out.println("Error al registrar proveedor.");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar proveedor: " + e.getMessage());
        }
        return idGenerado;
    }

    public List<Proveedor> obtenerProveedores() {
        List<Proveedor> ListaPr = new ArrayList();
        String sql = "SELECT * FROM proveedor";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Proveedor pr = new Proveedor();
                pr.setId(rs.getInt("id"));
                pr.setRuc(rs.getInt("ruc"));
                pr.setNombre(rs.getString("nombre"));
                pr.setTelefono(rs.getInt("telefono"));
                pr.setDireccion(rs.getString("direccion"));
                ListaPr.add(pr);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaPr;
    }

    public boolean eliminarProveedorPorId(int id) {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Proveedor eliminado exitosamente: " + id);
                return true;
            } else {
                System.out.println("Error al eliminado el proveedor.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error al consultarid del proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarProveedor(Proveedor pr) {
        String sql = "UPDATE proveedor SET ruc=?, nombre=?, telefono=?, direccion=? WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setInt(5, pr.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("db actualizar error: " + e.toString());
            return false;
        }
    }

    public Proveedor buscarProveedorPorId(int id) {
        Proveedor pr = new Proveedor();
        String sql = "SELECT * FROM proveedor WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                pr.setId(rs.getInt("id"));
                pr.setNombre(rs.getString("nombre"));
                pr.setTelefono(rs.getInt("telefono"));
                pr.setDireccion(rs.getString("direccion"));
                pr.setRuc(rs.getInt("ruc"));

            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return pr;
    }
}
