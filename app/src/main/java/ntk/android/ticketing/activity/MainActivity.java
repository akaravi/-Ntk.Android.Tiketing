package ntk.android.ticketing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

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
import ntk.android.base.activity.IntroActivity;
import ntk.android.base.activity.NotificationsActivity;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.BuildConfig;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdCoreImage;
import ntk.android.ticketing.event.toolbar.EVSearchClick;
import ntk.android.base.api.application.interfase.IApplication;
import ntk.android.base.api.application.model.ApplicationScoreRequest;
import ntk.android.base.api.application.model.ApplicationScoreResponse;
import ntk.android.base.api.core.entity.CoreMain;
import ntk.android.base.api.news.interfase.INews;
import ntk.android.base.api.news.model.NewsContentListRequest;
import ntk.android.base.api.news.model.NewsContentResponse;
import ntk.android.base.config.RetrofitManager;

public class MainActivity extends AppCompatActivity {

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
    RecyclerView Slider;

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
            CheckUpdate();
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
        CheckUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void EvClickSearch(EVSearchClick click) {
        startActivity(new Intent(this, TicketSearchActivity.class));
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


    /**
     * check new version availability
     */
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

    /**
     * optional update if user want
     */
    private void Update() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        Dialog dialog = new Dialog(this);
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
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialog);
        Ok.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Ok.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mcr.AppUrl));
            startActivity(i);
            dialog.dismiss();
        });
        Button Cancel = dialog.findViewById(R.id.btnCancelPermissionDialog);
        Cancel.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Cancel.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }

    /**
     * force update app
     */
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
        Button Ok = dialog.findViewById(R.id.btnOkPermissionDialogUpdate);
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
         INews iNews = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(INews.class);

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
                            findViewById(R.id.linear).setBackground(null);
                            SnapHelper snapHelper = new PagerSnapHelper();
                            AdCoreImage adapter = new AdCoreImage(MainActivity.this, newsContentResponse.ListItems);
                            Slider.setHasFixedSize(true);
                            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, true);
                            Slider.setLayoutManager(manager);
                            Slider.setAdapter(adapter);
                            snapHelper.attachToRecyclerView(Slider);
                            adapter.notifyDataSetChanged();
//                            List<Banner> banners = new ArrayList<>();
//                            for (NewsContent news : newsContentResponse.ListItems) {
//                                banners.add(new RemoteBanner(news.imageSrc));
//                            }
//                            Slider.setBanners(banners);
//                            Slider.setOnBannerClickListener(new OnBannerClickListener() {
//                                @Override
//                                public void onClick(int position) {
//                                    NewsContentViewRequest request = new NewsContentViewRequest();
//                                    request.Id = newsContentResponse.ListItems.get(position).Id;
//                                    startActivity(new Intent(ActMain.this, ActDetailNews.class).putExtra("Request", new Gson().toJson(request)));
//                                }
//                            });
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
        this.startActivity(new Intent(this, TicketListActivity.class));
    }

    @OnClick(R.id.searchBtn)
    public void onSearchClick() {
        this.startActivity(new Intent(this, TicketSearchActivity.class));
    }

    @OnClick(R.id.messageBtn)
    public void onInboxClick() {
        this.startActivity(new Intent(this, NotificationsActivity.class));
    }

    @OnClick(R.id.newsBtn)
    public void onNewsClick() {
        this.startActivity(new Intent(this, NewsListActivity.class));
    }

    @OnClick(R.id.feedbackBtn)
    public void onFeedBackClick() {
        ApplicationScoreRequest request = new ApplicationScoreRequest();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                Toasty.error(this, "لطفا نظر خود را وارد نمایید", Toasty.LENGTH_LONG, true).show();
            } else {
                if (AppUtill.isNetworkAvailable(this)) {
                    request.ScoreComment = Txt.getText().toString();
                    //todo show loading
                    IApplication iCore = new RetrofitManager(this).getCachedRetrofit().create(IApplication.class);
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
                                        Toasty.success(MainActivity.this, "با موفقیت ثبت شد", Toast.LENGTH_LONG, true).show();
                                    } else {
                                        Toasty.warning(MainActivity.this, "خظا در دریافت اطلاعات", Toast.LENGTH_LONG, true).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toasty.warning(MainActivity.this, "خظا در اتصال به مرکز", Toast.LENGTH_LONG, true).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Toasty.error(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.poolingBtn)
    public void onPoolingClick() {
        this.startActivity(new Intent(this, PoolingActivity.class));
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
        this.startActivity(new Intent(this, FaqActivity.class));
    }

    @OnClick(R.id.blogBtn)
    public void onBlogClick() {
        this.startActivity(new Intent(this, BlogActivity.class));
    }

    @OnClick(R.id.aboutUsBtn)
    public void onAboutUsClick() {
        this.startActivity(new Intent(this, AboutUsActivity.class));
    }

    @OnClick(R.id.introBtn)
    public void onIntroClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("Help", 1);
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra("Help", bundle);
        this.startActivity(intent);
    }
}
