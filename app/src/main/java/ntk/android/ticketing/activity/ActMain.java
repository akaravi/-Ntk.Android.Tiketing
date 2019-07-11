package ntk.android.ticketing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.gson.Gson;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
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
import ntk.android.ticketing.adapter.AdTicket;
import ntk.android.ticketing.adapter.drawer.AdDrawer;
import ntk.android.ticketing.adapter.toolbar.AdToobar;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.event.toolbar.EVHamberMenuClick;
import ntk.android.ticketing.event.toolbar.EVSearchClick;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.EasyPreference;
import ntk.android.ticketing.utill.EndlessRecyclerViewScrollListener;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.core.interfase.ICore;
import ntk.base.api.core.model.CoreMain;
import ntk.base.api.core.model.MainCoreResponse;
import ntk.base.api.model.theme.Theme;
import ntk.base.api.model.theme.Toolbar;
import ntk.base.api.news.interfase.INews;
import ntk.base.api.news.model.NewsContent;
import ntk.base.api.news.model.NewsContentListRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsContentViewRequest;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingListRequest;
import ntk.base.api.ticket.model.TicketingListResponse;
import ntk.base.api.ticket.model.TicketingTask;
import ntk.base.api.utill.NTKUtill;
import ntk.base.api.utill.RetrofitManager;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

public class ActMain extends AppCompatActivity {

    @BindView(R.id.drawerlayout)
    FlowingDrawer drawer;

    @BindView(R.id.HeaderImageActMain)
    KenBurnsView Header;

    @BindView(R.id.RecyclerToolbarActMain)
    RecyclerView RvToolbar;

    @BindView(R.id.RecyclerDrawer)
    RecyclerView RvDrawer;

    @BindView(R.id.recyclerActMain)
    RecyclerView Rv;

    @BindView(R.id.FabActMain)
    FloatingActionButton Fab;

    @BindView(R.id.RefreshTicket)
    SwipeRefreshLayout Refresh;

    @BindView(R.id.SliderActMain)
    public BannerSlider Slider;

    @BindView(R.id.lblStatusActMain)
    TextView status;

    @BindView(R.id.mainLayoutActMain)
    CoordinatorLayout layout;

    private ArrayList<TicketingTask> tickets = new ArrayList<>();
    private AdTicket adapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private int TotalTag = 0;

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
        status.setTypeface(FontManager.GetTypeface(ActMain.this, FontManager.IranSans));
        drawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
            }
        });
        HandelToolbarDrawer();

        HandelSlider();

        Rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);

        adapter = new AdTicket(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= TotalTag) {
                    HandelDataSupport((page + 1));
                }
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if (dy > 0 || dy < 0 && Fab.isShown())
                    Fab.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

        Rv.addOnScrollListener(scrollListener);

        Refresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorAccent,
                R.color.colorAccent);

        Refresh.setOnRefreshListener(() -> {
            tickets.clear();
            HandelDataSupport(1);
            Refresh.setRefreshing(false);
        });

        HandelDataSupport(1);
    }

    private void HandelDataSupport(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            ITicket iTicket = retro.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            TicketingListRequest request = new TicketingListRequest();
            request.RowPerPage = 10;
            request.CurrentPageNumber = i;
            request.SortType = NTKUtill.Descnding_Sort;
            request.SortColumn = "Id";

            Observable<TicketingListResponse> Call = iTicket.GetTicketList(headers, request);
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TicketingListResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingListResponse model) {
                            if (!model.ListItems.isEmpty()) {
                                tickets.addAll(model.ListItems);
                                adapter.notifyDataSetChanged();
                                TotalTag = model.TotalRowCount;
                                status.setVisibility(View.GONE);
                            } else {
                                status.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }

    @OnClick(R.id.FabActMain)
    public void ClickSendTicket() {
        startActivity(new Intent(this, ActSendTicket.class));
    }

    private void HandelToolbarDrawer() {
        Theme theme = new Gson().fromJson(EasyPreference.with(this).getString("Theme", ""), Theme.class);
        if (theme == null) return;
        RvToolbar.setHasFixedSize(true);
        RvToolbar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Toolbar> toolbars = new ArrayList<>();
        toolbars.add(theme.Toolbar);
        AdToobar AdToobar = new AdToobar(this, toolbars);
        RvToolbar.setAdapter(AdToobar);
        AdToobar.notifyDataSetChanged();

        ImageLoader.getInstance().displayImage(theme.Toolbar.Drawer.HeaderImage, Header);

        RvDrawer.setHasFixedSize(true);
        RvDrawer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdDrawer AdDrawer = new AdDrawer(this, theme.Toolbar.Drawer.Child, drawer);
        RvDrawer.setAdapter(AdDrawer);
        AdDrawer.notifyDataSetChanged();
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
    public void EvClickMenu(EVHamberMenuClick click) {
        drawer.openMenu(false);
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
                                    startActivity(new Intent(ActMain.this,ActDetailNews.class).putExtra("Request", new Gson().toJson(request)));
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

}
