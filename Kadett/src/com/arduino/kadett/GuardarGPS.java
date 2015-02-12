package com.arduino.kadett;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

public class GuardarGPS extends Thread{

	private double latitud;
	private double longitud;
	
	public GuardarGPS(double arg1, double arg2){
		latitud	 = arg1;
		longitud = arg2;
	}
	
	public void run(){
		Looper.prepare();
		
		//Borra la base de datos
		//MainActivity.appContext.deleteDatabase("RegistroGps");
		
		//Guardamos los datos recibidos
		guardarDatos();
		
		//Contamos la filas existentes en la BBDD, si hay más de 10 eliminamos 5.
		if(contarDatos()>10){
			borrarDatos();
		}
		        
        // Elimina la tabla si existe
        //db.execSQL("DROP TABLE IF EXISTS Posicion");
        
        
        }
	
	/**
	 * guardarDatos()
	 * 
	 **/
	public void guardarDatos(){
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
	 * borrarDatos() 
	 **/
	public void borrarDatos(){
		//Abrimos la base de datos 'DBUsuarios' en modo escritura 
        ControladorBaseDatos midb = new ControladorBaseDatos(MainActivity.appContext,"GPS.db",null);
        SQLiteDatabase bd = midb.getWritableDatabase();
        
        //Si hemos abierto correctamente la base de datos
        if(bd != null){
        	bd.execSQL("delete from " + ControladorBaseDatos.NOMBRE_TABLA +
						" where "+ControladorBaseDatos.ID+" in (select "+
						ControladorBaseDatos.ID +" from "+ ControladorBaseDatos.NOMBRE_TABLA+" order by _id LIMIT 5)");

        }
        //Cerramos la base de datos
    	bd.close();
	}
	
	
	/**
	 * contarDatos()
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

        
