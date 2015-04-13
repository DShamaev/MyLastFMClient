package com.hikimori911.mylastfmclient.appinterface;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hikimori911.mylastfmclient.R;
import com.hikimori911.mylastfmclient.data.AppPreferenceHelper;
import com.hikimori911.mylastfmclient.data.pojo.GetSessionObject;
import com.hikimori911.mylastfmclient.data.pojo.GetUserInfoObject;
import com.hikimori911.mylastfmclient.data.pojo.LFMImage;
import com.hikimori911.mylastfmclient.data.pojo.LFMSession;
import com.hikimori911.mylastfmclient.data.pojo.LFMUser;
import com.hikimori911.mylastfmclient.network.RestClient;
import com.hikimori911.mylastfmclient.sync.LastFMAuthenticator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class StartActivity extends AccountAuthenticatorActivity {

    private String TAG = this.getClass().getSimpleName();

    public static final String USERNAME_KEY = "USERNAME_KEY";
    public static final String PASSWORD_KEY = "PASSWORD_KEY";

    protected EditText mUserNameET;
    protected EditText mPasswordET;
    protected Button mLoginBtn;

    protected ProgressDialog dialog;

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(LastFMAuthenticator.ACCOUNT_TYPE);
        String userName = AppPreferenceHelper.getUserName(getApplicationContext());
        if(accounts.length>0 && userName!= null){
            for(int i=0;i<accounts.length;i++){
                if(accounts[i].name!=null && accounts[i].name.equals(userName)){
                    final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(accounts[i],
                            LastFMAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, null, this, null, null);
                    dialog = ProgressDialog.show(StartActivity.this, "Loading", "Authenticate...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bundle bnd = future.getResult();
                                final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                                EventBus.getDefault().post(new AuthEvent(authtoken));
                            } catch (Exception e) {
                                e.printStackTrace();
                                EventBus.getDefault().post(new AuthEvent(null));
                            }
                        }
                    }).start();
                    break;
                }
            }
        }

        mUserNameET = (EditText)findViewById(R.id.username);
        mPasswordET = (EditText)findViewById(R.id.password);

        mLoginBtn = (Button)findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = mPasswordET.getText().toString();
                final String userName = mUserNameET.getText().toString();
                if(userName.isEmpty() || password.isEmpty()){
                    Toast.makeText(StartActivity.this, getResources().getString(R.string.start_login_empty_fields_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog = ProgressDialog.show(StartActivity.this, "Loading", "Wait while loading...");

                String apiKey = getResources().getString(R.string.lastfm_api_key);
                String signature = new StringBuilder("api_key")
                        .append(apiKey)
                        .append("methodauth.getMobileSessionpassword")
                        .append(password)
                        .append("username")
                        .append(userName)
                        .append(getResources().getString(R.string.lastfm_api_secret)).toString();
                RestClient.get().getUserToken(apiKey,
                        md5(signature),
                        password,
                        userName,
                        new Callback<GetSessionObject>() {
                            @Override
                            public void failure(final RetrofitError error) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void success(GetSessionObject data, Response response) {
                                if(data.error==0) {
                                    LFMSession session = (LFMSession) data.session;
                                    if(session !=null) {
                                        EventBus.getDefault().post(new ExternalAuthEvent(session));
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),data.message,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

         return super.onOptionsItemSelected(item);
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ExternalAuthEvent event){
        final String password = mPasswordET.getText().toString();
        LFMSession session = event.session;
        AppPreferenceHelper.saveUserName(this, session.name);
        final Account account = new Account(session.name, LastFMAuthenticator.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(account, password, null);
        mAccountManager.setAuthToken(account, LastFMAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, session.key);
        showDashboard();
    }

    public void onEvent(AuthEvent event){
        String authtoken = event.token;
        if (authtoken != null && !authtoken.isEmpty()) {
            showDashboard();
        }
    }

    public void onEvent(UserInfoEvent event){
        dialog.dismiss();
        launchDashboard();
    }

    public void showDashboard(){
        if(AppPreferenceHelper.getUserAvatarURL(getApplicationContext())==null) {
            RestClient.get().getUserInfo(AppPreferenceHelper.getUserName(getApplicationContext()),
                    getResources().getString(R.string.lastfm_api_key),
                    new Callback<GetUserInfoObject>() {
                        @Override
                        public void failure(final RetrofitError error) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void success(GetUserInfoObject data, Response response) {
                            if (data.error == 0) {
                                LFMUser user = (LFMUser) data.user;

                                if (user != null) {
                                    LFMImage image = user.image[0];
                                    if(image!=null) {
                                        AppPreferenceHelper.saveUserAvatarURL(getApplicationContext(), image.text);
                                    }
                                    EventBus.getDefault().post(new UserInfoEvent());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), data.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }else {
            launchDashboard();
        }
    }

    public void launchDashboard(){
        dialog.dismiss();
        Intent intent = new Intent(StartActivity.this, DashbordActivity.class);
        startActivity(intent);
        finish();
    }

    public class ExternalAuthEvent {
        public final LFMSession session;
        public ExternalAuthEvent(LFMSession session) {
            this.session = session;
        }
    }

    public class AuthEvent {
        public final String token;
        public AuthEvent(String token) {
            this.token = token;
        }
    }

    public class UserInfoEvent {
        public UserInfoEvent() {
        }
    }
}
