package modelo;

import util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductosDAO {

    Connection con;
    ConexionDB cn = ConexionDB.getInstancia();
    PreparedStatement ps;
    ResultSet rs;

    public int registrarProducto(Producto pro) {
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio) VALUES (?,?,?,?,?)";
        int idGenerado = -1;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("boleta registrado exitosamente: ");
                ResultSet llavegenerada = ps.getGeneratedKeys();
                if (llavegenerada.next()) {
                    idGenerado = llavegenerada.getInt(1);
                }
            } else {
                System.out.println("Error al registrar producto.");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar producto: " + e.getMessage());
        }
        return idGenerado;
    }

    public List<Proveedor> obtenerProveedorIdYNombre() {
        String sql = "SELECT nombre, id FROM proveedor";
        ArrayList<Proveedor> proveedores = new ArrayList();
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor prov = new Proveedor();
                prov.setId(rs.getInt("id"));
                prov.setNombre(rs.getString("nombre"));
                proveedores.add(prov);
            }
        } catch (SQLException e) {
            System.out.println("Proveedor option error:" + e.toString());
        }
        return proveedores;
    }

    public List<Producto> obtenerProductos() {
        List<Producto> ListaPro = new ArrayList();
        String sql = "SELECT * FROM productos";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto pro = new Producto();
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getString("proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setPrecio(rs.getDouble("precio"));
                ListaPro.add(pro);
            }
        } catch (SQLException e) {
            System.out.println("db obtener error: " + e.toString());
        }
        return ListaPro;
    }

    public boolean eliminarProductoPorId(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("db eliminar id error: " + e.toString());
            return false;
        }
    }

    public boolean eliminarProductoPorCodigo(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, codigo);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("db eliminar codigo error: " + e.toString());
            return false;
        }

    }

    public boolean actualizarProducto(Producto pro) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, stock=?, precio=? WHERE id=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.setInt(6, pro.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("db actualizar error: " + e.toString());
            return false;
        }
    }

    public Producto buscarProductoPorCodigo(String cod) {
        Producto producto = new Producto();
        String sql = "SELECT * FROM productos WHERE codigo=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cod);
            rs = ps.executeQuery();
            if (rs.next()) {
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println("db buscar codigo error: " + e.toString());
        }
        return producto;
    }

}
