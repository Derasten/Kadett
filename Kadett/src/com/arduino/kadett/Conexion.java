package com.arduino.kadett;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;


public class Conexion {

	 private final BluetoothAdapter btAdapter;
	    private final Handler cHandler;
	    private hiloConectar cHiloConectar;
	    private hiloConectado cHiloConectado;
	    private final static String ACTIVIDAD = "Conexion Activity";
	    public int cEstado=0;
	    private final UUID bluetoothUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	    
	    //Tipos de estado
	    public static final int ESTADO_CERO = 0;       // No se está haciendo nada
	    public static final int ESTADO_CONECTANDO = 1; // Intentando conectarse a un dispositivo
	    public static final int ESTADO_CONECTADO = 2;  // Conectado a un dispositivo
	    
	    private double norte	= 0;
	    private double este	= 0;
	    private boolean longitud = false;
	    private boolean latitud  = false;
	    	    
	    public Conexion(Context context, Handler handler){
	    	Log.i(ACTIVIDAD,"Constructor");
	    	btAdapter = BluetoothAdapter.getDefaultAdapter();
	        cHandler = handler;
	    }
	    
	    /**
	     * 	setEstado(int estado)
	     * Fija el estado actual de la conexion
	     * @param estado  Integer que define el estado actual de la conexion
	     */
	    private synchronized void setEstado(int estado) {
	        Log.d(ACTIVIDAD, "setEstado() " + cEstado + " -> " + estado);
	        cEstado = estado;
	    }

	    /**
	     * 	getEstado()
	     * 	Devuelve el estado actual de la conexion
	     **/
	    public synchronized int getEstado() {
	        return cEstado;
	    }
	    
	    /**
	     * 	empezar()
	     * 	Resetea la conexión.
	     **/
	    public synchronized void empezar() {
	        Log.i(ACTIVIDAD, "empezar()");

	     // Cancela cualquier hilo que trate de conectarse
	        if (cHiloConectar != null) {cHiloConectar.cancelar(); cHiloConectar = null;}

	     // Cancela cualquier hilo que esté conectado
	        if (cHiloConectado != null) {cHiloConectado.cancelar(); cHiloConectado = null;}
	        setEstado(ESTADO_CERO);
	    }
	    
	    /**
	     * conectar(BluetoothDevice dispositivo)
	     * Empieza hiloConectar para iniciar la conexion al dispositivo.
	     * @param dispositivo  El BluetoothDevice a conectar
	     * 
	     */
	    public synchronized void conectar(BluetoothDevice dispositivo) {
	        Log.d(ACTIVIDAD, "Conectar a: " + dispositivo);

	        // Cancela cualquier hilo que trate de conectarse
	        if (cEstado == ESTADO_CONECTANDO) {
	            if (cHiloConectar != null) {cHiloConectar.cancelar(); cHiloConectar = null;}
	        }

	        // Cancela cualquier hilo que esté conectado
	        if (cHiloConectado != null) {cHiloConectado.cancelar(); cHiloConectado = null;}

	        // Empieza hiloConectar para conectarse al dispositivo
	        cHiloConectar = new hiloConectar(dispositivo);
	        cHiloConectar.start();
	        setEstado(ESTADO_CONECTANDO);
	    }
	    
	    /**
	     * conectado(BluetoothSocket socket, BluetoothDevice dispositivo)
	     * Empieza hiloConectado para controlar la conexion
	     * @param socket  El socket donde se realizó la conexion
	     * @param dispositivo  El dispositivo al que se conectó
	     */
	    public synchronized void conectado(BluetoothSocket socket, BluetoothDevice dispositivo){
	        Log.d(ACTIVIDAD, "Conectado");

	        // Cancela el hilo que completo la conexion
	        if (cHiloConectar != null) {cHiloConectar.cancelar(); cHiloConectar = null;}

	        // Cancela cualquier hilo que esté conectado
	        if (cHiloConectado != null) {cHiloConectado.cancelar(); cHiloConectado = null;}

	        // Empieza el hilo para controlar la conexion y transmitir informacion
	        cHiloConectado = new hiloConectado(socket);
	        cHiloConectado.start();

	        setEstado(ESTADO_CONECTADO);
	    }
	    
