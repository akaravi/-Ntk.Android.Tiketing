package ntk.android.ticketing;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import es.dmoral.toasty.Toasty;
import ntk.android.base.ApplicationParameter;
import ntk.android.base.ApplicationStaticParameter;
import ntk.android.base.ApplicationStyle;
import ntk.android.base.NTKApplication;
import ntk.android.base.utill.FontManager;
//import ntk.android.base.view.ViewController;
import ntk.android.base.view.ViewController;
import ntk.android.ticketing.activity.MainActivity;
import ntk.android.ticketing.activity.MainActivity3;
import ntk.android.ticketing.activity.MainActivity_2;

public class MyApplication extends NTKApplication {

    public static boolean Inbox = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DEBUG = true;
        if (!new File(getCacheDir(), "image").exists()) {
            new File(getCacheDir(), "image").mkdirs();
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(new File(getCacheDir(), "image")))
                .diskCacheFileNameGenerator(imageUri -> {
                    String[] Url = imageUri.split("/");
                    return Url[Url.length];
                })
                .build();
        ImageLoader.getInstance().init(config);

        Toasty.Config.getInstance()
                .setToastTypeface(FontManager.T1_Typeface(getApplicationContext()))
                .setTextSize(14).apply();
        applicationStyle = new ApplicationStyle() {
            @Override
            public ViewController getViewController() {
                ViewController vc = new ViewController() {
                };
                vc.setLoading_view(R.layout.app_base_loading)
                        .setEmpty_view(R.layout.app_base_empty)
                        .setError_view(R.layout.app_base_error)
                        .setError_button(R.id.btn_error_tryAgain)
                        .setError_label(R.id.tvError);
                return vc;
            }

            @Override
            public Class<?> getMainActivity() {
                return MainActivity.class;
            }

        };
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    protected ApplicationStaticParameter getConfig() {
        ApplicationStaticParameter applicationStaticParameter = new ApplicationStaticParameter();
//        applicationStaticParameter.URL = "https://b90d8887a5d8.ngrok.io/";
        ApplicationStaticParameter.PACKAGE_NAME="ntk.android.ticketing.app109";
        return applicationStaticParameter;
    }

    @Override
    public ApplicationParameter getApplicationParameter() {
        return new ApplicationParameter(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }
}
