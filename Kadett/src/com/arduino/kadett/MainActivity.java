package com.arduino.kadett;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

	static Context appContext;
	
	private static final String ACTIVIDAD = "Main Activity";
	private static final int PRIMERA_EJECUCION = 1;
	private static final int PEDIR_TELEFONO = 2;
	private static final int PEDIR_PCB = 3;
	private static final int REQUEST_ENABLE_BT = 5;
	private static final String PEDIR_POSICION_COCHE = " gps& pos%";
	private String miMac;
	
	//private boolean btActivado=false;
	private boolean primeraVez=false;
		
	private BluetoothAdapter btAdapter;
	
	private DrawerLayout mDrawerLayout; // DrawerLayout, es el marco de la actividad, se necesita para poder mostrar el desplazamiento lateral
    private LinearLayout mLinearLayout; // LinearLayout, engloba una imagen y una lista con el menu. Se desplazará lateralmente.
    private ImageView mImagenCoche;	// Imagen para decorar el desplazamiento lateral. No tiene otra utilidad.
    private ListView mMenuLateral; // Lista con los items del menú. 
    private ActionBarDrawerToggle mDrawerToggle; //Controla el ActionBar y el desplazamiento
    private SharedPreferences preferencias;	// Accedemos a las preferencias de la aplicacion, en este caso vemos si es la primera vez que se ha ejecutado
    private String[] mMenuArray;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ACTIVIDAD,"onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appContext=getApplicationContext();
		
		
		
        // Registramos LocalBroadCastManager para recibir información del Servicio "Búsqueda"
        LocalBroadcastManager.getInstance(this).registerReceiver(
                escuchaBusqueda, new IntentFilter("ImagenAbiertoCerrado"));
        
		btAdapter= BluetoothAdapter.getDefaultAdapter();
		
		/*
         * Comprueba si es la primera vez que se ejecuta la aplicación,
         * si es la primera vez salta un diálogo informando que tiene que configurar la aplicación.
         */
        // Inicializamos las preferencias que hemos llamado MisPreferencias cuando las hemos creado en Ajustes.class, en modo privado
        preferencias = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        String compruebaEjecucion = preferencias.getString("Ejecucion", "false"); // Cogemos la preferencia ejecucion, si está vacia devuelve false.
        miMac = preferencias.getString("Dispositivo","false");
        if(compruebaEjecucion.equals("false")){
        	mostrarDialogo();
        	
        }else{
        	// Comprobamos si el dispositivo tiene Bluetooth físico
            if (btAdapter == null) {
                Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
                return;
            }
            // Comprobamos si está encendido, si no lo está intentamos activarlo. "Intentamos" porque dependerá del usuario permitir su activación o no.
            if(!btAdapter.isEnabled()){
               	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
            	servicio();
            }
        }	
        dibujarPantalla();
        
        
                
	}
	
	private void dibujarPantalla(){
		if(primeraVez){
        	mMenuArray = getResources().getStringArray(R.array.menu_array_principio);// Guardamos el array con los opciones del menú y boton EMPEZAR.
        }else{
        	mMenuArray = getResources().getStringArray(R.array.menu_array);// Guardamos el array con los opciones del menú.
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// Fijamos el DrawerLayout a la vista que está en Layout-> activity_main.xml
        mLinearLayout = (LinearLayout) findViewById(R.id.marco_menu_lateral);// Fijamos el LinearLayout a la vista que está en Layout-> activity_main.xml
        mMenuLateral = (ListView) findViewById(R.id.menu_lateral); // Fijamos el ListView a la vista que está en Layout-> activity_main.xml
        mImagenCoche = (ImageView) findViewById(R.id.imagen_coche); // Fijamos el ImageView a la vista que está en Layout-> activity_main.xml
        
        //Fijamos el fondo de la imagen
        mImagenCoche.setBackgroundColor(0xffe4e5de); 
        
        // Fija un borde en el lateral del menú lateral
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setScrimColor(0x505D595B);//Fija el color de la sombra que se pondrá encima de la pantalla cuando se desplaze el menú.
               
        // Fijamos el adaptador del ListView con un ArrayAdapter que contenga la vista y los nombres del menú
        mMenuLateral.setAdapter(new ArrayAdapter<String>(this,
                R.layout.elementos_menu_lateral, mMenuArray));
        //Fijamos el ClickListener que está declarado más abajo
        mMenuLateral.setOnItemClickListener(new MenuClickListener());

        // Activamos ActionBar para que el icono de la aplicación se comporte como una accion para el DrawerToggle
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        /*
         * Fijamos la imagen de las 3 rayas de menú,la acción del icono de la aplicación y
         * el deslizamiento del menú en el DrawerToogle
         */
        mDrawerToggle = new ActionBarDrawerToggle(
                			this,                  // Activity en la que se funciona
                			mDrawerLayout,         // DrawerLayout que utilizará
                			R.drawable.ic_drawer,  // Imagen que remplaza la flecha del ActionBar para volver a Home
                			R.string.menu_abierto, // Descripción del menu abierto para accesibilidad
                			R.string.menu_cerrado  // Descripción del menu cerrada para accesibilidad
                			);
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	private void mostrarDialogo(){
		primeraVez=true;
    	Log.i(ACTIVIDAD,"Es la primera vez que se ejecuta");
    	/*	Como es la primera vez que se ejecuta,
    	 * 	mostramos un dialogo modificado a gusto, diciendo que tiene que configurar la aplicación
    	 */
    	final Dialog dialogo = new Dialog(this);// Creamos dialogo
    	dialogo.setContentView(R.layout.alerta_inicial); // Fijamos alerta_inicial como layout
    	dialogo.setTitle("Bienvenido"); // Fijamos el título
    	
    	TextView textoDialogo = (TextView) dialogo.findViewById(R.id.texto_alerta_inicial);// Fijamos en una variable el TextView de alerta_inicial en dialogo
    	textoDialogo.setText(R.string.primera_ejecucion); // Fijamos el texto que va a ir en el TextView
    	ImageView imagenDialogo = (ImageView) dialogo.findViewById(R.id.imagen_alerta_inicial); // Fijamos en una variable el ImageView de alerta_inicial en dialogo 
    	imagenDialogo.setImageResource(R.drawable.ic_launcher); // Fijamos la imagen que va a ir en el ImageView

    	Button dialogoBoton = (Button) dialogo.findViewById(R.id.boton_alerta_inicial);// Fijamos en una variable el Button de alerta_inicial en dialogo
    	// Definimos ClickListener, cuando se pulse se cierra el dialogo
    	dialogoBoton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			dialogo.dismiss();
    			
    			//Empezamos la actividad para pedir el numero de telefono
            	Intent intenton = new Intent(appContext, PedirTelefono.class);
            	intenton.putExtra("tipo", 1);
                startActivityForResult(intenton,PEDIR_TELEFONO);
    		}
    	});
    	
    	dialogo.show(); //Mostramos el dialogo
	}
	/**
	 *  
	 * 	BroadcastReceiver escuchaBusqueda
	 *  
	 *  que utilizaremos para escuchar al servicio "Busqueda",
	 *  especificamente ahora para actualizar la imagen del estado del coche "Abierto-Cerrado"
	 * 
	 **/
	private BroadcastReceiver escuchaBusqueda = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.i(ACTIVIDAD,"onReceive() "+context.getPackageName());
	    	// Cogemos la información pasada
	        boolean estadoCoche = intent.getBooleanExtra("estado",false);//la función getBooleanExtra() necesita un valor por defecto, le damos false.
	        boolean espera = intent.getBooleanExtra("espera", false);
	        // Si estadoCoche es true, ponemos la imagen de coche_abierto, sino la del coche_cerrado
	        if (estadoCoche){
	        	mImagenCoche.setImageResource(R.drawable.coche_abierto);
	        }else{
	        	mImagenCoche.setImageResource(R.drawable.coche_cerrado);
	        }
	        
	        if(espera){
	        	Log.i("MainActivity()","Espera recibida");
	        	Intent i= new Intent(getBaseContext(), Busqueda.class);
    			//Paramos el servicio
    			stopService(i);
    			//Esperamos 5 segundos y se empezará el servicio
	        	new esperar().execute();
        		
	        }
	    }
	};
	
	/**
	 * Servicio()
	 * 
	 * Función que utilizamos para empezar el servicio.
	 * 
	 **/
	public void servicio(){
		Log.i(ACTIVIDAD,"servicio()");
		Intent i= new Intent(this, Busqueda.class);
		// añadimos la MAC del dispositivo al servicio para que se conecte.
		i.putExtra("MAC",miMac);
		//Empezamos el servicio
		startService(i); 
	}

	/** 
	 *  MenuClickListener 
	 *  
	 *  Definimos las acciones a seguir según que se clíque en el menú lateral
	 *  
	 **/
    private class MenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int opcion, long id) {
            seleccionar(opcion,view);
            //Volvemos a poner el botón seleccionado al color original del menu
        	new Handler().postDelayed(new Runnable() {
                public void run() {
                    view.setBackgroundColor(0xffd50000);
                }
            }, 200); 
        }
    }
	
    /**
     * Seleccionar(int opcion, View view)
     * 
     * Recoge la opción seleccionada por el usuario y actua en consecuencia
     * 
     **/
    private void seleccionar(int opcion,View view) {
    	mMenuLateral.setItemChecked(opcion, true);
        mDrawerLayout.closeDrawer(mLinearLayout); // Cerramos el menú lateral
        if(primeraVez){        
        	switch (opcion){
        		case 0:
        			// Si se ha seleccionado Empezar, ejecutamos la actividad primeraEjecucion
        			Intent intentin = new Intent(this, PrimeraEjecucion.class);
                    startActivityForResult(intentin,PRIMERA_EJECUCION);
        			break;        		
        		case 1:
        			//Si se ha selecionado Ajustes, empezamos la actividad Ajustes.
        		    Intent intent = new Intent(this, Ajustes.class);
        		    startActivity(intent);
        		    break;
        		case 2:
        			//Si se ha seleccionado Salir, finalizamos la aplicación.
        			Intent i= new Intent(this, Busqueda.class);
        			stopService(i);
        			System.exit(0);
                	break;
        		default:
        			break;	
        	}
        }else{
        	switch (opcion){
    			case 0:
    				//Si se ha selecionado Mapa, empezamos la actividad Mapa.
    				Intent intentona = new Intent(this, Mapa.class);
    				startActivity(intentona);
    				break;
    			case 1:
    				//Si se ha seleccionado obtener posición coche
    				//Luego lo pongo más adelante.
    				new enviarSMS(PEDIR_POSICION_COCHE);
    				break;
    			case 2:
    				//Si se ha selecionado Ajustes, empezamos la actividad Ajustes.
    				Intent intent = new Intent(this, Ajustes.class);
    				startActivity(intent);
    				break;
    			case 3:
    				//Si se ha seleccionado Salir, finalizamos la aplicación.
    				Intent i= new Intent(this, Busqueda.class);
    				stopService(i);
    				System.exit(0);
    				break;
    			default:
    				break;	
        	}
        }
    }

    /**
     * onBackPressed()
     * Es lo que se hace cuando se presiona el botón atrás
     * 
     **/
    @Override
    public void onBackPressed() {
    /*	//Si se ha seleccionado Salir, finalizamos la aplicación.
		Intent i= new Intent(this, Busqueda.class);
		//Empezamos el servicio
		stopService(i);
		System.exit(0);
	*/
    	moveTaskToBack(true); 
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     * Called when activity start-up is complete (after onStart() and onRestoreInstanceState(Bundle) have been called)
     */

    /**
     * OnPostCreate se llama cuando se ha cargado una actividad completamente,
     *  después de haber llamado a onStart() y onRestoreInstanceState(Bundle) 
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincroniza el estado de DrawerToggle despues de que se haya llamado a onRestoreInstanceState
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pasa cualquier cambio en la configuracion a DrawerToggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // La acción del actionBar (arriba/izquierda) debe abrir o cerrar la pestaña del menu.
         // ActionBarDrawerToggle se encargará de eso.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Se encarga de las acciones de los botones
        return super.onOptionsItemSelected(item);
        
    }
    /**
     *	onActivityResult(int requestCode,int resultCode,Intent datos)
     * 
     * 	Se encarga de los resultados devueltos por las actividades, 
     *  en éste caso, se encarga del resultado de la actividad PrimeraEjecucion
     *  
     **/
    
    public void onActivityResult(int requestCode, int resultCode, Intent datos) {
        Log.i(ACTIVIDAD,"onActivityResult()");
        switch (requestCode) {
        	case PRIMERA_EJECUCION:
        		// Cuando la actividad PrimeraEjecucion devuelve la mac del dispositivo a conectar.
        		if (resultCode == Activity.RESULT_OK) {
        			String mac = datos.getExtras().getString(PrimeraEjecucion.DIRECCION_MAC);
        			Intent j= new Intent(this, Busqueda.class);
        			// añadimos la mac al intento.
        			j.putExtra("MAC", mac);
        			startService(j); 
        		}else{
        			//Intent intentin = new Intent(this, PrimeraEjecucion.class);
        			//startActivityForResult(intentin,PRIMERA_EJECUCION);
        		}
        		break;
        case PEDIR_TELEFONO:
        	Intent intenton = new Intent(appContext, PedirTelefono.class);
        	intenton.putExtra("tipo", 2);
            startActivityForResult(intenton,PEDIR_PCB);
        	break;
        case PEDIR_PCB:
        	// Comprobamos si el dispositivo tiene Bluetooth físico
            if (btAdapter == null) {
                Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
                return;
            }
            // Comprobamos si está encendido, si no lo está intentamos activarlo. "Intentamos" porque dependerá del usuario permitir su activación o no.
            if(!btAdapter.isEnabled()){
               	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        	break;
        case REQUEST_ENABLE_BT:
        	if(resultCode == RESULT_CANCELED){
        		Toast.makeText(this, "Bluetooth denegado, se cerrará la aplicación.", Toast.LENGTH_SHORT).show();
        	
        		finish();
        	}else if(resultCode == RESULT_OK){
        		if(!primeraVez){
        			Log.i("bluetooth","servicio");
        			// Si ya se ha ejecutado una vez y configurado, activamos directamente el servicio "Busqueda".
        			servicio();
        		}
        	}
        	break;
        case -1:
        	break;
        }
        
    }
    
    private class esperar extends AsyncTask<Void, Void, Void>{
    	@Override
        protected Void doInBackground(Void... nada) {
            try{
            	Thread.sleep(2000);//Anteriormente estaba a 5000
            }catch(Exception e){}
            return null;
        }
    	@Override
        protected void onPostExecute(Void nada) {    
    		servicio();
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
	    if (btAdapter != null) {
	      btAdapter.cancelDiscovery();
	    }
	    try{
	    	unregisterReceiver(escuchaBusqueda);
	    }catch(Exception e){
	    	//En caso de que no se llegue a registrar escuchaBusqueda
	    }
	  }
    
    
}
