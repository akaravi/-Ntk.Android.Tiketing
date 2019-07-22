package ntk.android.ticketing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.animators.MemorybarAnimation;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import ntk.android.ticketing.BuildConfig;
import ntk.android.ticketing.R;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.event.toolbar.EVSearchClick;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.EasyPreference;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.application.interfase.IApplication;
import ntk.base.api.application.model.ApplicationScoreRequest;
import ntk.base.api.application.model.ApplicationScoreResponse;
import ntk.base.api.core.interfase.ICore;
import ntk.base.api.core.model.CoreMain;
import ntk.base.api.core.model.MainCoreResponse;
import ntk.base.api.news.interfase.INews;
import ntk.base.api.news.model.NewsContent;
import ntk.base.api.news.model.NewsContentListRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsContentViewRequest;
import ntk.base.api.utill.RetrofitManager;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

public class ActMain extends AppCompatActivity {

    @BindViews({R.id.news,
            R.id.pooling,
            R.id.invite,
            R.id.feedback,
            R.id.question,
            R.id.intro,
            R.id.blog,
            R.id.aboutUs,
            R.id.support,
            R.id.message,
            R.id.search})
    List<TextView> lbl;

    @BindViews({R.id.newsBtn,
            R.id.poolingBtn,
            R.id.searchBtn,
            R.id.inviteBtn,
            R.id.feedbackBtn,
            R.id.questionBtn,
            R.id.introBtn,
            R.id.blogBtn,
            R.id.aboutUsBtn,
            R.id.supportBtn,
            R.id.messageBtn})
    List<LinearLayout> btn;

    @BindView(R.id.bannerLayout)
    LinearLayout layout;

    @BindView(R.id.SliderActMain)
    BannerSlider Slider;

