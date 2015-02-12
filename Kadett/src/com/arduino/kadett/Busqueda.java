package com.arduino.kadett;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class Busqueda extends Service{
	
	private BroadcastReceiver bReceiver;
	private BluetoothAdapter btAdapter;
	private Conexion mConexion;
	private sHandler bHandler;
	
	private String ACTIVIDAD="Servicio B�squeda";
	private String mac;
	
    // Tipos de mensajes enviados al Handler desde Conexion.java
    public static final int MENSAJE_ESCRIBIR = 1;
    public static final int MENSAJE_CONEXION_PERDIDA = 2;
    
	
    private boolean ESTADO_COCHE=false;

    @Override
    public void onCreate() {
    	
    	Log.v(ACTIVIDAD,"onCreate()");
    	btAdapter= BluetoothAdapter.getDefaultAdapter();
    	bReceiver= new escuchaBT();
	     // Registramos el  BroadcastReceiver
	 	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	 	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	 	registerReceiver(bReceiver, filter); // Hay que desregistrarlo en onDestroy!!!!!
	 	bHandler = new sHandler(this);
	   
    }
    
 	// Clase tipo BroadCastReceiver
    public class escuchaBT extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
	        // Si se encuentra un dispositivo nuevo
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	Log.i(ACTIVIDAD,"Dispositivo encontrado");
	            // Coge el dispositivo y compara su direccion con el dispositivo registrado en la aplicaci�n
	            BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if(dispositivo.getAddress().equals(mac)){
	            	btAdapter.cancelDiscovery();
	            	mConexion.conectar(dispositivo);
	            	
	            }
	        }
	        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
	        	if(mConexion.cEstado==0){
	        		Log.i("Busqueda()","Busqueda parado");
	        		Intent intentFin = new Intent("ImagenAbiertoCerrado");
              		intentFin.putExtra("espera",true);
              		LocalBroadcastManager.getInstance(MainActivity.appContext).sendBroadcast(intentFin);
	        		
	        	}
	        }
        }

        // Constructor
        public escuchaBT(){

        }
    }
    /**
     * reconectar()
     * Trata de reconectar la conexi�n
     **/
    public void reconectar(){
    	Log.i(ACTIVIDAD,"reconectar()");
    	if(btAdapter.isDiscovering()){
    		btAdapter.cancelDiscovery();
    	}
    	btAdapter.startDiscovery();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.i(ACTIVIDAD,"onStartCommand()");
    	if(intent.getExtras().getString("MAC")==null){
    		Log.e(ACTIVIDAD,"onStartCommand() extra mac es NULL");
    	}else{
    		mac = intent.getExtras().getString("MAC");
    	}
    	mConexion = new Conexion(this, bHandler);
    	Log.i("Reconectar()","on startCommand");
    	reconectar();
      return Service.START_NOT_STICKY;//En teor�a, le dice al sistema que aunque quite el proceso, lo vuelva a empezar inmediatamente.
    }
    
    /**
     * confirmaEstado()
     *  Seguramente lo borre m�s adelante.
     **/
    public void confirmaEstado(){
    	
        	String mensaje="alive$";
        	byte[] envio = new byte[1024];;
        	try {
        		envio = mensaje.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	mConexion.escribir(envio);
        	
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    /**
     *	onDestroy()
     *
     **/
    @Override
	  public void onDestroy() {
	    
	    if (btAdapter != null) {
	      btAdapter.cancelDiscovery();
	    }
	    unregisterReceiver(bReceiver);
	    stopSelf();
	    super.onDestroy();
	  }
    
    /**
     * 	Handler()
     *  Recibe los "eventos" de Conexion.java
     **/
    private static class sHandler extends Handler {
        private final WeakReference<Busqueda> bServicio;

        public sHandler(Busqueda service) {
          bServicio = new WeakReference<Busqueda>(service);
        }

        @Override
        public void handleMessage(Message mensaje) {
          Busqueda servicio = bServicio.get();
          if (servicio != null) {
        	  switch (mensaje.what) {
              		case MENSAJE_ESCRIBIR:
              					byte[] escribirBuf = (byte[]) mensaje.obj;
              					String escrito = new String(escribirBuf, 0, mensaje.arg1);
              					if(escrito.equals("OK") && !servicio.ESTADO_COCHE){
              						Log.v("Envio a MainActivity","CocheAbierto");
              						servicio.ESTADO_COCHE=true;
              						Intent intent = new Intent("ImagenAbiertoCerrado");
              						intent.putExtra("estado",servicio.ESTADO_COCHE);
                  		
              						LocalBroadcastManager.getInstance(servicio).sendBroadcast(intent);            	  				
              					}
              					break;
              		case MENSAJE_CONEXION_PERDIDA:
              					servicio.ESTADO_COCHE= false;
              					Intent intfallo = new Intent("ImagenAbiertoCerrado");
              					intfallo.putExtra("estado",servicio.ESTADO_COCHE);
              					LocalBroadcastManager.getInstance(servicio).sendBroadcast(intfallo);
                
              					servicio.reconectar();
              					break;
              }
          }
        }
      }
    
}
