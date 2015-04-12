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
import com.hikimori911.mylastfmclient.data.pojo.LFMSession;
import com.hikimori911.mylastfmclient.data.pojo.GetSessionObject;
import com.hikimori911.mylastfmclient.network.RestClient;
import com.hikimori911.mylastfmclient.sync.LastFMAuthenticator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            AccountManagerFuture<Bundle> future = null;
            for(int i=0;i<accounts.length;i++){
                if(accounts[i].name!=null && accounts[i].name.equals(userName)){
                    try {
                        future = mAccountManager.getAuthToken(accounts[i],
                                LastFMAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, null, this, null, null);
                        Bundle bnd = future.getResult();
                        final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        if(authtoken!=null && !authtoken.isEmpty()) {
                            Intent intent = new Intent(StartActivity.this, DashbordActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                    }
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
                                android.util.Log.i("example", "Error, body: " + error.getBody().toString());
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),error.getBody().toString(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void success(GetSessionObject data, Response response) {
                                dialog.dismiss();
                                if(data.error==0) {
                                    LFMSession LFMSession = (LFMSession) data.LFMSession;

                                    if(LFMSession !=null) {
                                        AppPreferenceHelper.saveUserName(getApplicationContext(), LFMSession.name);
                                        final Account account = new Account(LFMSession.name, LastFMAuthenticator.ACCOUNT_TYPE);
                                        mAccountManager.addAccountExplicitly(account, password, null);
                                        mAccountManager.setAuthToken(account, LastFMAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, LFMSession.key);

                                        Intent intent = new Intent(StartActivity.this, DashbordActivity.class);
                                        startActivity(intent);
                                        finish();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
}
