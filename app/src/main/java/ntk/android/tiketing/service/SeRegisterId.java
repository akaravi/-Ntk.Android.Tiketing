package ntk.android.tiketing.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import ntk.android.tiketing.BuildConfig;

/**
 * Created by Mehrdad Safari on 18-Jan-17.
 */

public class SeRegisterId extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID);
        Log.i("Token", FirebaseInstanceId.getInstance().getToken());
    }
}