	    /**
	     * 	parar()
	     * 	Para todos los hilos.
	     */
	    public synchronized void parar() {
	        Log.d(ACTIVIDAD, "parar()");

	        if (cHiloConectar != null) {
	            cHiloConectar.cancelar();
	            cHiloConectar = null;
	        }

	        if (cHiloConectado != null) {
	            cHiloConectado.cancelar();
	            cHiloConectado = null;
	        }
	        parar();
	        setEstado(ESTADO_CERO);
	    }
	    
	    /**
	     * escribir(byte[] salida)
	     * Escribe a hiloConectado de forma asincrona
	     * @param salida bytes a escribir
	     * @see ConnectedThread#write(byte[])
	     */
	    public void escribir(byte[] salida) {
	    	Log.i(ACTIVIDAD,"Escribir: "+salida);
	        // Crea un hilo temporal
	        hiloConectado hiloEscribir;
	        // Sincroniza el hilo temporal con cHiloConectado
	        synchronized (this) {
	            if (cEstado != ESTADO_CONECTADO) return;
	            hiloEscribir = cHiloConectado;
	        }
	        // Escribe de forma asíncrona
	        hiloEscribir.escribirHiloConectado(salida);
	    }

	    /**
	     * 	conexionPerdida()
	     * 	Indica que se perdió la conexion y notifica a Busquda.
	     */
	    private void conexionPerdida() {
	    	Log.i(ACTIVIDAD,"conexionPerdida()");
	    	// Manda el mensaje de conexion perdida a Busqueda
	    	cHandler.obtainMessage(Busqueda.MENSAJE_CONEXION_PERDIDA).sendToTarget();

	        // Empieza otra vez el servicio
	        Conexion.this.empezar();
	    }
	    
	    /**
	     * Este hilo corre mientras intenta conectarse a un dispositivo.
	     * Termina cuando falla o cuando tiene éxito
	     */
	    private class hiloConectar extends Thread {
	        private final BluetoothSocket socket;
	        private final BluetoothDevice dispositivo;

	        public hiloConectar(BluetoothDevice dispositivoArg) {
	        	Log.d(ACTIVIDAD, "Crear hiloConectar");
	            dispositivo = dispositivoArg;
	            BluetoothSocket tmp = null;

	            // Coge un socket para conectarse al Dispositivo
	            try {
	               tmp = dispositivoArg.createRfcommSocketToServiceRecord(bluetoothUuid);
	            } catch (IOException e) {
	                Log.e(ACTIVIDAD, "Fallo la creación del Socket", e);
	            }
	            socket = tmp; 
	        }

	        public void run() {
	            Log.i(ACTIVIDAD, "Empieza chiloConectar");
	            setName("ConnectThread");

	            // Siempre se cancela la busqueda de Dispositivo porque reduce la velocidad de la conexion
	            btAdapter.cancelDiscovery();

	            // Nos conectamos a través del socket
	            try {
	                socket.connect();
	            } catch (IOException e) {
	                // Cierra el socket por que ha fallado
	                try {
	                    socket.close();
	                } catch (IOException e2) {
	                    Log.e(ACTIVIDAD, "No se ha podido cerrar el socket despues del fallo de conexión", e2);
	                }
	                conexionPerdida();
	                return;
	            }

	            // Reiniciamos  hiloConectar porque hemos terminado
	            synchronized (Conexion.this) {
	                cHiloConectar = null;
	            }

	            // Empezamos hiloConectar
	            conectado(socket, dispositivo);
	        }

