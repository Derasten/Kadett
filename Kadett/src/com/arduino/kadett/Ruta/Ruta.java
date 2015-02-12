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

    public Ruta() {
            puntos = new ArrayList<LatLng>();
            segmentos = new ArrayList<Segmento>();
    }
    
    public void addPunto(final LatLng p){
    	puntos.add(p);
    }
    
    public void addPuntos(final ArrayList<LatLng> puntos){
    	this.puntos.addAll(puntos);
    }
    
    public ArrayList<LatLng> getPuntos(){
    	return puntos;
    }

    public void addSegment(final Segmento s) {
            segmentos.add(s);
    }

    public List<Segmento> getSegmentos() {
            return segmentos;
    }

    /**
     * @param nombre El nombre a poner
     */
    public void setNombre(final String nombre) {
            this.nombre = nombre;
    }

    /**
     * @return Recupera el nombre
     */
    public String getNombre() {
            return nombre;
    }

    /**
     * @param copyright El copyright a fijar
     */
    public void setCopyright(String copyright) {
            this.copyright = copyright;
    }

    /**
     * @return El copyright
     */
    public String getCopyright() {
            return copyright;
    }

    /**
     * @param advertencia La advertencia a fijar
     */
    public void setAdvertencia(String advertencia) {
            this.advertencia = advertencia;
    }

    /**
     * @return La advertencia
     */
    public String getAdvertencia() {
            return advertencia;
    }

    /**
     * @param pais El país a fijar
     */
    public void setPais(String pais) {
            this.pais = pais;
    }

    /**
     * @return El pais
     */
    public String getPais() {
            return pais;
    }

    /**
     * @param longitud La longitud a fijar
     */
    public void setLongitud(int longitud) {
            this.longitud = longitud;
    }

    /**
     * @return La longitud
     */
    public int getLongitud() {
            return longitud;
    }


    /**
     * @param polyline La polyline a fijar
     */
    public void setPolyline(String polyline) {
            this.polyline = polyline;
    }

    /**
     * @return La polyline
     */
    public String getPolyline() {
            return polyline;
    }
}