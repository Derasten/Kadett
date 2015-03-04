package com.arduino.kadett;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ControladorBaseDatos extends SQLiteOpenHelper{

	public static final String NOMBRE_TABLA = "registroGps";
	public static final String NOMBRE_BASE_DATOS = "GPS.db";
	private static int VERSION_BASE_DATOS = 1;
	
	public static final int ID = 0;
	public static final int TIEMPO = 1;
	public static final int LATITUD = 2;
	public static final int LONGITUD = 3 ;
	
	public ControladorBaseDatos(Context context, String nombre, SQLiteDatabase.CursorFactory factory){
		super(context, nombre, factory, VERSION_BASE_DATOS);
	}
	
	@Override
	public void onCreate(SQLiteDatabase base_datos){
		crearTabla(base_datos);
	}
	
	/**
	 * Crea la tabla en la BB.DD que utilizaremos para guardar la localización del dispositivo Arduino
	 * 
	 * @param base_datos (SQLiteDatabase) la BB.DD donde se creará la tabla
	 * 
	 * */
	public void crearTabla(SQLiteDatabase base_datos){
		String linea = "CREATE TABLE " + NOMBRE_TABLA + 
				   " ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "TIEMPO INTEGER, " + 
				   "LATITUD REAL, " + 
				   "LONGITUD REAL);";
		base_datos.execSQL(linea);
	}
	
	/**
	 * Cuando se actualize la versión de la app, borra la tabla y crea una nueva
	 * 
	 * @param base_datos (SQLiteDatabase) La base de datos de la que se borrará la tabla
	 * @param version_v (int) La versión vieja
	 * @param version_n (int) La versión nueva
	 * 
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase base_datos, int version_v, int version_n){
		base_datos.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA + ";");
		crearTabla(base_datos);
	}
	
}
