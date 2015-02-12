package com.arduino.kadett;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


public class enviarSMS {
	
	private Context miContext;
	private boolean mensajeEntregado = false;
	private String numeroArduino ="";
	private double norte	= 0;
    private double este	= 0;
    private boolean longitud = false;
    private boolean latitud  = false;
	
	public enviarSMS(String mensaje){  
		
		miContext = MainActivity.appContext;
		
		SharedPreferences preferencias = MainActivity.appContext.getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
	       
        numeroArduino = preferencias.getString("NumeroArduino", "false");
		
		IntentFilter filter = new IntentFilter("MENSAJE_RECIBIDO");
	 	filter.addAction("TelefonoPermiso");
	 	LocalBroadcastManager.getInstance(miContext).registerReceiver(recibirMensajes, filter); // Hay que desregistrarlo en onDestroy!!!!!
	 	enviar(numeroArduino, mensaje);
	 	
			    
    }
	
	private void enviar(String numeroTelefono, String mensaje){
		Log.i("enviarSMS.java","enviarSMS");
		String ENVIADO = "MENSAJE_ENVIADO";
        String ENTREGADO = "MENSAJE_ENTREGADO";
 
        PendingIntent intentoEnviar = PendingIntent.getBroadcast(miContext, 0, new Intent(ENVIADO), 0);
 
        PendingIntent intentoEntregar = PendingIntent.getBroadcast(miContext, 0, new Intent(ENTREGADO), 0);

        miContext.registerReceiver(confirmarMensajes, new IntentFilter(ENVIADO));
        miContext.registerReceiver(entregarMensajes,new IntentFilter(ENTREGADO));
       	 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numeroTelefono, null, mensaje, intentoEnviar, intentoEntregar);
	}
	
	private BroadcastReceiver confirmarMensajes = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	Log.i("enviarSMS.java","Broadcast confirmarMensajes");
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(miContext, "Mensaje enviado.\n Espere un momento.", 
                            Toast.LENGTH_SHORT).show();
                    //Esperamos 5 segundos para esperar confirmación entrega mensaje
    	        	new esperar().execute();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(miContext, "Fallo al enviar", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(miContext, "Sin servicio", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(miContext, "PDU vacia",//Protocol Description Unit 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(miContext, "No hay conexion de red", 
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private BroadcastReceiver entregarMensajes = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	Log.i("enviarSMS.java","Broadcast entregarMENSAJES");
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(miContext, "Mensaje entregado.\n Espere un momento.", 
                            Toast.LENGTH_SHORT).show();
                    //Ponemos el booleano a true
                    mensajeEntregado = true;
                    break;
                case SmsManager.STATUS_ON_ICC_UNREAD:
                	Toast.makeText(miContext, "STATUS_ON_ICC_UNREAD", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_UNSENT:
                	Toast.makeText(miContext, "STATUS_ON_ICC_UNSENT", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_SENT:
                	Toast.makeText(miContext, "STATUS_ON_ICC_SENT", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_FREE:
                	Toast.makeText(miContext, "STATUS_ON_ICC_FREE", 
                            Toast.LENGTH_SHORT).show();
                	break;
            }
        }
    };
    
	private BroadcastReceiver recibirMensajes = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();
	    	if(action.equals("MENSAJE_RECIBIDO")){
	    		Log.i("enviarSMS.java","Mensaje Recibido");
	    		String numeroRemitente = intent.getStringExtra("numero");
	    		String cuerpoMensaje = intent.getStringExtra("cuerpo");
	    		Log.i("Numero",numeroRemitente);
	    		Log.i("Cuerpo",cuerpoMensaje.trim());
            
	    		//Toast.makeText(getBaseContext(), numeroRemitente,Toast.LENGTH_SHORT).show();
	    		//Toast.makeText(getBaseContext(), cuerpoMensaje.trim(),Toast.LENGTH_SHORT).show();
	    		if(cuerpoMensaje.trim().equals("PosicionCorrupta")){
	    			Toast.makeText(miContext,"La posición recibida está corrupta",Toast.LENGTH_SHORT).show();
	    		}else{
	    			//Hay que comprobar que recibe una cadena gps.
	    			//convierteGPS(cuerpoMensaje);
	    			Intent intentona = new Intent(miContext, Mapa.class);
	    			// Por alguna razón volvía a crear una actividad mapa después de cerrar
	    			// Indicamos que la actividad no tendrá "Historial" y volverá a la actividad MainActivity
	    			intentona.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			intentona.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    			
	    			miContext.startActivity(intentona);
	    		}
	    	}
    		
    			
    		
	    }
	};
	
	/**
     *	convierteGPS(String cadena)
     *	Convierte las coordenadas GPS
     *	@param cadena String que se convertirá
     *
     **/
    public void convierteGPS(String cadena){
    	Log.i("enviarSMS.java","convierteGPS()");
    	Log.i("Cadena",cadena);
    	boolean siguiente=false;
    	String coordenadasNorte="";
    	String coordenadasEste="";
    	for(int i=0;i<cadena.length();i++){
    		if((char)cadena.charAt(i)=='Z'){
    			siguiente=true;
    			continue;
    		}
    		if((char)cadena.charAt(i)==' '){
    			continue;
    		}
    		if(siguiente){
    			if((char)cadena.charAt(i)=='E'){
    				continue;
    			}else{
    				coordenadasEste+=(char) cadena.charAt(i);
    			}
    		}else{
    			if((char)cadena.charAt(i)=='N'){
    				continue;
    			}else{
    				coordenadasNorte+=(char) cadena.charAt(i);
    			}
    		}
    	}
    	Log.i("Norte",coordenadasNorte);
    	Log.i("Este",coordenadasEste);
    	norte= Double.valueOf(coordenadasNorte);
    	este= Double.valueOf(coordenadasEste);
    	
    	if(norte>-90||norte<90){
			latitud =true;
		}else{
			latitud=false;
		}
		if(este>-180||este<180){
			longitud =true;
		}else{
			longitud=false;
		}
    	Log.i("norte: ",String.valueOf(norte));
    	Log.i("este: ",String.valueOf(este));
    	if(latitud && longitud){
    		latitud = false;
    		longitud = false;
    		Thread i = new GuardarGPS(norte,este);
    		i.start();
    	}
    }
	
    private class esperar extends AsyncTask<Void, Void, Void>{
    	@Override
        protected Void doInBackground(Void... nada) {
            try{
            	Thread.sleep(6000);
            }catch(Exception e){}
            return null;
        }
    	@Override
        protected void onPostExecute(Void nada) {    
    		if(!mensajeEntregado){
    			Toast.makeText(miContext, "Posiblemente el dispositivo esté fuera de cobertura.", 
                        Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}
