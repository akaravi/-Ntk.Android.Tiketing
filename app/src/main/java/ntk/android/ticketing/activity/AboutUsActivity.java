package ntk.android.ticketing.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.ticketing.BuildConfig;
import ntk.android.ticketing.R;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.ticketing.library.about.AboutPage;
import ntk.android.ticketing.library.about.Element;
import ntk.android.base.api.core.interfase.ICore;
import ntk.android.base.api.core.model.CoreAboutUsResponse;
import ntk.android.base.config.RetrofitManager;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        ICore iAbout = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ICore.class);
        Observable<CoreAboutUsResponse> about = iAbout.GetAbout(new ConfigRestHeader().GetHeaders(this));
        about.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CoreAboutUsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CoreAboutUsResponse about) {
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
                                    .addItem(new Element().setTitle(" "+BuildConfig.VERSION_NAME+" ver"))
                                    .create();
                            setContentView(aboutPage);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(AboutUsActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
