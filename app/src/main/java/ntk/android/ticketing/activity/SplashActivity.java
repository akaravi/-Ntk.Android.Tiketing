package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import io.sentry.Sentry;
import ntk.android.base.activity.common.BaseSplashActivity;

public class SplashActivity extends BaseSplashActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Sentry.captureMessage("testing SDK setup");
    }
}
