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
		
		//Fijamos el color de fondo de la pantalla
		getWindow().getDecorView().setBackgroundColor(0xffe4e5de);
		
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
			
	}
	
	/**
     * Modificamos el botón del menú para cerrar bien la actividad.
     * @param item (MenuItem) 
     * @return x Boolean
     * */
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
	  
	  }
}

