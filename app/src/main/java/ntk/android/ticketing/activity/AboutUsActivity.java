package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.application.AboutUsDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.ticketing.BuildConfig;
import ntk.android.ticketing.R;
import ntk.android.ticketing.library.about.AboutPage;
import ntk.android.ticketing.library.about.Element;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        new ApplicationAppService(this).getAboutUs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NtkObserver<ErrorException<AboutUsDtoModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<AboutUsDtoModel> about) {
                        if (about != null) {
                            View aboutPage = new AboutPage(AboutUsActivity.this)
                                    .isRTL(true)
                                    .setImage(R.mipmap.ic_launcher)

                                    .setDescription(about.Item.Content)
                                    .addGroup("آدرس")
                                    .addItem(new Element().setTitle(about.Item.Address))
                                    .addGroup("تماس با ما")
                                    .addPhone(about.Item.MobileNo, about.Item.TitleMobileNo)
                                    .addPhone(about.Item.OfficeNo, about.Item.TitleOfficeNo)
                                    .addPhone(about.Item.Tel1, about.Item.TitleTel1)
                                    .addPhone(about.Item.Tel2, about.Item.TitleTel2)
                                    .addEmail(about.Item.Email, about.Item.TitleEmail)
                                    .addInstagram(about.Item.Instagram, about.Item.TitleInstagram)
                                    .addWebsite(about.Item.WebUrl, about.Item.TitleWebUrl)
                                    .addTelegram(about.Item.Telegram, about.Item.TitleTelegram)
                                    .addItem(new Element().setTitle(" " + BuildConfig.VERSION_NAME + " ver"))
                                    .create();
                            setContentView(aboutPage);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toasty.warning(AboutUsActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }
}
