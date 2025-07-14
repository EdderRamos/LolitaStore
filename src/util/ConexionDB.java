package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDB {

    //library 
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    // database credentials
    private static final String URL = "jdbc:mysql://localhost:3307/tiendalolitabd?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection con = null;

    private static ConexionDB instancia = null;

    //THIS CLASS USES THE SINGLETON PATTERN  TO MANIPULATE DATA IN A MYSQL DATABASE  
    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConnection() {
        return con;
    }

    public ConexionDB() {
        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("conexion exitosa");
        } catch (Exception e) {
            System.out.println("conexion fallida:  " + e);
        }
    }

    public static void cerrarConexion() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Conexión cerrada");
            } catch (Exception e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

}
