package com.hikimori911.mylastfmclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hikimori911.mylastfmclient.data.pojo.AppPreferenceHelper;
import com.hikimori911.mylastfmclient.data.pojo.GetSessionObject;
import com.hikimori911.mylastfmclient.data.pojo.Session;
import com.hikimori911.mylastfmclient.network.RestClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class StartActivity extends Activity{

    public static final String USERNAME_KEY = "USERNAME_KEY";
    public static final String PASSWORD_KEY = "PASSWORD_KEY";

    protected EditText mUserNameET;
    protected EditText mPasswordET;
    protected Button mLoginBtn;

    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if(AppPreferenceHelper.getAuthToken(this)!=null &&
                AppPreferenceHelper.getUserName(this)!=null ){
            Intent intent = new Intent(StartActivity.this,DashbordActivity.class);
            startActivity(intent);
            finish();
        }


        mUserNameET = (EditText)findViewById(R.id.username);
        mPasswordET = (EditText)findViewById(R.id.password);

        mLoginBtn = (Button)findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordET.getText().toString();
                String userName = mUserNameET.getText().toString();
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
                                Toast.makeText(StartActivity.this,error.getBody().toString(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void success(GetSessionObject data, Response response) {
                                dialog.dismiss();
                                if(data.error==0) {
                                    Session session = (Session) data.session;
                                    AppPreferenceHelper.saveAuthToken(StartActivity.this,session.key);
                                    AppPreferenceHelper.saveUserName(StartActivity.this,session.name);
                                    Intent intent = new Intent(StartActivity.this,DashbordActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(StartActivity.this,data.message,Toast.LENGTH_SHORT).show();
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
