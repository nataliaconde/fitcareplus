package br.com.fitcareplus.factory;

import android.os.Handler;
import android.os.Message;

public class MessageHandlerFactory {

    private Handler mmHandler;

    public MessageHandlerFactory(Handler handler) {
        mmHandler = handler;
    }

    public void sendMessage(int code, Object value) {
        Message message = new Message();
        message.what = code;
        message.obj = value;
        mmHandler.sendMessage(message);
    }
}
