package com.hikimori911.mylastfmclient.network;

import android.app.IntentService;
import android.content.Intent;

import com.hikimori911.mylastfmclient.StartActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkService extends IntentService {

    public interface ResponseReceiver{
        public void onRequestSuccess(Object data,int error,String message);
        public void onRequestFailed(String message);
    }



    public static final String RECIEVER = "RECIEVER";
    public static final String GET_SESSION = "GET_SESSION";

    public NetworkService() {
        super("NetworkService");
    }
    private ResponseReceiver receiver;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if(intent.getAction()!= null && intent.getAction().equals(GET_SESSION)) {
                String userName = intent.getStringExtra(StartActivity.USERNAME_KEY);
                String password = intent.getStringExtra(StartActivity.PASSWORD_KEY);
                this.receiver = (ResponseReceiver) intent.getParcelableExtra(RECIEVER);

            }
        }
    }


}
