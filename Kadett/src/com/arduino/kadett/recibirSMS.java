package com.arduino.kadett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

public class recibirSMS extends BroadcastReceiver{
	
    private static final String RECIBIDO = "android.provider.Telephony.SMS_RECEIVED";
    

    /**
     * 
     * Recibe los mensajes y los desgrana para a continuación enviarlo a otro proceso
     * @param contexto (Context) El contexto sobre el cual se ejecutará
     * @param intento (Intent) 
     * */
    public void onReceive(Context contexto, Intent intento) {
    	
    	this.abortBroadcast();
        
    	String cuerpoSMS = "";
        String numeroSMS = "";
        
        String action = intento.getAction();

        if(action.equals(RECIBIDO)){

            SmsMessage[] msgs = obtenerSMS(intento);
            if (msgs != null) {
                for (int i = 0; i < msgs.length; i++) {
                    numeroSMS = msgs[i].getOriginatingAddress();
                    cuerpoSMS += msgs[i].getMessageBody().toString();
                    cuerpoSMS += "\n";
                }
            }   

            
            Intent intent = new Intent("MENSAJE_RECIBIDO");
            intent.putExtra("numero",numeroSMS);
            intent.putExtra("cuerpo", cuerpoSMS);
            LocalBroadcastManager.getInstance(contexto).sendBroadcast(intent);
        }

    }

    /**
     * Recorre el SMS para leer todos los datos y los devuelve en forma de SmsMessage[]
     * @param intent (Intent)
     * @return SMS (SmsMessage[]) Devuelve el mensaje organizado
     * */
    public static SmsMessage[] obtenerSMS(Intent intent) {
        Object[] mensajes = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] PDUS = new byte[mensajes.length][];

        for (int i = 0; i < mensajes.length; i++) {
            PDUS[i] = (byte[]) mensajes[i];
        }
        byte[][] pdus = new byte[PDUS.length][];
        int longitudPDU = pdus.length;
        SmsMessage[] SMS = new SmsMessage[longitudPDU];
        for (int i = 0; i < longitudPDU; i++) {
            pdus[i] = PDUS[i];
            SMS[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return SMS;
    }
}
