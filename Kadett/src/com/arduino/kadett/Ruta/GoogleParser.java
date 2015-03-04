package com.arduino.kadett.Ruta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GoogleParser extends XMLParser implements Parser{
	 /* Distancia cubierta */
    private int distancia;
    protected URL url;
    protected InputStream isUrl;

    /**
     * Se encarga de acceder a la url pasada por parámetro
     * @param feedUrl (String) url a acceder
     * */
    public GoogleParser(String feedUrl) {
    		//Ésta línea y XMLParser ya no serían necesarios
            super(feedUrl);
            try{
            	url= new URL(feedUrl);
            }catch(MalformedURLException e){
            	Log.e("GoogleParser()","MalformedURLException :" + e);
            }
            try{
            	isUrl = url.openStream();
            }catch(IOException e){
            	Log.e("GoogleParser()","IOException :" + e);
            }
    }

    /**
     * Analiza una url apuntando a un objeto Google JSON a un objeto Ruta.
     * @return  Ruta objeto basado en JSON.
     */

    public Ruta parse() {
    		//Log.i("GoogleParser()","parse()");
            // Convierte el stream a cadenas
            final String resultado = conversor(isUrl);//conversor(this.getInputStream)
            Log.i("Resultado",resultado);
            //Log.i("GoogleParser()",resultado);
            // Crea una ruta vacia
            final Ruta ruta = new Ruta();
            // Crea un segmento vacio
            final Segmento segmento = new Segmento();
            
            try {
                    // Transforma la cadena a un objeto json
                    final JSONObject json = new JSONObject(resultado);
                    // Coge el objeto ruta
                    final JSONObject rutaJson = json.getJSONArray("routes").getJSONObject(0);
                    // Coge la "leg", sólo se coge una porque no se adminten puntos de ruta
                    // Es decir, son partes de ruta, que se han separado, como este no es el caso, solo habrá uno.
                    final JSONObject leg = rutaJson.getJSONArray("legs").getJSONObject(0);
                    // Coge los pasos para este "leg"
                    final JSONArray pasos = leg.getJSONArray("steps");
                    // Numero de pasos a usar en este bucle
                    final int numeroPasos = pasos.length();
                    // Fija el nombre de la ruta utilizando el inicio y el final de las direcciones
                    ruta.setNombre(leg.getString("start_address") + " to " + leg.getString("end_address"));
                    // Coge el copyright de Google(tos)
                    ruta.setCopyright(rutaJson.getString("copyrights"));
                    // Coge la longitud total de la ruta.
                    ruta.setLongitud(leg.getJSONObject("distance").getInt("value"));
                    // Coge las advertencias(tos)
                    if (!rutaJson.getJSONArray("warnings").isNull(0)) {
                            ruta.setAdvertencia(rutaJson.getJSONArray("warnings").getString(0));
                    }
                    /* Bucle a través de los pasos, creando un segmento por cada uno y
                     * decodificando las polylines que vaya encontrando para añadir a la ruta.
                     */
                    for (int i = 0; i < numeroPasos; i++) {
                            // Coge un paso
                            final JSONObject paso = pasos.getJSONObject(i);
                            // Coge la posicion inicial para éste paso y lo fija al segmento
                            final JSONObject inicio = paso.getJSONObject("start_location");
                            final LatLng posicion = new LatLng(inicio.getDouble("lat"),inicio.getDouble("lng"));
                            segmento.setPunto(posicion);
                            // Fija la longitud del segmento
                            final int longitud = paso.getJSONObject("distance").getInt("value");
                            distancia += longitud;
                            segmento.setLongitud(longitud);
                            segmento.setDistancia(distancia/1000);
                            // Quita las instrucciones html de las direcciones de google y fija la instrucción
                            segmento.setInstruccion(paso.getString("html_instructions").replaceAll("<(.*?)*>", ""));
                            //Coge y decodifica el polyline de este segmento para añadirlo a la ruta.
                            ruta.addPuntos(decodificarPolyLinea(paso.getJSONObject("polyline").getString("points")));
                            // Añade una copia del segmento a la ruta.
                            ruta.addSegment(segmento.copia());
                    }
            } catch (JSONException e) {
                    Log.e(e.getMessage(), "GoogleParser - " + feedUrl);
            }
            return ruta;
    }

    /**
     * Convierte un InputStream a una cadena.
     * @param entrada Inputstream a convertir.
     * @return una cadena.
     */

    private static String conversor(final InputStream entrada) {
    	//Log.i("GoogleParser()","conversor()");
    	final BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
    	final StringBuilder constructor = new StringBuilder();

    	String linea = null;
    	try {
    		while ((linea = lector.readLine()) != null) {
    			constructor.append(linea);
    		}
    	} catch (IOException e) {
            Log.e("GoogleParser","Conversor() " + e.getMessage());
    	} finally {
    				try {
    					entrada.close();
    				} catch (IOException e) {
    						Log.e("GoogleParser","Conversor() " + e.getMessage());
    				}
    	}
    	return constructor.toString();
    }
    
    /**
     * Decodifica la polylinea en una array de LatLng.
     * @param poly (String) polyline codificada a decodificar.
     * @return el array de LatLng.
     */

    private ArrayList<LatLng> decodificarPolyLinea(final String poly) {
    		//En el siguiente enlace está la información de como decodificar una polylinea
    		//https://developers.google.com/maps/documentation/utilities/polylinealgorithm?csw=1
            int tam = poly.length();
            int index = 0;
            ArrayList<LatLng> decodificado = new ArrayList<LatLng>();
            int lat = 0;
            int lng = 0;
            
            while(index < tam){
            	
            	int b;
            	int shift = 0;
            	int resultado = 0;

            	do{
                    b = poly.charAt(index++) - 63;
                    resultado |= (b & 0x1f) << shift;
                    shift += 5;
            	}while(b >= 0x20);
            	
            	int dlat = ((resultado & 1) != 0 ? ~(resultado >> 1) : (resultado >> 1));
            	lat += dlat;

            	shift = 0;
            	resultado = 0;
            	
            	do{
                    b = poly.charAt(index++) - 63;
                    resultado |= (b & 0x1f) << shift;
                    shift += 5;
            	}while (b >= 0x20);
                
            	int dlng = ((resultado & 1) != 0 ? ~(resultado >> 1) : (resultado >> 1));
                lng += dlng;
                decodificado.add(new LatLng((double) (lat / 1E5), (double) (lng / 1E5)));
            }

            return decodificado;
    }
}
