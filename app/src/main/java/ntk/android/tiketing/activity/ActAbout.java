package ntk.android.tiketing.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.tiketing.BuildConfig;
import ntk.android.tiketing.R;
import ntk.android.tiketing.config.ConfigRestHeader;
import ntk.android.tiketing.config.ConfigStaticValue;
import ntk.android.tiketing.library.about.AboutPage;
import ntk.android.tiketing.library.about.Element;
import ntk.base.api.core.interfase.ICore;
import ntk.base.api.core.model.CoreAboutUsResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActAbout extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        RetrofitManager manager = new RetrofitManager(this);
        ICore iAbout = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ICore.class);
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
                            View aboutPage = new AboutPage(ActAbout.this)
                                    .isRTL(true)
                                    .setImage(R.mipmap.ic_launcher)
                                    .addItem(new Element().setTitle(BuildConfig.VERSION_NAME))
                                    .addGroup("تماس با ما")
                                    .setDescription(about.Content)
                                    .addEmail(about.Email)
                                    .addInstagram(about.Instagram)
                                    .create();
                            setContentView(aboutPage);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(ActAbout.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                    }
                    @Override
                    public void onComplete() {

                    }
                });
    }
}
