package br.com.fitcareplus.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import br.com.fitcareplus.Interfaces.BluetoothConnectionServiceInterface;
import br.com.fitcareplus.factory.MessageHandlerFactory;
import br.com.fitcareplus.service.BluetoothConnectionService;

public class ConnectedThread extends Thread{

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final MessageHandlerFactory mmHandlerFactory;
    private Boolean ready = false;
    private BluetoothConnectionServiceInterface mmServiceInterface;

    public ConnectedThread(BluetoothSocket socket, Handler handler, BluetoothConnectionServiceInterface serviceInterface){
        mmSocket = socket;
        mmHandlerFactory = new MessageHandlerFactory(handler);

        mmServiceInterface = serviceInterface;

        InputStream tmpInStream = null;
        OutputStream tmpOutStream = null;

        try {
            tmpInStream = socket.getInputStream();
            tmpOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            serviceInterface.onConnectionError();
        }

        mmInStream = tmpInStream;
        mmOutStream = tmpOutStream;

        try {
            mmOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serviceInterface.onConnectionError();
        }
    }

    public void run(){
        BufferedReader buffer = new BufferedReader(new InputStreamReader(mmInStream));

        while(true){
            try {
                mmHandlerFactory.sendMessage(BluetoothConnectionService.INCOMING_MESSAGE, buffer.readLine());
                notifyDeviceIsReady();
            } catch(IOException e){
                e.printStackTrace();
                break;
            }
        }
    }

    private void notifyDeviceIsReady() {
        if(!ready) {
            ready = true;
            mmServiceInterface.onDeviceReady();
        }
    }

    public void write(byte[] bytes){
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