    @BindView(R.id.RefreshMain)
    SwipeRefreshLayout Refresh;

    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setAnimation();
        for (int i = 0; i < lbl.size(); i++) {
            lbl.get(i).setTypeface(FontManager.GetTypeface(this, FontManager.DastNevis));
        }
        Refresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorAccent,
                R.color.colorAccent);

        Refresh.setOnRefreshListener(() -> {
            HandelData();
            setAnimation();
            Refresh.setRefreshing(false);
        });
        HandelSlider();
    }

    private void setAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        for (int i = 0; i < btn.size(); i++) {
            btn.get(i).startAnimation(scaleAnimation);
        }
        layout.startAnimation(alphaAnimation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        HandelData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void EvClickSearch(EVSearchClick click) {
        startActivity(new Intent(this, ActSearch.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        finish();
                    } else {
                        Toasty.warning(getApplicationContext(), "برای خروج مجددا کلید بازگشت را فشار دهید",
                                Toast.LENGTH_SHORT, true).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

    private void HandelData() {
        if (AppUtill.isNetworkAvailable(this)) {
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
                            EasyPreference.with(ActMain.this).addString("configapp", new Gson().toJson(mainCoreResponse.Item));
                            CheckUpdate();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            CheckUpdate();
        }
    }

    private void CheckUpdate() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        if (mcr.AppVersion > BuildConfig.VERSION_CODE && BuildConfig.APPLICATION_ID.indexOf(".APPNTK") < 0) {
            if (mcr.AppForceUpdate) {
                UpdateFore();
            } else {
                Update();
            }
        }
    }

    private void Update() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_permission);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialog)).setText("توجه");
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialog)).setText("نسخه جدید اپلیکیشن اومده دوست داری آبدیت بشه؟؟");
        Button Ok = (Button) dialog.findViewById(R.id.btnOkPermissionDialog);
        Ok.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mcr.AppUrl));
            startActivity(i);
            dialog.dismiss();
        });
        Button Cancel = (Button) dialog.findViewById(R.id.btnCancelPermissionDialog);
        Cancel.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Cancel.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    private void UpdateFore() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_update);
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl1PernissionDialogUpdate)).setText("توجه");
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) dialog.findViewById(R.id.lbl2PernissionDialogUpdate)).setText("نسخه جدید اپلیکیشن اومده حتما باید آبدیت بشه");
        Button Ok = (Button) dialog.findViewById(R.id.btnOkPermissionDialogUpdate);
        Ok.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mcr.AppUrl));
            startActivity(i);
            dialog.dismiss();
        });
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    finish();
            }
            return true;
        });
        dialog.show();
    }

    private void HandelSlider() {
        RetrofitManager manager = new RetrofitManager(this);
        INews iNews = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(INews.class);

        NewsContentListRequest request = new NewsContentListRequest();
        request.RowPerPage = 5;
        request.CurrentPageNumber = 1;
        Observable<NewsContentResponse> call = iNews.GetContentList(new ConfigRestHeader().GetHeaders(this), request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentResponse newsContentResponse) {
                        if (newsContentResponse.IsSuccess) {
                            List<Banner> banners = new ArrayList<>();
                            for (NewsContent news : newsContentResponse.ListItems) {
                                banners.add(new RemoteBanner(news.imageSrc));
                            }
                            Slider.setBanners(banners);
                            Slider.setOnBannerClickListener(new OnBannerClickListener() {
                                @Override
                                public void onClick(int position) {
                                    NewsContentViewRequest request = new NewsContentViewRequest();
                                    request.Id = newsContentResponse.ListItems.get(position).Id;
                                    startActivity(new Intent(ActMain.this, ActDetailNews.class).putExtra("Request", new Gson().toJson(request)));
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.supportBtn)
    public void onSupportClick() {
        this.startActivity(new Intent(this, ActSupport.class));
    }

    @OnClick(R.id.searchBtn)
    public void onSearchClick() {
        this.startActivity(new Intent(this, ActSearch.class));
    }

    @OnClick(R.id.messageBtn)
    public void onInboxClick() {
        this.startActivity(new Intent(this, ActInbox.class));
    }

    @OnClick(R.id.newsBtn)
    public void onNewsClick() {
        this.startActivity(new Intent(this, ActNews.class));
    }

    @OnClick(R.id.feedbackBtn)
    public void onFeedBackClick() {
        ApplicationScoreRequest request = new ApplicationScoreRequest();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogComment);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        final EditText Txt = dialog.findViewById(R.id.txtDialogComment);
        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txt.setText(EasyPreference.with(this).getString("RateMessage", ""));
        final MaterialRatingBar Rate = dialog.findViewById(R.id.rateDialogComment);
        Rate.setRating(EasyPreference.with(this).getInt("Rate", 0));
        Rate.setOnRatingChangeListener((ratingBar, rating) -> {
            request.ScorePercent = (int) rating;
            //برای تبدیل به درصد
            request.ScorePercent = request.ScorePercent * 17;
            if (request.ScorePercent > 100)
                request.ScorePercent = 100;
        });
        Button Btn = dialog.findViewById(R.id.btnDialogComment);
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Btn.setOnClickListener(v -> {
            if (Txt.getText().toString().isEmpty()) {
                Toast.makeText(this, "لطفا نظر خود را وارد نمایید", Toast.LENGTH_SHORT).show();
            } else {
                if (AppUtill.isNetworkAvailable(this)) {
                    request.ScoreComment = Txt.getText().toString();

                    RetrofitManager manager = new RetrofitManager(this);
                    IApplication iCore = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(IApplication.class);
                    Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
                    Observable<ApplicationScoreResponse> Call = iCore.SetScoreApplication(headers, request);
                    Call.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<ApplicationScoreResponse>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(ApplicationScoreResponse applicationScoreResponse) {
                                    if (applicationScoreResponse.IsSuccess) {
                                        Toasty.success(ActMain.this, "با موفقیت ثبت شد", Toast.LENGTH_LONG, true).show();
                                    } else {
                                        Toasty.warning(ActMain.this, "خظا در دریافت اطلاعات", Toast.LENGTH_LONG, true).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toasty.warning(ActMain.this, "خظا در اتصال به مرکز", Toast.LENGTH_LONG, true).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.poolingBtn)
    public void onPoolingClick() {
        this.startActivity(new Intent(this, ActPooling.class));
    }

    @OnClick(R.id.inviteBtn)
    public void onInviteClick() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_qrcode);
        dialog.show();
        TextView Lbl = dialog.findViewById(R.id.lblTitleDialogQRCode);
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        QRGEncoder qrgEncoder = new QRGEncoder(mcr.AppUrl, null, QRGContents.Type.TEXT, 300);
        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            ImageView img = dialog.findViewById(R.id.qrCodeDialogQRCode);
            img.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toasty.warning(this, e.getMessage(), Toast.LENGTH_LONG, true).show();
        }

        Button Btn = dialog.findViewById(R.id.btnDialogQRCode);
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.app_name) + "\n" + "لینک دانلود:" + "\n" + mcr.AppUrl);
            shareIntent.setType("text/txt");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(Intent.createChooser(shareIntent, "به اشتراک گزاری با...."));
        });
    }

    @OnClick(R.id.questionBtn)
    public void onQuestionClick() {
        this.startActivity(new Intent(this, ActFaq.class));
    }

    @OnClick(R.id.blogBtn)
    public void onBlogClick() {
        this.startActivity(new Intent(this, ActBlog.class));
    }

    @OnClick(R.id.aboutUsBtn)
    public void onAboutUsClick() {
        this.startActivity(new Intent(this, ActAbout.class));
    }

    @OnClick(R.id.introBtn)
    public void onIntroClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("Help", 1);
        Intent intent = new Intent(this, ActIntro.class);
        intent.putExtra("Help", bundle);
        this.startActivity(intent);
    }
}
