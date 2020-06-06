package br.com.fitcareplus;

import com.parse.livequery.ParseLiveQueryClient;

import java.net.URI;
import java.net.URISyntaxException;

public class SharedConnection {
    private ParseLiveQueryClient connection;

    public ParseLiveQueryClient get(){
        //Existe conexão?
//        if(!connection){
//            connection = null;
        try {
            connection = ParseLiveQueryClient.Factory.getClient(new URI("wss://fitcareplus.back4app.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public ParseLiveQueryClient close(){
//        if(connection){
//
//        }
        connection = null;
        return connection;
    }
}
