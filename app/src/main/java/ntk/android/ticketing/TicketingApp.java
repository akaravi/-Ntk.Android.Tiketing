package ntk.android.ticketing;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import es.dmoral.toasty.Toasty;
import ntk.android.base.ApplicationParameter;
import ntk.android.base.ApplicationStyle;
import ntk.android.base.NTKApplication;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.activity.MainActivity;

public class TicketingApp extends NTKApplication {

    public static boolean Inbox = false;

    @Override
    public void onCreate() {
        super.onCreate();
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
                .setToastTypeface(FontManager.GetTypeface(getApplicationContext(), FontManager.IranSans))
                .setTextSize(14).apply();
        applicationStyle = new ApplicationStyle() {
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
    public ApplicationParameter getApplicationParameter() {
        return new ApplicationParameter(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }
}
