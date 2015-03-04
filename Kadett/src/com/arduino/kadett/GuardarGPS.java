package com.arduino.kadett;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

public class GuardarGPS extends Thread{

	private double latitudG;
	private double longitudG;
	
	public GuardarGPS(double arg1, double arg2){
		latitudG	 = arg1;
		longitudG = arg2;
	}
	
	public void run(){
		Looper.prepare();
		
		//Borra la base de datos
		//MainActivity.appContext.deleteDatabase("RegistroGps");
		
		//Guardamos los datos recibidos
		guardarDatos(latitudG,longitudG);
		
		//Contamos la filas existentes en la BBDD, si hay más de 10 eliminamos 5.
		if(contarDatos()>10){
			borrarDatos(5);
		}
		        
        // Elimina la tabla si existe
        //db.execSQL("DROP TABLE IF EXISTS Posicion");
        
        
        }
	
	/**
	 * Guarda la longitud, latitud, fecha y hora.
	 * 
	 * @param latitud (Double) la latitud de la localización
	 * @param longitud (Double) la longitud de la localización
	 * 
	 **/
	public void guardarDatos(Double latitud, Double longitud){
		//Abrimos la base de datos 'DBUsuarios' en modo escritura 
        ControladorBaseDatos midb = new ControladorBaseDatos(MainActivity.appContext,"GPS.db",null);
        SQLiteDatabase bd = midb.getWritableDatabase();
        
      //Si hemos abierto correctamente la base de datos
        if(bd != null){
        	//Generamos los datos
            
            Long tiempo = System.currentTimeMillis()/1000L;
            int aux = Integer.valueOf(tiempo.intValue());
            //Insertamos los datos en la tabla Usuarios
            bd.execSQL("INSERT INTO "+ControladorBaseDatos.NOMBRE_TABLA+" (tiempo, latitud, longitud) " +
                           "VALUES ('"+ aux +"', '"+ latitud +"', '"+ longitud +"')");
             
            //Cerramos la base de datos
        	bd.close();
        }
	}
	
	/**
	 * Borra los datos cuando hay más de 5.
	 * Esto se hace para evitar un crecimiento desmesurado del tamaño de la base de datos. 
	 * Ya que sólo se necesita la última posición.
	 * 
	 * @param limite (int) Número máximo de columnas a dejar
	 **/
	public void borrarDatos(int limite){
		//Abrimos la base de datos 'DBUsuarios' en modo escritura 
        ControladorBaseDatos midb = new ControladorBaseDatos(MainActivity.appContext,"GPS.db",null);
        SQLiteDatabase bd = midb.getWritableDatabase();
        
        //Si hemos abierto correctamente la base de datos
        if(bd != null){
        	bd.execSQL("delete from " + ControladorBaseDatos.NOMBRE_TABLA +
						" where "+ControladorBaseDatos.ID+" in (select "+
						ControladorBaseDatos.ID +" from "+ ControladorBaseDatos.NOMBRE_TABLA+" order by _id LIMIT "+limite+")");

        }
        //Cerramos la base de datos
    	bd.close();
	}
	
	
	/**
	 * Cuenta la cantidad de filas existentes en la tabla de la BBDD
	 * 
	 * @return numero (int) Número de filas existentes.
	 **/
	public int contarDatos(){
		int numero=0;
		//Abrimos la base de datos 'DBUsuarios' en modo lectura 
        ControladorBaseDatos midb = new ControladorBaseDatos(MainActivity.appContext,"GPS.db",null);
        SQLiteDatabase bd = midb.getReadableDatabase();
        
        //Si hemos abierto correctamente la base de datos
        if(bd != null){
        	//Contamos las filas existentes
        	Cursor numFilas=bd.rawQuery("SELECT COUNT(*) FROM "+ControladorBaseDatos.NOMBRE_TABLA,null);
         	numFilas.moveToFirst();
         	numero= numFilas.getInt(0);
         	//Cerramos la base de datos
        	bd.close();
        }
		return numero;
	}

}

        
