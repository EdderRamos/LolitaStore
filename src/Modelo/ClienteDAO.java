package Modelo;

import EstructuraDeDatos.ListaEnlazada.ListaClientes;
import ConexionBaseDeDatos.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    ConexionDB cn = ConexionDB.getInstancia();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Lista enlazada de clientes (accesible desde otras clases)
    public static ListaClientes listaEnlazada = new ListaClientes();

    // REGISTRAR CLIENTE EN LA BASE DE DATOS
    public int registrarCliente(Cliente cl) {
        String sql = "INSERT INTO clientes (dni, nombre, telefono, direccion) VALUES (?,?,?,?)";
        int idGenerado = -1;
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("boleta registrado exitosamente: ");
                ResultSet llavegenerada = ps.getGeneratedKeys();
                if (llavegenerada.next()) {
                    idGenerado = llavegenerada.getInt(1);
                }
            } else {
                System.out.println("Error al registrar cliente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
        }
        return idGenerado;
    }
    // LISTAR CLIENTES y llenar la lista enlazada

    public List<Cliente> obternerClientes() {
        List<Cliente> ListaCl = new ArrayList();
        listaEnlazada.reiniciar(); // Reinicia lista para evitar duplicados
        String sql = "SELECT * FROM clientes";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cl = new Cliente();
                cl.setId(rs.getInt("id"));
                cl.setDni(rs.getInt("dni"));
                cl.setNombre(rs.getString("nombre"));
                cl.setTelefono(rs.getInt("telefono"));
                cl.setDireccion(rs.getString("direccion"));
                ListaCl.add(cl);
                listaEnlazada.agregar(cl);     // Para lista enlazada
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaCl;
    }

    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente eliminado exitosamente: " + id);
                return true;
            } else {
                System.out.println("Error al eliminado el cliente.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error al consultarid del cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarCliente(Cliente cl) {
        String sql = "UPDATE clientes SET dni=?, nombre=?, telefono=?, direccion=? WHERE id=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setInt(5, cl.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public Cliente buscarClientePorDniORuc(int dni) {
        Cliente cl = new Cliente();
        String sql = "SELECT * FROM clientes WHERE dni=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setNombre(rs.getString("nombre"));
                cl.setTelefono(rs.getInt("telefono"));
                cl.setDireccion(rs.getString("direccion"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }

    public Cliente buscarClientePorId(int id) {
        Cliente cl = new Cliente();
        String sql = "SELECT * FROM clientes WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setId(rs.getInt("id"));
                cl.setNombre(rs.getString("nombre"));
                cl.setTelefono(rs.getInt("telefono"));
                cl.setDireccion(rs.getString("direccion"));
                cl.setDni(rs.getInt("dni"));

            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }
}
