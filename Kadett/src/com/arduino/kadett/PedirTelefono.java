package com.arduino.kadett;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PedirTelefono extends Activity{

	private TextView tituloActividad;
	private EditText textoTelefono;
	private Button   dialogoBoton;
	private int tipoTelefono;
	
	protected void onCreate(Bundle savedInstanceState) {
					
			super.onCreate(savedInstanceState);
        	
			//Obtenemos el tipo de pantalla a mostrar
			tipoTelefono = getIntent().getExtras().getInt("tipo");
			
			//Quitamos el titulo de la actividad, no queremos que se vea
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // Hacemos que no puedan recibir eventos de "tocar pantalla"
            getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
            // Notificando a la actividad
            getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

            setContentView(R.layout.alerta_telefono);
        	
			tituloActividad= (TextView) findViewById(R.id.titulo_pedir_telefono);
			
			textoTelefono =(EditText) findViewById(R.id.texto_telefono);
			dialogoBoton = (Button) findViewById(R.id.boton_alerta_telefono);// Fijamos en una variable el Button de alerta_inicial en dialogo
	
            switch(tipoTelefono){
            		//Pedir teléfono móvil
            	case 1: 
            			tituloActividad.setText("Introduzca su número de teléfono: \n + prefijo_país número \n Ejemplo: +34600112233");
            	
            			// Definimos ClickListener, cuando se pulse se cierra el dialogo
            			dialogoBoton.setOnClickListener(new OnClickListener() {
            				@Override
            				public void onClick(View v) {
            					String numero = textoTelefono.getText().toString();
            					if(!comprobarNumero(numero)){
            						Toast.makeText(getApplicationContext(), "Introduzca el número como se indica", Toast.LENGTH_SHORT).show();
            					}else{
            						escribirPreferencias("Numero",numero);
            						finish();
            					}
            				}
            			});
            			
            			break;
            			
            		//Pedir teléfono Arduino
            	case 2:	
            		tituloActividad.setText("Introduzca ahora el número de telefono insertado en el dispositivo Arduino.\n + prefijo_país número \n Ejemplo: +34600112233");
            	
            		// Definimos ClickListener, cuando se pulse se cierra el dialogo
            		dialogoBoton.setOnClickListener(new OnClickListener() {
            			@Override
            			public void onClick(View v) {
            				String numero = textoTelefono.getText().toString();
            				if(!numero.startsWith("+") || numero.length()<11){
            					Toast.makeText(getApplicationContext(), "Introduzca el número como se indica", Toast.LENGTH_SHORT).show();
            				}else{
            					escribirPreferencias("NumeroArduino",numero);
            					finish();
            				}
            			}
            		});
            		
            		break;
            		//Pedir teléfono al que se le dará permiso
            	case 3:
            		tituloActividad.setText("Introduzca el número de telefono al que va a dar permiso.\n + prefijo_país número \n Ejemplo: +34600112233");
                	
                	// Definimos ClickListener, cuando se pulse se cierra el dialogo
                	dialogoBoton.setOnClickListener(new OnClickListener() {
                		@Override
                		public void onClick(View v) {
                			String numero = textoTelefono.getText().toString();
                			if(!numero.startsWith("+") || numero.length()<11){
                				Toast.makeText(getApplicationContext(), "Introduzca el número como se indica", Toast.LENGTH_SHORT).show();
                			}else{
                				String numPermiso = "permiso& ";
                	    		numPermiso += numero;
                	    		numPermiso +="%";
                	    		new enviarSMS(numPermiso);
                			
                    			finish();
                			}
                      		
                		}
                	});
            		
                	break;
            	
                default:
                	
                	break;
            			
            }
	}
	
	/**
	 * 
	 * Commprueba que el número empieza con el carácter '+' y 
	 * que al menos tenga 11 caracteres
	 * @param numero (String) numero a comprobar
	 * */
	private boolean comprobarNumero(String numero){
		if(!numero.startsWith("+") || numero.length()<11 ){
			return false;
		}else{
			return true;
		}
	}
	
	@Override
	  public boolean onTouchEvent(MotionEvent event) {
	    // Si se notifica que el usuario ha tocado fuera de la aplicación, no hacemos nada.
	    if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
	      //finish();
	      return true;
	    }

	    // Delegamos el resto a la actividad
	    return super.onTouchEvent(event);
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
	
}
