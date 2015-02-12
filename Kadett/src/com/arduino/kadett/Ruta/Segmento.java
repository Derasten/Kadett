package com.arduino.kadett.Ruta;

import com.google.android.gms.maps.model.LatLng;

public class Segmento {
    /* Punto en el segmento*/
    private LatLng inicio;
    /* Instruccion para llegar al siguiente segmento*/
    private String instruccion;
    /* Longitud del segmento*/
    private int longitud;
    /* Distancia cubierta*/
    private double distancia;

    /**
     * Crea un Segmento vacio.
     */

    public Segmento() {
    }


    /**
     * Fija la instruccion.
     * @param a instruccion.
     */

    public void setInstruccion(final String a) {
            this.instruccion = a;
    }

    /**
     * Devuelve la instruccion.
     * @return String de la instruccion.
     */

    public String getInstruccion() {
            return instruccion;
    }

    /**
     * Añade punto al Segmento.
     * @param punto LatLng que se añadirá.
     */

    public void setPunto(final LatLng punto) {
            inicio = punto;
    }

    /** 
     * Recupera el punto inicial del segmento.
     * @return un LatLng
     */

    public LatLng puntoInicial() {
            return inicio;
    }

    /** 
     * Crea un segmento copia.
     * @return un Segmento que es una copia de éste.
     */

    public Segmento copia() {
            final Segmento copia = new Segmento();
            copia.inicio = inicio;
            copia.instruccion = instruccion;
            copia.longitud = longitud;
            copia.distancia = distancia;
            return copia;
    }

    /**
     * Fija la longitud
     * @param longitud la longitud a fijar
     */
    public void setLongitud(final int longitud) {
            this.longitud = longitud;
    }

    /**
     * Devuelve la longitud
     * @return la longitud
     */
    public int getLongitud() {
            return longitud;
    }

    /**
     * Fija la distancia
     * @param distancia la distancia a fijar
     */
    public void setDistancia(double distancia) {
            this.distancia = distancia;
    }

    /**
     * Devuelve la distancia
     * @return the distance
     */
    public double getDistancia() {
            return distancia;
    }
}
