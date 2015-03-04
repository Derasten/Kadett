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
    private LinearLayout mLinearLayout; // LinearLayout, engloba una imagen y una lista con el menu. Se desplazar� lateralmente.
    private ImageView mImagenCoche;	// Imagen para decorar el desplazamiento lateral. No tiene otra utilidad.
    private ListView mMenuLateral; // Lista con los items del men�. 
    private ActionBarDrawerToggle mDrawerToggle; //Controla el ActionBar y el desplazamiento
    private SharedPreferences preferencias;	// Accedemos a las preferencias de la aplicacion, en este caso vemos si es la primera vez que se ha ejecutado
    private String[] mMenuArray;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(ACTIVIDAD,"onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appContext=getApplicationContext();
		
		
		
        // Registramos LocalBroadCastManager para recibir informaci�n del Servicio "B�squeda"
        LocalBroadcastManager.getInstance(this).registerReceiver(
                escuchaBusqueda, new IntentFilter("ImagenAbiertoCerrado"));
        
		btAdapter= BluetoothAdapter.getDefaultAdapter();
		
		/*
         * Comprueba si es la primera vez que se ejecuta la aplicaci�n,
         * si es la primera vez salta un di�logo informando que tiene que configurar la aplicaci�n.
         */
        // Inicializamos las preferencias que hemos llamado MisPreferencias cuando las hemos creado en Ajustes.class, en modo privado
        preferencias = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        String compruebaEjecucion = preferencias.getString("Ejecucion", "false"); // Cogemos la preferencia ejecucion, si est� vacia devuelve false.
        miMac = preferencias.getString("Dispositivo","false");
        if(compruebaEjecucion.equals("false")){
        	mostrarDialogo();
        	
        }else{
        	// Comprobamos si el dispositivo tiene Bluetooth f�sico
            if (btAdapter == null) {
                Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
                return;
            }
            // Comprobamos si est� encendido, si no lo est� intentamos activarlo. "Intentamos" porque depender� del usuario permitir su activaci�n o no.
            if(!btAdapter.isEnabled()){
               	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
            	servicio();
            }
        }	
        dibujarPantalla();
        
        
                
	}
	
	/**
	 *  
	 * Muestra todos los elementos en la pantalla.
	 * 
	 * */
	private void dibujarPantalla(){
		if(primeraVez){
        	mMenuArray = getResources().getStringArray(R.array.menu_array_principio);// Guardamos el array con los opciones del men� y boton EMPEZAR.
        }else{
        	mMenuArray = getResources().getStringArray(R.array.menu_array);// Guardamos el array con los opciones del men�.
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// Fijamos el DrawerLayout a la vista que est� en Layout-> activity_main.xml
        mLinearLayout = (LinearLayout) findViewById(R.id.marco_menu_lateral);// Fijamos el LinearLayout a la vista que est� en Layout-> activity_main.xml
        mMenuLateral = (ListView) findViewById(R.id.menu_lateral); // Fijamos el ListView a la vista que est� en Layout-> activity_main.xml
        mImagenCoche = (ImageView) findViewById(R.id.imagen_coche); // Fijamos el ImageView a la vista que est� en Layout-> activity_main.xml
        
        //Fijamos el fondo de la imagen
        mImagenCoche.setBackgroundColor(0xffe4e5de); 
        
        // Fija un borde en el lateral del men� lateral
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setScrimColor(0x505D595B);//Fija el color de la sombra que se pondr� encima de la pantalla cuando se desplaze el men�.
               
        // Fijamos el adaptador del ListView con un ArrayAdapter que contenga la vista y los nombres del men�
        mMenuLateral.setAdapter(new ArrayAdapter<String>(this,
                R.layout.elementos_menu_lateral, mMenuArray));
        //Fijamos el ClickListener que est� declarado m�s abajo
        mMenuLateral.setOnItemClickListener(new MenuClickListener());

        // Activamos ActionBar para que el icono de la aplicaci�n se comporte como una accion para el DrawerToggle
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        /*
         * Fijamos la imagen de las 3 rayas de men�,la acci�n del icono de la aplicaci�n y
         * el deslizamiento del men� en el DrawerToogle
         */
        mDrawerToggle = new ActionBarDrawerToggle(
                			this,                  // Activity en la que se funciona
                			mDrawerLayout,         // DrawerLayout que utilizar�
                			R.drawable.ic_drawer,  // Imagen que remplaza la flecha del ActionBar para volver a Home
                			R.string.menu_abierto, // Descripci�n del menu abierto para accesibilidad
                			R.string.menu_cerrado  // Descripci�n del menu cerrada para accesibilidad
                			);
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	/**
	 * 
	 * Muestra el dialogo de entrada, cuando se ejecuta la app por primera vez.
	 * 
	 * */
	private void mostrarDialogo(){
		primeraVez=true;
    	Log.i(ACTIVIDAD,"Es la primera vez que se ejecuta");
    	/*	Como es la primera vez que se ejecuta,
    	 * 	mostramos un dialogo modificado a gusto, diciendo que tiene que configurar la aplicaci�n
    	 */
    	final Dialog dialogo = new Dialog(this);// Creamos dialogo
    	dialogo.setContentView(R.layout.alerta_inicial); // Fijamos alerta_inicial como layout
    	dialogo.setTitle("Bienvenido"); // Fijamos el t�tulo
    	
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
	 *  
	 *  Utilizaremos para escuchar al servicio "Busqueda",
	 *  especificamente ahora para actualizar la imagen del estado del coche "Abierto-Cerrado"
	 * 
	 **/
	private BroadcastReceiver escuchaBusqueda = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.i(ACTIVIDAD,"onReceive() "+context.getPackageName());
	    	// Cogemos la informaci�n pasada
	        boolean estadoCoche = intent.getBooleanExtra("estado",false);//la funci�n getBooleanExtra() necesita un valor por defecto, le damos false.
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
    			//Esperamos 5 segundos y se empezar� el servicio
	        	new esperar().execute();
        		
	        }
	    }
	};
	
	/**
	 * Servicio()
	 * 
	 * Funci�n que utilizamos para empezar el servicio.
	 * 
	 **/
	public void servicio(){
		Log.i(ACTIVIDAD,"servicio()");
		Intent i= new Intent(this, Busqueda.class);
		// a�adimos la MAC del dispositivo al servicio para que se conecte.
		i.putExtra("MAC",miMac);
		//Empezamos el servicio
		startService(i); 
	}

	/** 
	 *  
	 *  Definimos las acciones a seguir seg�n que se cl�que en el men� lateral
	 *  
	 **/
    private class MenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int opcion, long id) {
            seleccionar(opcion,view);
            //Volvemos a poner el bot�n seleccionado al color original del menu
        	new Handler().postDelayed(new Runnable() {
                public void run() {
                    view.setBackgroundColor(0xffd50000);
                }
            }, 200); 
        }
    }
	
    /**
     * 
     * Recoge la opci�n seleccionada por el usuario en el men� y actua en consecuencia.
     * Hay 2 men�s, el primero cuando se ejecuta la aplicaci�n por primera vez y
     * el otro el resto del tiempo
     * 
     * @param opcion (int) opci�n seleccionada
     * @param view (View) La vista en la que actua
     * 
     **/
    private void seleccionar(int opcion,View view) {
    	mMenuLateral.setItemChecked(opcion, true);
        mDrawerLayout.closeDrawer(mLinearLayout); // Cerramos el men� lateral
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
        			//Si se ha seleccionado Salir, finalizamos la aplicaci�n.
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
    				//Si se ha seleccionado obtener posici�n coche
    				//Luego lo pongo m�s adelante.
    				new enviarSMS(PEDIR_POSICION_COCHE);
    				break;
    			case 2:
    				//Si se ha selecionado Ajustes, empezamos la actividad Ajustes.
    				Intent intent = new Intent(this, Ajustes.class);
    				startActivity(intent);
    				break;
    			case 3:
    				//Si se ha seleccionado Salir, finalizamos la aplicaci�n.
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
     *
     * Es lo que se hace cuando se presiona el bot�n atr�s
     * 
     **/
    @Override
    public void onBackPressed() {
    /*	//Si se ha seleccionado Salir, finalizamos la aplicaci�n.
		Intent i= new Intent(this, Busqueda.class);
		//Empezamos el servicio
		stopService(i);
		System.exit(0);
	*/
    	moveTaskToBack(true); 
    }
    
    

    /**
     * OnPostCreate se llama cuando se ha cargado una actividad completamente,
     *  despu�s de haber llamado a onStart() y onRestoreInstanceState(Bundle) 
     *  
     *  @param savedInstanceState (Bundle) 
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincroniza el estado de DrawerToggle despues de que se haya llamado a onRestoreInstanceState
        mDrawerToggle.syncState();
    }

    /**
     * Actua en la barra del men� cuando cambia la configuraci�n
     * 
     * @param newConfig (Configuration) la nueva configuraci�n
     * */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pasa cualquier cambio en la configuracion a DrawerToggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    	
    /**
     * Se encarga de abrir o cerrar la pesta�a del men�
     * @param item (MenuItem) Se le pasa el men�
     * @return boolean
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // La acci�n del actionBar (arriba/izquierda) debe abrir o cerrar la pesta�a del menu.
         // ActionBarDrawerToggle se encargar� de eso.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Se encarga de las acciones de los botones
        return super.onOptionsItemSelected(item);
        
    }
    /**
     *	 
     * 	Se encarga de los resultados devueltos por las actividades, 
     *  en �ste caso, se encarga del resultado de la actividad PrimeraEjecucion
     *  
     *  @param requestCode (int) c�digo que se pidio
     *  @param resultCodigo (int) c�digo que di� como resultado
     *  @param datos (Intent) intento con los datos
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
        			// a�adimos la mac al intento.
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
        	/*Creo que ya no necesitamos este caso*/
        case PEDIR_PCB:
        	//Una vez guardado el numero del dispositivo Arduino se empieza a buscar dispositivos
        	// Comprobamos si el dispositivo tiene Bluetooth f�sico
            if (btAdapter == null) {
                Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
                return;
            }
            // Comprobamos si est� encendido, si no lo est� intentamos activarlo. "Intentamos" porque depender� del usuario permitir su activaci�n o no.
            if(!btAdapter.isEnabled()){
               	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        	break;
        case REQUEST_ENABLE_BT:
        	//Cuando se pide que active el Bluetooth del dispositivo Android, si declina la petici�n 
        	// se cierra la aplicaci�n
        	if(resultCode == RESULT_CANCELED){
        		Toast.makeText(this, "Bluetooth denegado, se cerrar� la aplicaci�n.", Toast.LENGTH_SHORT).show();
        	
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
    
    /**
     * 
     * Clase as�ncrona que se encarga de esperar un tiempo determinado
     * y volver a empezar el servicio.
     * 
     * */
    private class esperar extends AsyncTask<Void, Void, Void>{
    	
    	/**
    	 * Se encarga de esperar X segundos, 
    	 * para no estar buscando constantemente y agotar la bateria
    	 * @param nada (Void) No se le pasa ning�n parametro.
    	 * @throws Exception Obligatorio al usar hilos.
    	 * */
    	@Override
        protected Void doInBackground(Void... nada) {
            try{
            	Thread.sleep(2000);//Anteriormente estaba a 5000
            }catch(Exception e){}
            return null;
        }
    	
    	/**
    	 * Ejecuta el servicio despu�s de haber esperado.
    	 * @param nada (Void) No se le pasa ning�n parametro
    	 * 
    	 * */
    	@Override
        protected void onPostExecute(Void nada) {    
    		servicio();
    	}
    }
   
    /**
     * 
     *  Se encarga de desregistrar receiver y 
     *  cancelar una posible busqueda de bluetooth cuando se destruye la aplicaci�n
     * 
     *  @throws Excepction Siempre que se desregistra un receptor hay que asegurarlo.
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
