package Modelo;

import baseDeDatos.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    ConexionDB cn = ConexionDB.getInstancia();

    public Sesion login(String correo, String contra) {
        Sesion l = new Sesion();
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contra = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, contra);
            rs = ps.executeQuery();

            if (rs.next()) {
                l.setId(rs.getInt("id"));
                l.setNombre(rs.getString("nombre"));
                l.setCorreo(rs.getString("correo"));
                l.setContra(rs.getString("contra"));
                l.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return l;
    }

    public boolean registrar(Sesion reg) {
        String sql = "INSERT INTO usuarios (nombre, correo, contra, rol) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, reg.getNombre());
            ps.setString(2, reg.getCorreo());
            ps.setString(3, reg.getContra());
            ps.setString(4, reg.getRol());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
}
