package ntk.android.ticketing.activity;

import android.view.ViewStub;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ntk.android.ticketing.R;
import ntk.android.ticketing.utill.FontManager;
import ntk.android.ticketing.view.swicherview.Switcher;

public abstract class BaseActivity extends AppCompatActivity {
    protected boolean usedSwicher = true;
    protected Switcher switcher;

    protected Button btnTryAgain;

    @Override
    protected void onStart() {
        super.onStart();
        initBase();
    }

    protected void initBase() {
    }

    @Override
    public void setContentView(int layoutResID) {
        if (!usedSwicher) {
            super.setContentView(layoutResID);
        } else {
            super.setContentView(R.layout.act_base);
            ViewStub activity = (ViewStub) findViewById(R.id.activity_stub);
            activity.setLayoutResource(layoutResID);
            activity.inflate();
            Switcher.Builder builder = new Switcher.Builder(this);
            builder.addEmptyView(findViewById(R.id.activity_BaseError))
                    .addProgressView(findViewById(R.id.activity_BaseLoading))
                    .addContentView(activity)
                    .addErrorView(findViewById(R.id.activity_BaseError)).setErrorLabel((R.id.tvError))
                    .addProgressView(findViewById(R.id.sub_loading));
            switcher = builder.build();
            btnTryAgain = findViewById(R.id.btnTryAgain);
            btnTryAgain.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
    }
}