	        public void cancelar() {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                Log.e(ACTIVIDAD, "No se ha podido cerrar el socket en hiloConectar.cancelar()", e);
	            }
	        }
	    }
	    
	    /**
	     * Este hilo corre mientras haya conexion con el dispositvo.
	     * Se ocupa de las transmisiones entrantes y salientes.
	     */
	    private class hiloConectado extends Thread {
	        private final BluetoothSocket socket;
	        private final InputStream mmInStream;
	        private final OutputStream mmOutStream;

	        public hiloConectado(BluetoothSocket socketArg) {
	            Log.d(ACTIVIDAD, "Crear hiloConectado");
	            socket = socketArg;
	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;

	            // Cogemos los sockets input y output stream
	            try {
	                tmpIn = socketArg.getInputStream();
	                tmpOut = socketArg.getOutputStream();
	            } catch (IOException e) {
	                Log.e(ACTIVIDAD, "Sockets temporales no creados", e);
	            }

	            mmInStream = tmpIn;
	            mmOutStream = tmpOut;
	        }

	        public void run() {
	            Log.i(ACTIVIDAD, "Empieza cHiloConectado");
	            byte[] buffer = new byte[1024];
	            int bytes;
	            String cadenaBT="";
	            //Una vez conectado mandamos directamente la clave de paso antes de que la vuelva a pedir.
	            comparaCadena("alive$");//Nos aprovechamos de está función para mandar la clave de paso.
	            // Se mantiene escuchando mientras esté conectado
	            while (true) {
	            	Log.i(ACTIVIDAD,"escuchando bluetooth");
	                try {
	                	bytes = mmInStream.read(buffer);
	                    for(int i = 0; i < bytes; i++)
	                    {
	                        cadenaBT += (char)buffer[i];
	                        if(cadenaBT.substring(cadenaBT.length() - 1).equals("$")){
	                        	
	                        	comparaCadena(cadenaBT);
	                    
	                        	cadenaBT="";
		                    	break;
	                        }
	                    }
	                    
	                } catch (IOException e) {
	                    Log.e(ACTIVIDAD, "Desconectado cHiloConectado", e);
	                    conexionPerdida();
	                    // Empezamos el servicio de nuevo
	                    Conexion.this.empezar();
	                    break;
	                }
	            }
	        }

	        /**
	         * 	escribirHiloConectado(byte[] buffer)
	         *  Escribe al Dispositivo
	         *  @param buffer  Los bytes a escribir
	         */
	        public void escribirHiloConectado(byte[] buffer) {
	        	Log.i(ACTIVIDAD,"write()");
	        	try {
	                mmOutStream.write(buffer);
	            } catch (IOException e) {
	                Log.e(ACTIVIDAD, "Fallo al escribir", e);
	            }
	        }
	        
	        /**
	         *	cancelar()
	         *	Cancela la conexión cerrando el socket
	         **/
	        public void cancelar() {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                Log.e(ACTIVIDAD, "Fallo al cerrar socket en cHiloConectado.cancelar()", e);
	            }
	        }
	        
	        /**
	         *	comparaCadena(String cadena)
	         *	Compara la cadena recibida y actua en consecuencia
	         *	@param cadena (String) cadena a comparar
	         **/
	        private void comparaCadena(String cadena){
	        	if(cadena.equals("alive$")){
	        		
                    String numeroMovil = leerPreferencias("Numero");
                    
                    if(!numeroMovil.equals("false")){
                    	
                    	String numeroMensaje = "num";
                    	numeroMensaje += String.valueOf(numeroMovil.length());
                    	numeroMensaje += numeroMovil;
                    	numeroMensaje += "$";
                    	
                    	
                    	String mensajeMio= new String(codificar(numeroMovil));
                    	Log.i("CODIGOOOOOOOOOO",mensajeMio);
                    	try{
                    		cHiloConectado.escribirHiloConectado(mensajeMio.getBytes("UTF-8"));
                    	}catch(Exception e){
                    		Log.e("Error Conversión","getBytes(UTF-8)"+e);
                    	}
                    	
                    	Log.i("NUMERO","NUMERO: "+numeroMensaje);
            		
                    }
                    
	        	}else if(cadena.equals("OK$")){
	        			 String ok= "OK";
	        			 try{
	        				 byte[] bok = ok.getBytes("UTF-8");
	        				 cHandler.obtainMessage(Busqueda.MENSAJE_ESCRIBIR, bok.length, -1, bok).sendToTarget();
	        			 }catch(Exception e){
	        				 Log.e("Error Conversión","getBytes(UTF-8)"+e);
	        			 }
	        	
	        	}else if(cadena.length()>7){
	        			convierteGPS(cadena);
	        	}
	        }
	        /**
	         * */
	        private char[] codificar(String numero){
	        	char codif[] = new char[10];
	        	char[] arr2;
	        	codif[0]='n';
            	codif[1]='u';
            	codif[2]='m';
	        	int auxc1=0;
	        	int auxc2=0;
	        	for(int i=3;i<3+(numero.length()/3);i++){
	        		for(int j=0;j<3;j++){
	        			auxc1+=(int) numero.charAt(auxc2);
	        			auxc2++;
	        		}
	        	
	        		if(auxc1>126){auxc1=auxc1/2;}else if(auxc1<32){auxc1=auxc1*2;}
	        	
	        		codif[i]=(char) auxc1;
	        		auxc1=0;
	        	}
	        	if(numero.length()%3>0){
	        		for(int i=0;i<(numero.length()%3);i++){
	        			auxc1+=(int) numero.charAt(auxc2);
	        			auxc2++;
	        		}
	        	
	        		if(auxc1>126){auxc1=auxc1/2;}else if(auxc1<32){auxc1=auxc1*2;}
        		
	        		codif[3+(numero.length()/3)+(numero.length()%3)]=(char) auxc1;
	        		codif[4+(numero.length()/3)+(numero.length()%3)]= '$';
	        		int numArray= (5+(numero.length()/3)+(numero.length()%3));
	        		arr2 = Arrays.copyOf(codif, numArray);
	        	}else{
	        		codif[3+(numero.length()/3)]= '$';
	        		int numArray=(4+(numero.length()/3));
	        		arr2 = Arrays.copyOf(codif, numArray);
	        		
	        	}
	        	for(int i=0;i<10;i++){Log.i("Devuelve",String.valueOf((int)codif[i]));}
	        	Log.i("Resultado en String",String.valueOf(arr2));
	        	
	        	return arr2;
	        }
	        /**
	         *	leerPreferencias(String preferencia)
	         *	Lee las preferencias almacenadas
	         *	@param preferencia (String) que se leerá
	         *	@return cadena (String) con la preferencia 
	         **/
	        private String leerPreferencias(String preferencia){
	        	String cadena="";
	        	SharedPreferences preferencias = MainActivity.appContext.getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
	            cadena = preferencias.getString(preferencia, "false"); // Cogemos la preferencia ejecucion, si está vacia devuelve false.
	            
	        	return cadena;
	        }

		    /**
		     *	convierteGPS(String cadena)
		     *	Convierte las coordenadas GPS
		     *	@param cadena String que se convertirá
		     *
		     **/
		    public void convierteGPS(String cadena){
		    	Log.i(ACTIVIDAD,"convierteGPS()");
		    	if(cadena.substring(cadena.length() - 2).equals("N$")){
		    		Log.i("convierteGPS","Cadena norte");
		    		String aux="";
		    		for (int c = 0; c < cadena.length()-2; c++) {
	                    aux +=(char) cadena.charAt(c);
		    		}
		    		norte = Double.valueOf(aux);
		    		if(norte>-90||norte<90){
		    			latitud =true;
		    		}else{
		    			latitud=false;
		    		}
		    		Log.i("Coordenadas norte",String.valueOf(norte));
		    	
		    	}else if(cadena.substring(cadena.length() - 2).equals("E$")){
		    		Log.i("convierteGPS","Cadena este");
		    		String aux3="";
		    		Log.v("Numero de caracteres",String.valueOf(cadena.length()));
		    		for (int d = 0; d < cadena.length()-2; d++) {
		    			aux3 +=(char) cadena.charAt(d);
		    		}
		    		este = Double.valueOf(aux3);
		    		// Primero tiene que llegar la información norte, esto evita problemas de solapamiento.
		    		if(!latitud ||este<-180||este>180){
		    			longitud=false;
		    		}else{
		    			longitud = true;
		    		}
		    		Log.i("Coordenadas este",String.valueOf(este));
		    	}
		    	if(latitud && longitud){
		    		latitud = false;
		    		longitud = false;
		    		Thread i = new GuardarGPS(norte,este);
		    		i.start();
		    	}
		    }
	    }
	    
}
