package com.hikimori911.mylastfmclient.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.hikimori911.mylastfmclient.R;
import com.hikimori911.mylastfmclient.data.AppPreferenceHelper;
import com.hikimori911.mylastfmclient.data.pojo.GetRecentTracks;
import com.hikimori911.mylastfmclient.data.pojo.GetUserEvents;
import com.hikimori911.mylastfmclient.network.RestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LastFMSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = LastFMSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    protected static Account mAccount;
    protected Context ctx;

    public LastFMSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        ctx = context;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        String apiKey = getContext().getResources().getString(R.string.lastfm_api_key);
        RestClient.get().getUserEvents(account.name,
                10,
                apiKey,
                new Callback<GetUserEvents>() {
                    @Override
                    public void failure(final RetrofitError error) {
                        android.util.Log.i(LOG_TAG, "Error, body: " + error.getBody().toString());
                    }

                    @Override
                    public void success(GetUserEvents data, Response response) {
                        android.util.Log.d(LOG_TAG, "recent events: success");
                    }
                }
        );
        RestClient.get().getUserRecentTracks(account.name,
                10,
                apiKey,
                new Callback<GetRecentTracks>() {
                    @Override
                    public void failure(final RetrofitError error) {
                        android.util.Log.i(LOG_TAG, "Error, body: " + error.getBody().toString());
                    }

                    @Override
                    public void success(GetRecentTracks data, Response response) {
                        android.util.Log.d(LOG_TAG, "recent tracks: success");
                    }
                }
        );
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        if(mAccount==null) {
            // Get an instance of the Android account manager
            AccountManager mAccountManager = AccountManager.get(context);
            Account[] accounts = mAccountManager.getAccountsByType(LastFMAuthenticator.ACCOUNT_TYPE);
            String userName = AppPreferenceHelper.getUserName(context);
            if (accounts.length > 0 && userName != null) {
                AccountManagerFuture<Bundle> future = null;
                for (int i = 0; i < accounts.length; i++) {
                    if (accounts[i].name != null && accounts[i].name.equals(userName)) {
                        mAccount = accounts[i];
                        LastFMSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
                        ContentResolver.setSyncAutomatically(accounts[i], context.getString(R.string.content_authority), true);
                        syncImmediately(context);
                        break;
                    }
                }
            }
        }
        return mAccount;
    }
}