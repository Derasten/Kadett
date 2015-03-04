package com.arduino.kadett;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CambiarNumeros extends Activity{
		
	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.i("CambiarNumeros Activity","onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cambiar);
		
		getWindow().getDecorView().setBackgroundColor(0xffe4e5de);
		
		//dibujamos Pantalla y le pasamos los números de las preferencias
		dibujarPantalla(leerPreferencias("Numero"),leerPreferencias("NumeroArduino"));
        
	}
	/**
	 * leerPreferencias()
	 * Lee las preferencias almacenadas
	 * @param preferencia (String) Nombre de la preferencia a leer
	 * @return String con el valor leido
	 * */
	private String leerPreferencias(String preferencia){
		String cadena="";
		SharedPreferences preferencias = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        cadena = preferencias.getString(preferencia, "false"); // Cogemos la preferencia ejecucion, si está vacia devuelve false.
        
		return cadena;
	}
	
	/**
	 * escribirPreferencias()
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
	 * dibujarPantalla()
	 * @param contenidoNumeroMovil (String) El numero Movil a mostrar
	 * @param contenidoNumeroArduino (String) El numero Arduino a mostrar
	 */
	private void dibujarPantalla(final String contenidoNumeroMovil, final String contenidoNumeroArduino){
		// Activamos ActionBar y ponemos que pueda volver a la pantalla principal.
     	// Hay que configurar a donde va cuando se pulsa el botón en la función onOptionsItemSelected()
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
		TextView numeroMovil = (TextView) findViewById(R.id.numero_movil);
    	numeroMovil.setText("Número de teléfono actual.\n + prefijo_país número \n Ejemplo: +34600112233");
    	
    	final EditText editNumeroMovil =(EditText) findViewById(R.id.edit_numero_movil);
    	if(contenidoNumeroMovil.equals("false")){
    		editNumeroMovil.setText("",TextView.BufferType.EDITABLE);
    	}else{
    		editNumeroMovil.setText(contenidoNumeroMovil,TextView.BufferType.EDITABLE);
    	}
    	TextView numeroArduino =(TextView) findViewById(R.id.numero_arduino);
    	numeroArduino.setText("Número de teléfono ARDUINO.\n + prefijo_país número \n Ejemplo: +34600112233");
    	
    	final EditText editNumeroArduino = (EditText) findViewById(R.id.edit_numero_arduino);
    	if(contenidoNumeroArduino.equals("false")){
    		editNumeroArduino.setText("",TextView.BufferType.EDITABLE);
    	}else{
    		editNumeroArduino.setText(contenidoNumeroArduino,TextView.BufferType.EDITABLE);
    	}
    	Button botonGuardar = (Button) findViewById(R.id.boton_cambiar_numeros);// Fijamos en una variable el Button de alerta_inicial en dialogo
    	// Definimos ClickListener, cuando se pulse se cierra el dialogo
    	botonGuardar.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			String numMovil = editNumeroMovil.getText().toString();
    			String numArduino = editNumeroArduino.getText().toString();
    			if(!numMovil.equals(contenidoNumeroMovil)){
    				escribirPreferencias("Numero",numMovil);
                	
                }
                if (!numArduino.equals(contenidoNumeroArduino)){
                	escribirPreferencias("NumeroArduino",numArduino);
                }
    			finish();
    		}
    	});
	}
	/**
	 * onOptionsItemSelected()
	 * Modificamos el ActionBar para que vaya a la actividad HOME cuando se pulse.
	 * @param item (MenuItem) 
	 * @return boolean
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
}
