package com.arduino.kadett;

import com.arduino.kadett.Ruta.*;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
//import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Se encarga de mostrar el mapa y las localizaciones del coche y del usuario.
 * 
 * */
public class Mapa extends Activity implements LocationListener{

	private LocationManager locationManager;
	private GoogleMap Mapa;
	private String proveedor;
	private double latitudCoche  = 0;
	private double longitudCoche = 0;
	private double latitudMovil  = 0;
	private double longitudMovil = 0;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // Activamos ActionBar y ponemos que pueda volver a la pantalla principal.
     	// Hay que configurar a donde va cuando se pulsa el bot�n en la funci�n onOptionsItemSelected()
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // Preparamos la base de datos para leer.
        ControladorBaseDatos midb = new ControladorBaseDatos(MainActivity.appContext,"GPS.db",null);
        SQLiteDatabase bd = midb.getReadableDatabase();
        if(bd != null){
        	Cursor c = bd.rawQuery("SELECT * FROM "+ControladorBaseDatos.NOMBRE_TABLA+" ORDER BY _ID DESC LIMIT 1",null);
        	if (c.moveToFirst()) {
        		do{	
        			latitudCoche= c.getDouble(c.getColumnIndex("LATITUD"));
        			longitudCoche= c.getDouble(c.getColumnIndex("LONGITUD"));
        		}while(c.moveToNext());
        	}
        	
        	//Cerramos la base de datos
        	bd.close();
        	
        	
        }
        
        // Comprobamos si Google Play est� disponible
        int estado = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
 
        // Mostramos el estado si no est� disponible
        if(estado!=ConnectionResult.SUCCESS){
           
        	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(estado, this, 10);
            dialog.show();
 
        }else { 
 
        	// Fijamos el SupportMapFragment a la vista que est� en Layout-> activity_mapa.xml
            MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
 
            // Cogemos el mapa del MapFragment
            Mapa = mMapFragment.getMap();
            
            // Establecemos la capa de localizacion actual
            Mapa.setMyLocationEnabled(true);
            
            // Cogemos LocationManager del servicio del sistema LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creamos un criterio, que utilizaremos adelante para seleccionar el mejor proveedor de localizacion
            Criteria criterio = new Criteria();
 
            // Cogemos el nombre del mejor proveedor
            proveedor= locationManager.getBestProvider(criterio, true);
 
            // Cogemos la �ltima localizaci�n conocida
            Location localizacion = locationManager.getLastKnownLocation(proveedor);
            Toast.makeText(this, "Espere unos segundos para que la ruta se calcule correctamente.", Toast.LENGTH_LONG).show();
            if(localizacion!=null){
                onLocationChanged(localizacion);  
            }
           locationManager.requestLocationUpdates(proveedor, 20000, 0, (android.location.LocationListener) this);
             
        }
    }
	
	/**
	 * Cuando la localizaci�n cambia se recalcula la ruta
	 * 
	 * @param localizacion (location) La nueva localizaci�n
	 * */
    @Override
    public void onLocationChanged(Location localizacion) {
    	
    	Mapa.clear();
        // Cogemos la latitud
        latitudMovil = localizacion.getLatitude();
        
        // Cogemos la longitud
        longitudMovil = localizacion.getLongitude();
 
        // Creamos un objeto LatLng con la localizacion actual
        LatLng lugar = new LatLng(latitudMovil, longitudMovil);
 
        // Mostramos la localizacion en el Mapa
        Mapa.moveCamera(CameraUpdateFactory.newLatLng(lugar));
 
        // Hacemos zoom en el Mapa
        Mapa.animateCamera(CameraUpdateFactory.zoomTo(15));
        
        Mapa.addMarker(new MarkerOptions().position(new LatLng(latitudCoche, longitudCoche)).title("Posicion Coche"));
 
       	// Calculamos la ruta de nuevo para dibujarla en una tarea as�ncrona
       	new CalculaRuta().execute(latitudMovil,longitudMovil);
        
        
        
        
 
    }
    
    /**
     * Calcula la ruta entre el dispositivo y el autom�vil de forma as�ncrona para despu�s mostrarlo en el mapa.
     * */
    private class CalculaRuta extends AsyncTask<Double, Void, Ruta> {

    	
    	/**
    	 * Ejecuta la funci�n en 'Background', calculando la ruta
    	 * @param param (Double...) Dos par�metros,el primero con la latitud y el segundo con la longitud.
    	 * @return ruta (Ruta)
    	 * @throws Exception Hay que mantenerlo controlado.
    	 * */
        @Override
        protected Ruta  doInBackground(Double... param) {
           Ruta ruta=null;
          
          try {
              ruta = direcciones(new LatLng(param[0],param[1]),new LatLng(latitudCoche,longitudCoche));
          } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          
              return ruta ;
        }      

        /**
         * Una vez ejecutada la clase, se dibuja la ruta en el mapa.
         * @param ruta (Ruta) La ruta a dibujar
         * */
        @Override
        protected void onPostExecute(Ruta ruta) {    
            // Dibujamos la ruta       	
        		PolylineOptions poli = new PolylineOptions()
        			.addAll(ruta.getPuntos())
        			.width(7)
        			.color(0xFFD50000);
        	
        		Mapa.addPolyline(poli);
        }

  }
    
    /**
     * Hace la petici�n a Google para obtener la ruta entre los dos puntos.
     * @param inicio (LatLng) La posici�n del dispositivo
     * @param destion (LatLng) La posici�n del autom�vil
     * @return r (Ruta) La ruta a dibujar
     * */
    private Ruta direcciones(LatLng inicio,LatLng destino){
    	Parser parser;
        //https://developers.google.com/maps/documentation/directions/#JSON <- get api
        String jsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
        final StringBuffer sBuf = new StringBuffer(jsonURL);
        sBuf.append("origin=");
        sBuf.append(inicio.latitude);
        sBuf.append(',');
        sBuf.append(inicio.longitude);
        sBuf.append("&destination=");
        sBuf.append(destino.latitude);
        sBuf.append(',');
        sBuf.append(destino.longitude);
        sBuf.append("&sensor=true&mode=driving");
        parser = new GoogleParser(sBuf.toString());
        Ruta r =  parser.parse();
        return r;
    }
    
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
     
    /**
     * Modificamos el bot�n del men� para cerrar bien la actividad.
     * @param item (MenuItem) 
     * @return x Boolean
     * */
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case android.R.id.home:
	    	Log.i("Mapa","Cerrando la actividad");
	    	Mapa.clear();
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
