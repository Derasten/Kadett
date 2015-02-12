package com.arduino.kadett;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Ajustes extends Activity{
	private Button btn_permiso,btn_numeros,btn_borrar;
	private Context miContext;
    private boolean advertencia = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.i("Ajustes_activity","onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajustes);
		
		getWindow().getDecorView().setBackgroundColor(0xffe4e5de);
		/*
		SharedPreferences preferencias = MainActivity.appContext.getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
       
        numeroArduino = preferencias.getString("NumeroArduino", "false");
*/
		miContext = getApplicationContext();
		
        // Activamos ActionBar y ponemos que pueda volver a la pantalla principal.
     	// Hay que configurar a donde va cuando se pulsa el botón en la función onOptionsItemSelected()
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        
		btn_permiso = (Button) findViewById(R.id.boton_permiso);
		btn_numeros = (Button) findViewById(R.id.boton_numeros);
		btn_borrar = (Button) findViewById(R.id.boton_borrar);
		
		btn_permiso.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	//Empezamos la actividad para pedir el numero de telefono para dar permiso
            	//Intent intenton = new Intent(miContext, PedirTelefonoPermiso.class);
            	Intent intenton  = new Intent(miContext,PedirTelefono.class);
            	intenton.putExtra("tipo", 3);
                startActivityForResult(intenton,15);  
            }
        });
		btn_numeros.setOnClickListener(new OnClickListener(){
			public void onClick(final View v){
				Intent intentNumero = new Intent(miContext,CambiarNumeros.class);
				startActivity(intentNumero);
			}
		});
		final Dialog dialogo = new Dialog(this);// Creamos dialogo
    	dialogo.setContentView(R.layout.alerta_inicial); // Fijamos alerta_inicial como layout
    	dialogo.setTitle("Borrar números"); // Fijamos el título
    	
    	TextView textoDialogo = (TextView) dialogo.findViewById(R.id.texto_alerta_inicial);// Fijamos en una variable el TextView de alerta_inicial en dialogo
    	textoDialogo.setText("¡Atención! \n Está acción borrará todos los números almacenados en la placa PCB.\n Si vuelve a pulsar el botón se llevara a cabo la acción."); // Fijamos el texto que va a ir en el TextView
    	ImageView imagenDialogo = (ImageView) dialogo.findViewById(R.id.imagen_alerta_inicial); // Fijamos en una variable el ImageView de alerta_inicial en dialogo 
    	imagenDialogo.setImageResource(R.drawable.ic_launcher); // Fijamos la imagen que va a ir en el ImageView

    	Button dialogoBoton = (Button) dialogo.findViewById(R.id.boton_alerta_inicial);// Fijamos en una variable el Button de alerta_inicial en dialogo
    	// Definimos ClickListener, cuando se pulse se cierra el dialogo
    	dialogoBoton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			advertencia=true;
    			dialogo.dismiss();
    			
    		}
    	});
		
		btn_borrar.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	if(advertencia){
            		//enviarSMS(numeroArduino," borrar& todos%");
            		new enviarSMS(" borrar& todos%");
            		advertencia=false;
            	}else{
            		dialogo.show();
            	}
            }
        });
		
		/*
		IntentFilter filter = new IntentFilter("MENSAJE_RECIBIDO");
	 	filter.addAction("TelefonoPermiso");
	 	LocalBroadcastManager.getInstance(this).registerReceiver(recibirMensajes, filter); // Hay que desregistrarlo en onDestroy!!!!!
	 	*/
	 	
		
	}
	/*
	private void enviarSMS(String numeroTelefono, String mensaje){  
		Log.i("AjustesActivity","enviarSMS");
		  String ENVIADO = "MENSAJE_ENVIADO";
	        String ENTREGADO = "MENSAJE_ENTREGADO";
	 
	        PendingIntent intentoEnviar = PendingIntent.getBroadcast(this, 0, new Intent(ENVIADO), 0);
	 
	        PendingIntent intentoEntregar = PendingIntent.getBroadcast(this, 0, new Intent(ENTREGADO), 0);

	        registerReceiver(confirmarMensajes, new IntentFilter(ENVIADO));
	        registerReceiver(entregarMensajes,new IntentFilter(ENTREGADO));
	       	 
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(numeroTelefono, null, mensaje, intentoEnviar, intentoEntregar);    
    }
	*/
	
	/*
	private BroadcastReceiver confirmarMensajes = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	Log.i("Ajustes","Broadcast confirmarMensajes");
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "Mensaje enviado.\n Espere un momento.", 
                            Toast.LENGTH_SHORT).show();
                    //Esperamos 5 segundos para esperar confirmación entrega mensaje
    	        	new esperar().execute();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Fallo al enviar", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "Sin servicio", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "PDU vacia",//Protocol Description Unit 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "No hay conexion de red", 
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    */
	
	/*
    private BroadcastReceiver entregarMensajes = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	Log.i("Ajustes","Broadcast entregarMENSAJES");
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "Mensaje entregado.\n Espere un momento.", 
                            Toast.LENGTH_SHORT).show();
                    //Ponemos el booleano a true
                    mensajeEntregado = true;
                    break;
                case SmsManager.STATUS_ON_ICC_UNREAD:
                	Toast.makeText(getBaseContext(), "STATUS_ON_ICC_UNREAD", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_UNSENT:
                	Toast.makeText(getBaseContext(), "STATUS_ON_ICC_UNSENT", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_SENT:
                	Toast.makeText(getBaseContext(), "STATUS_ON_ICC_SENT", 
                            Toast.LENGTH_SHORT).show();
                	break;
                case SmsManager.STATUS_ON_ICC_FREE:
                	Toast.makeText(getBaseContext(), "STATUS_ON_ICC_FREE", 
                            Toast.LENGTH_SHORT).show();
                	break;
            }
        }
    };
    
    */
	
    /*
	private BroadcastReceiver recibirMensajes = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();
	    	if(action.equals("MENSAJE_RECIBIDO")){
	    		Log.i("Ajustes","Mensaje Recibido");
	    		String numeroRemitente = intent.getStringExtra("numero");
	    		String cuerpoMensaje = intent.getStringExtra("cuerpo");
	    		Log.i("Numero",numeroRemitente);
	    		Log.i("Cuerpo",cuerpoMensaje.trim());
            
	    		//Toast.makeText(getBaseContext(), numeroRemitente,Toast.LENGTH_SHORT).show();
	    		//Toast.makeText(getBaseContext(), cuerpoMensaje.trim(),Toast.LENGTH_SHORT).show();
	    		if(cuerpoMensaje.trim().equals("PosicionCorrupta")){
	    			Toast.makeText(getBaseContext(),"La posición recibida está corrupta",Toast.LENGTH_SHORT).show();
	    		}else{
	    			convierteGPS(cuerpoMensaje);
	    			Intent intentona = new Intent(miContext, Mapa.class);
	    			// Por alguna razón volvía a crear una actividad mapa después de cerrar
	    			// Indicamos que la actividad no tendrá "Historial" y volverá a la actividad MainActivity
	    			intentona.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    			startActivity(intentona);
	    			finish();
	    		}
	    	}else{
	    		String numPermiso = "permiso& ";
	    		numPermiso += intent.getStringExtra("numeroPermiso");
	    		numPermiso +="%";
	    		Log.i("Num permiso",numPermiso);
	    		enviarSMS(numeroArduino,numPermiso);
	    	}
    		
    			
    		
	    }
	};
	*/
	
	/**
     *	convierteGPS(String cadena)
     *	Convierte las coordenadas GPS
     *	@param cadena String que se convertirá
     *
     **/
  /*  public void convierteGPS(String cadena){
    	Log.i("Ajustes","convierteGPS()");
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
	*/
	
	/*
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
    			Toast.makeText(getBaseContext(), "Posiblemente el dispositivo esté fuera de cobertura.", 
                        Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    */
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    /**
     * onDestroy()
     * 
     *  Se encarga de desregistrar receiver y 
     *  cancelar una posible busqueda de bluetooth cuando se destruye la aplicación
     *  
     **/
    @Override
	  protected void onDestroy() {
	    super.onDestroy();
	  /*  try{
	    	unregisterReceiver(recibirMensajes);
	    	unregisterReceiver(confirmarMensajes);
	    	unregisterReceiver(entregarMensajes);
	    }catch(Exception e){
	    	//No se llego a registrar
	    }
	    */
	  }
}

