/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EstructuraDeDatos.Colas;

import modelo.Boleta;

/**
 *
 * @author lee_j
 */
public class NodoBoleta {

    private Boleta dato;
    private NodoBoleta sig;

    public Boleta getDato() {
        return dato;
    }

    public void setDato(Boleta dato) {
        this.dato = dato;
    }

    public NodoBoleta getSig() {
        return sig;
    }

    public void setSig(NodoBoleta sig) {
        this.sig = sig;
    }


    public NodoBoleta(Boleta dato) {
        this.dato = dato;
        this.sig = null;
    }
}
