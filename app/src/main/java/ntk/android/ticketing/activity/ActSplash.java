package ntk.android.ticketing.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.ticketing.BuildConfig;
import ntk.android.ticketing.R;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.EasyPreference;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.core.entity.CoreMain;
import ntk.base.api.core.entity.CoreTheme;
import ntk.base.api.core.interfase.ICore;
import ntk.base.api.core.model.MainCoreResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActSplash extends BaseActivity {


    @BindView(R.id.lblVersionActSplash)
    TextView Lbl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("نسخه  " + (int) Float.parseFloat(BuildConfig.VERSION_NAME) + "." + BuildConfig.VERSION_CODE);
        btnTryAgain.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    /**
     * get all needed data
     */
    private void getData() {
        if (AppUtill.isNetworkAvailable(this)) {
            getThemeData();

        } else {
            switcher.showErrorView();
        }
    }

    /**
     * get theme from server
     */
    private void getThemeData() {

        RetrofitManager manager = new RetrofitManager(this);
        ICore iCore = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ICore.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<CoreTheme> call = iCore.GetThemeCore(headers);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CoreTheme>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CoreTheme theme) {
                        EasyPreference.with(ActSplash.this).addString("Theme", new Gson().toJson(theme.Item.ThemeConfigJson));
                        //now can get main response
                        requestMainData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        switcher.showErrorView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * req main data
     */
    private void requestMainData() {
        RetrofitManager manager = new RetrofitManager(this);
        ICore iCore = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ICore.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<MainCoreResponse> observable = iCore.GetResponseMain(headers);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MainCoreResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MainCoreResponse mainCoreResponse) {
                        if (!mainCoreResponse.IsSuccess) {
//                                Loading.cancelAnimation();
//                                Loading.setVisibility(View.GONE);
                            btnTryAgain.setVisibility(View.VISIBLE);
                            //replace with layout
                            Toasty.warning(ActSplash.this, mainCoreResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            return;

                        }
                        HandelDataAction(mainCoreResponse.Item);

                    }


                    @Override
                    public void onError(Throwable e) {
                        //replace with layout
//                            Loading.cancelAnimation();
//                            Loading.setVisibility(View.GONE);
                        btnTryAgain.setVisibility(View.VISIBLE);
                        Toasty.warning(ActSplash.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * @param model get from response
     */
    private void HandelDataAction(CoreMain model) {

        EasyPreference.with(ActSplash.this).addLong("MemberUserId", model.MemberUserId);
        EasyPreference.with(ActSplash.this).addLong("UserId", model.UserId);
        EasyPreference.with(ActSplash.this).addLong("SiteId", model.SiteId);
        EasyPreference.with(ActSplash.this).addString("configapp", new Gson().toJson(model));
        if (model.UserId <= 0)
            EasyPreference.with(ActSplash.this).addBoolean("Registered", false);

//        Loading.cancelAnimation();
//        Loading.setVisibility(View.GONE);

        if (!EasyPreference.with(ActSplash.this).getBoolean("Intro", false)) {
            new Handler().postDelayed(() -> {
//                Loading.setVisibility(View.GONE);
                startActivity(new Intent(ActSplash.this, ActIntro.class));
                finish();
            }, 3000);
            return;
        }
        if (!EasyPreference.with(ActSplash.this).getBoolean("Registered", false)) {
            new Handler().postDelayed(() -> {
//                Loading.setVisibility(View.GONE);
                startActivity(new Intent(ActSplash.this, ActRegister.class));
                finish();
            }, 3000);
            return;
        }
        new Handler().postDelayed(() -> {
//            Loading.setVisibility(View.GONE);
            startActivity(new Intent(ActSplash.this, ActMain.class));
            finish();
        }, 3000);
    }

    /**
     * handle click of try again
     */
    @OnClick(R.id.btnTryAgain)
    public void ClickRefresh() {
        switcher.showProgressView();
        getData();
    }
}
