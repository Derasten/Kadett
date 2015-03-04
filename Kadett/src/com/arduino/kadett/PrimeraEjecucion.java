package com.arduino.kadett;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PrimeraEjecucion extends Activity{

	private static String ACTIVIDAD = "Clase PrimeraEjecucion";
	public static String DIRECCION_MAC = "mac_dispositivo";
	
	private ArrayList<BluetoothDevice> listaDispositivos = new ArrayList<BluetoothDevice>();
	private ArrayList<String> listaNombres = new ArrayList<String>();
	
	private ArrayAdapter<String> adaptaDispositivos;
	
	private BluetoothAdapter btAdapter;
	private BroadcastReceiver peReceiver;

	protected void onCreate(Bundle savedInstanceState) {
        
			super.onCreate(savedInstanceState);
        	//Mostramos un circulo de progreso
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        	setContentView(R.layout.alerta_lista_dispositivos);
        
        	Button dialogoBoton = (Button) findViewById(R.id.boton_alerta_dispositivos);// Fijamos en una variable el Button de alerta_inicial en dialogo
        	// Definimos ClickListener, cuando se pulse se cierra el dialogo
        	dialogoBoton.setOnClickListener(new OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			btAdapter.cancelDiscovery();
        			finish();
        		}
        	});
        
        	btAdapter = BluetoothAdapter.getDefaultAdapter();
        
        	// Registramos el broadCast Receiver
        	peReceiver= new buscarBT();
     		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
     		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
     	    registerReceiver(peReceiver, filter); // Unregister en onDestroy
     		
     	    //Nos  curamos en salud, si se está buscando se cancela la operación
     	    if(btAdapter.isDiscovering()){
     	    	btAdapter.cancelDiscovery();
     	    }
     	    //Mostramos el circulo de progreso
     	    setProgressBarIndeterminateVisibility(true);
     	    btAdapter.startDiscovery();
     	           
	}
	/**
	 * Se encarga de buscar dispositivos Bluetooth y mostrarlos en una lista.
	 * 
	 * */
	private class buscarBT extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // Si ha encontrado un nuevo dispositivo
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Cogemos el objeto BluetoothDevice
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Añade el nombre a un arrayAdapter para mostrar en una ListView
	            listaNombres.add(device.getName());
	            listaDispositivos.add(device);
	        }
	        //Si ha terminado de buscar
	        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
	        	Log.v(ACTIVIDAD,"Se ha terminado de buscar");
	        	//Ocultamos el circulo de progreso
	        	setProgressBarIndeterminateVisibility(false);
	        	//Si no ha encontrado ningún dispositivo
	        	if(listaDispositivos.isEmpty()){
	        		Toast.makeText(context, "No se ha encontrado ningún dispositivo", Toast.LENGTH_LONG).show();
	        	}else{
	        		//Si ha encontrado un dispositivo o más, lo/s mostramos
	        		Log.v("Lista dispositivos","No vacia");
		         	adaptaDispositivos = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,listaNombres);
		         	ListView listaDialogo = (ListView) findViewById(R.id.lista_alerta_dispositivos);// Fijamos en una variable el ListView de alerta_lista en dialogo
		         	listaDialogo.setClickable(true);
		         	listaDialogo.setAdapter(adaptaDispositivos); // Fijamos la lista que va a ir en el ListView
		         	listaDialogo.setOnItemClickListener(new listaDialogoClickListener());
		         	
	        	}
	        }
	    }
		
		/**
		 * Constructor
		 * */
		private buscarBT(){
			
		}
	}
	
	/** 
	 * Definimos las acciones a seguir según que se clíque en la lista
	 *
	 **/
    private class listaDialogoClickListener implements ListView.OnItemClickListener {
    	/**
    	 * Se guardará la MAC del dispositivo Bluetooth seleccionado en la lista
    	 * 
    	 * @param parent (AdapterView<?>) 
    	 * @param view (View)
    	 * @param opcion (int) Dispositivo seleccionado en la lista
    	 * @param id (long) 
    	 * 
    	 * */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int opcion, long id) {
        	
        		String dirMac = listaDispositivos.get(opcion).getAddress();
        		escribirPreferencias("Ejecucion","true");
        		escribirPreferencias("Dispositivo",dirMac);
        
        		Intent intent = new Intent();
                intent.putExtra(DIRECCION_MAC, dirMac);
        		setResult(Activity.RESULT_OK, intent);
                finish();
        	
        }
    }
    
    /**
	 * Guarda el número como una cadena en las preferencias (valores-clave)
	 * @param preferencia (String) Nombre de la preferencia a editar
	 * @param  valor (String) Valor a introducir en la preferencia 
	 */
	private void escribirPreferencias(String preferencia, String valor){
		SharedPreferences preferencias = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putString(preferencia, valor);
		editor.commit();
	}
    
	/**
	 * Desregistra los receptores y cancela la búsqueda bluetooth si la hubiera al cerrar la actividad
	 * @throws Exception Hay que ponerla al desregistrar receptores
	 * */
    @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    if (btAdapter != null) {
	      btAdapter.cancelDiscovery();
	    }
	    unregisterReceiver(peReceiver);
	  }
}
