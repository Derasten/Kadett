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
     * Constructor
     */
    public Segmento() {
    }


    /**
     * Fija la instruccion en el segmento.
     * @param a (String) Instrucción a fijar
     */

    public void setInstruccion(final String a) {
            this.instruccion = a;
    }

    /**
     * Devuelve la instruccion.
     * @return instruccion (String) del segmento.
     */

    public String getInstruccion() {
            return instruccion;
    }

    /**
     * Añade punto al Segmento.
     * @param punto (LatLng) que se añadirá.
     */

    public void setPunto(final LatLng punto) {
            inicio = punto;
    }

    /** 
     * Recupera el punto inicial del segmento.
     * @return inicio (LatLng) punto inicial del segmento
     */

    public LatLng puntoInicial() {
            return inicio;
    }

    /** 
     * Crea un segmento copia.
     * @return copia (Segmento) copia de este segmento.
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
     * @param longitud (int) a fijar
     */
    public void setLongitud(final int longitud) {
            this.longitud = longitud;
    }

    /**
     * Devuelve la longitud
     * @return longitud (int)
     */
    public int getLongitud() {
            return longitud;
    }

    /**
     * Fija la distancia
     * @param distancia (double) la distancia a fijar.
     */
    public void setDistancia(double distancia) {
            this.distancia = distancia;
    }

    /**
     * Devuelve la distancia
     * @return distancia (double) la distancia
     */
    public double getDistancia() {
    		return distancia;
    }
    
}
