package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.activity.common.NotificationsActivity;
import ntk.android.base.activity.poling.PolingActivity;
import ntk.android.base.activity.ticketing.FaqActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.base.activity.ticketing.TicketSearchActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.CoreImageAdapter;
import ntk.android.ticketing.event.toolbar.EVSearchClick;

public class MainActivity extends AbstractMainActivity {

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
        setDirectContentView(R.layout.main_activity);
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
//         todo   CheckUpdate(res.Item);
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






    private void HandelSlider() {

        FilterDataModel request = new FilterDataModel();
        request.RowPerPage = 5;
        request.CurrentPageNumber = 1;
        new NewsContentService(this).getAll(request).
                observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new NtkObserver<ErrorException<NewsContentModel>>() {

                    @Override
                    public void onNext(ErrorException<NewsContentModel> newsContentResponse) {
                        if (newsContentResponse.IsSuccess) {
                            findViewById(R.id.linear).setBackground(null);
                            SnapHelper snapHelper = new PagerSnapHelper();
                            CoreImageAdapter adapter = new CoreImageAdapter(MainActivity.this, newsContentResponse.ListItems);
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
        onFeedbackClick();
    }

    @OnClick(R.id.poolingBtn)
    public void onPoolingClick() {
        this.startActivity(new Intent(this, PolingActivity.class));
    }

    @OnClick(R.id.inviteBtn)
    public void onInviteClick() {
        onInviteMethod();
    }

    @OnClick(R.id.questionBtn)
    public void onQuestionClick() {
        this.startActivity(new Intent(this, FaqActivity.class));
    }

    @OnClick(R.id.blogBtn)
    public void onBlogClick() {
        this.startActivity(new Intent(this, BlogListActivity.class));
    }

    @OnClick(R.id.aboutUsBtn)
    public void onAboutUsClick() {
        this.startActivity(new Intent(this, AboutUsActivity.class));
    }

    @OnClick(R.id.introBtn)
    public void onIntroClick() {
        onMainIntro();
    }
}