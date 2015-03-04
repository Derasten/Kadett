package com.arduino.kadett.Ruta;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Ruta {
	private String nombre;
    //private final List<GeoPoint> points;
    private final ArrayList<LatLng> puntos;
    private List<Segmento> segmentos;
    private String copyright;
    private String advertencia;
    private String pais;
    private int longitud;
    private String polyline;

    /**
     * Constructor
     * */
    public Ruta() {
            puntos = new ArrayList<LatLng>();
            segmentos = new ArrayList<Segmento>();
    }
    /**
     * Añade punto a la ruta
     * @param p (LatLng) punto a añadir
     * */
    public void addPunto(final LatLng p){
    	puntos.add(p);
    }
    
    /**
     * Añade varios puntos a la ruta
     * @param puntos (ArrayList<LatLng>) Array con los puntos
     * */
    public void addPuntos(final ArrayList<LatLng> puntos){
    	this.puntos.addAll(puntos);
    }
    
    /**
     * Coge los puntos de la ruta completos
     * @return puntos (ArrayList<LatLng>) Todos los puntos de la ruta.
     * */
    public ArrayList<LatLng> getPuntos(){
    	return puntos;
    }

    
    /**
     * Añade un segmento a la ruta
     * @param s (Segmento) El segmento a añadir
     * */
    public void addSegment(final Segmento s) {
            segmentos.add(s);
    }

    /**
     * Coge todos los segmentos de la ruta
     * @return segmentos (List<Segmento>)
     * */
    public List<Segmento> getSegmentos() {
            return segmentos;
    }

    /**
     * Fija el nombre de la ruta
     * @param nombre (String) El nombre a poner
     */
    public void setNombre(final String nombre) {
            this.nombre = nombre;
    }

    /**
     * Coge el nombre de la ruta
     * @return nombre (String) Recupera el nombre
     */
    public String getNombre() {
            return nombre;
    }

    /**
     * Fija el copyright a la ruta
     * @param copyright (String) El copyright a fijar
     */
    public void setCopyright(String copyright) {
            this.copyright = copyright;
    }

    /**
     * Coge el copyright
     * @return copyright (String)
     */
    public String getCopyright() {
            return copyright;
    }

    /**
     * Fija la advertencia a la ruta
     * @param advertencia (String) La advertencia a fijar
     */
    public void setAdvertencia(String advertencia) {
            this.advertencia = advertencia;
    }

    /**
     * Coge la advertencia de la ruta
     * @return advertencia (String)
     */
    public String getAdvertencia() {
            return advertencia;
    }

    /**
     * Fija el país de la ruta
     * @param pais (String) El país a fijar
     */
    public void setPais(String pais) {
            this.pais = pais;
    }

    /**
     * Coge el país de la ruta
     * @return pais (String)
     */
    public String getPais() {
            return pais;
    }

    /**
     * Fija la longitud de la ruta
     * @param longitud (int) La longitud a fijar
     */
    public void setLongitud(int longitud) {
            this.longitud = longitud;
    }

    /**
     * Coge la longitud de la ruta
     * @return longitud (int)
     */
    public int getLongitud() {
            return longitud;
    }


    /**
     * Fija la polylínea en la ruta
     * @param polyline (String) La polyline a fijar
     */
    public void setPolyline(String polyline) {
            this.polyline = polyline;
    }

    /**
     * Coge la polylínea de la ruta
     * @return polyline (String)
     */
    public String getPolyline() {
            return polyline;
    }
    
}