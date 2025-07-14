/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.ABB;

/**
 *
 * @author lee_j
 */
public enum OrdenamientoABB {
    PREORDEN("Preorden"),
    INORDEN("Inorden"),
    POSTORDEN("PostOrden");
    private final String nombre;

    OrdenamientoABB(String nombre) {
        this.nombre = nombre;

    }

    public String getNombre() {
        return nombre;
    }

    public static OrdenamientoABB[] ordenamientos = {PREORDEN, INORDEN, POSTORDEN};

}
