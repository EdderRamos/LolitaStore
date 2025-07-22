/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author eramos
 */
public class CompanyData {

    public static String RUC = "20563529378";
    public static String NOMBRE = "TIENDA LOLITA";
    public static String TELEFONO = "970 575 243";
    public static String DIRECCION = "Se encuentra en Lima, en el distrito de San Juan de Lurigancho exactamente en Av. Fernando Wiesse 4267, San Juan de Lurigancho 15423";
    public static String RAZON = "El mejor minimarket del Peru : Tienda Lolita!";

    public static String toStringData() {
        StringBuilder data = new StringBuilder();
        return data.append("RUC: ").append(CompanyData.RUC).append("\n")
                .append("NOMBRE: ").append(CompanyData.NOMBRE).append("\n")
                .append("TELEFONO: ").append(CompanyData.TELEFONO).append("\n")
                .append("DIRECCIÓN: ").append(CompanyData.DIRECCION).append("\n")
                .append("RAZON SOCIAL: ").append(CompanyData.RAZON)
                .toString();
    }

}
