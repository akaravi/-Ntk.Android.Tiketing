package ntk.android.ticketing.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import ntk.android.ticketing.BuildConfig;

/**
 * Created by Mehrdad Safari on 18-Jan-17.
 */

public class RegisterIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID);
        Log.i("Token", FirebaseInstanceId.getInstance().getToken());
    }
}
