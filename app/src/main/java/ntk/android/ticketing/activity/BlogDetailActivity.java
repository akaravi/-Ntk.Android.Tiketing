package ntk.android.ticketing.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.BlogAdapter;
import ntk.android.ticketing.adapter.AdCommentBlog;
import ntk.android.ticketing.adapter.AdTabBlog;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.ticketing.event.BlogHtmlBodyEvent;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.blog.interfase.IBlog;
import ntk.android.base.api.blog.model.BlogCommentAddRequest;
import ntk.android.base.api.blog.model.BlogCommentListRequest;
import ntk.android.base.api.blog.model.BlogCommentResponse;
import ntk.android.base.api.blog.model.BlogContentFavoriteAddRequest;
import ntk.android.base.api.blog.model.BlogContentFavoriteAddResponse;
import ntk.android.base.api.blog.model.BlogContentFavoriteRemoveRequest;
import ntk.android.base.api.blog.model.BlogContentFavoriteRemoveResponse;
import ntk.android.base.api.blog.entity.BlogContentOtherInfo;
import ntk.android.base.api.blog.model.BlogContentOtherInfoListRequest;
import ntk.android.base.api.blog.model.BlogContentOtherInfoListResponse;
import ntk.android.base.api.blog.model.BlogContentResponse;
import ntk.android.base.api.blog.model.BlogContentViewRequest;
import ntk.android.base.api.core.entity.CoreMain;
import ntk.android.base.api.baseModel.Filters;
import ntk.android.base.config.RetrofitManager;

public class BlogDetailActivity extends AppCompatActivity {

    @BindView(R.id.progressActDetailBlog)
    ProgressBar Progress;

    @BindView(R.id.rowProgressActDetailBlog)
    LinearLayout Loading;

    @BindViews({R.id.lblTitleActDetailBlog,
            R.id.lblNameCommandActDetailBlog,
            R.id.lblKeySeenActDetailBlog,
            R.id.lblValueSeenActDetailBlog,
            R.id.lblMenuActDetailBlog,
            R.id.lblAllMenuActDetailBlog,
            R.id.lblCommentActDetailBlog,
            R.id.lblProgressActDetailBlog
    })
    List<TextView> Lbls;

    @BindView(R.id.imgHeaderActDetailBlog)
    ImageView ImgHeader;

    @BindView(R.id.recyclerMenuActDetailBlog)
    RecyclerView Rv;

    @BindView(R.id.recyclerTabActDetailBlog)
    RecyclerView RvTab;

    @BindView(R.id.recyclerCommentActDetailBlog)
    RecyclerView RvComment;

    @BindView(R.id.ratingBarActDetailBlog)
    RatingBar Rate;

    @BindView(R.id.WebViewBodyActDetailBlog)
    WebView webViewBody;

    @BindView(R.id.PageActDetailBlog)
    LinearLayout Page;

    @BindView(R.id.mainLayoutActDetailBlog)
    CoordinatorLayout layout;

    private String RequestStr;
    private BlogContentResponse model;
    private BlogContentOtherInfoListResponse Info;
    private BlogContentViewRequest Request;
    private ConfigStaticValue configStaticValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_blog);
        ButterKnife.bind(this);
        configStaticValue = new ConfigStaticValue(this);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        for (TextView tv : Lbls) {
            tv.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        Progress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        webViewBody.getSettings().setJavaScriptEnabled(true);
        webViewBody.getSettings().setBuiltInZoomControls(true);
        RvTab.setHasFixedSize(true);
        RvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RequestStr = getIntent().getExtras().getString("Request");
        Request = new Gson().fromJson(RequestStr, BlogContentViewRequest.class);
        HandelDataContent(Request);
        Loading.setVisibility(View.VISIBLE);

        RvComment.setHasFixedSize(true);
        RvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) return;

                if (AppUtill.isNetworkAvailable(BlogDetailActivity.this)) {

                    BlogContentViewRequest request = new BlogContentViewRequest();
                    request.Id = Request.Id;
                    request.ActionClientOrder = 55;
                    if (rating == 0.5) {
                        request.ScorePercent = 10;
                    }
                    if (rating == 1) {
                        request.ScorePercent = 20;
                    }
                    if (rating == 1.5) {
                        request.ScorePercent = 30;
                    }
                    if (rating == 2) {
                        request.ScorePercent = 40;
                    }
                    if (rating == 2.5) {
                        request.ScorePercent = 50;
                    }
                    if (rating == 3) {
                        request.ScorePercent = 60;
                    }
                    if (rating == 3.5) {
                        request.ScorePercent = 70;
                    }
                    if (rating == 4) {
                        request.ScorePercent = 80;
                    }
                    if (rating == 4.5) {
                        request.ScorePercent = 90;
                    }
                    if (rating == 5) {
                        request.ScorePercent = 100;
                    }
                    IBlog iBlog = new RetrofitManager(BlogDetailActivity.this).getRetrofitUnCached(new ConfigStaticValue(BlogDetailActivity.this).GetApiBaseUrl()).create(IBlog.class);
                    Map<String, String> headers = new ConfigRestHeader().GetHeaders(BlogDetailActivity.this);

                    Observable<BlogContentResponse> Call = iBlog.GetContentView(headers, request);
                    Call.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<BlogContentResponse>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(BlogContentResponse biographyContentResponse) {
                                    Loading.setVisibility(View.GONE);
                                    if (biographyContentResponse.IsSuccess) {
                                        Toasty.success(BlogDetailActivity.this, "نظر شمابا موفقیت ثبت گردید").show();
                                    } else {
                                        Toasty.warning(BlogDetailActivity.this, biographyContentResponse.ErrorMessage).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Loading.setVisibility(View.GONE);
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
                    Loading.setVisibility(View.GONE);
                    Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            init();
                        }
                    }).show();
                }
            }
        });
    }


    private void HandelDataContent(BlogContentViewRequest request) {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            Observable<BlogContentResponse> call = iBlog.GetContentView(headers, request);
            call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BlogContentResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogContentResponse ContentResponse) {
                            Loading.setVisibility(View.GONE);
                            model = ContentResponse;
                            SetData(model);
                            if (Request.Id > 0) {
                                HandelDataContentOtherInfo(Request.Id);
                                HandelDataComment(Request.Id);
                            }
                            Loading.setVisibility(View.GONE);
                            Page.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Loading.setVisibility(View.GONE);
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
            Loading.setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }

    private void HandelDataComment(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            List<Filters> filters = new ArrayList<>();
            BlogCommentListRequest Request = new BlogCommentListRequest();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = ContentId;
            filters.add(f);
            Request.filters = filters;
            RetrofitManager retro = new RetrofitManager(this);
            IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
            Observable<BlogCommentResponse> call = iBlog.GetCommentList(headers, Request);
            call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BlogCommentResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogCommentResponse model) {
                            if (model.IsSuccess && !model.ListItems.isEmpty()) {
                                findViewById(R.id.lblCommentActDetailBlog).setVisibility(View.VISIBLE);
                                AdCommentBlog adapter = new AdCommentBlog(BlogDetailActivity.this, model.ListItems);
                                RvComment.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.lblCommentActDetailBlog).setVisibility(View.GONE);
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
            findViewById(R.id.lblCommentActDetailBlog).setVisibility(View.GONE);
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }

    private void HandelDataContentOtherInfo(long ContentId) {
        if (AppUtill.isNetworkAvailable(this)) {
            List<Filters> filters = new ArrayList<>();
            BlogContentOtherInfoListRequest Request = new BlogContentOtherInfoListRequest();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = ContentId;
            filters.add(f);
            Request.filters = filters;
            RetrofitManager retro = new RetrofitManager(this);
            IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);


            Observable<BlogContentOtherInfoListResponse> call = iBlog.GetContentOtherInfoList(headers, Request);
            call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BlogContentOtherInfoListResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogContentOtherInfoListResponse ContentOtherInfoResponse) {
                            SetDataOtherinfo(ContentOtherInfoResponse);
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

    private void SetDataOtherinfo(BlogContentOtherInfoListResponse model) {
        Info = model;
        if (model.ListItems == null || model.ListItems.size() == 0) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 3;
            return;
        }
        List<BlogContentOtherInfo> Info = new ArrayList<>();

        for (BlogContentOtherInfo ai : model.ListItems) {
            switch (ai.TypeId) {
                case 21:
                    Lbls.get(7).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(6).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                case 22:
                    Lbls.get(9).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(8).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                case 23:
                    Lbls.get(11).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(10).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                default:
                    Info.add(ai);
                    break;
            }
        }
        AdTabBlog adapter = new AdTabBlog(BlogDetailActivity.this, Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void SetData(BlogContentResponse model) {
        ImageLoader.getInstance().displayImage(model.Item.imageSrc, ImgHeader);
        Lbls.get(0).setText(model.Item.Title);
        Lbls.get(1).setText(model.Item.Title);
        Lbls.get(3).setText(String.valueOf(model.Item.viewCount));
        double rating = 0.0;
        int sumClick = model.Item.ScoreSumClick;
        if (model.Item.ScoreSumClick == 0) sumClick = 1;
        if (model.Item.ScoreSumPercent / sumClick > 0 && model.Item.ScoreSumPercent / sumClick <= 10) {
            rating = 0.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 10 && model.Item.ScoreSumPercent / sumClick <= 20) {
            rating = 1.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 20 && model.Item.ScoreSumPercent / sumClick <= 30) {
            rating = 1.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 30 && model.Item.ScoreSumPercent / sumClick <= 40) {
            rating = 2.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 40 && model.Item.ScoreSumPercent / sumClick <= 50) {
            rating = 2.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 50 && model.Item.ScoreSumPercent / sumClick <= 60) {
            rating = 3.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 60 && model.Item.ScoreSumPercent / sumClick <= 70) {
            rating = 3.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 70 && model.Item.ScoreSumPercent / sumClick <= 80) {
            rating = 4.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 80 && model.Item.ScoreSumPercent / sumClick <= 90) {
            rating = 4.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 90) {
            rating = 5.0;
        }
        Rate.setRating((float) rating);
        if (model.Item.Body != null)
            webViewBody.loadData("<html dir=\"rtl\" lang=\"\"><body>" + model.Item.Body + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        if (model.Item.Favorited) {
            ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav_full);
        }

        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        Rv.setLayoutManager(manager);

        BlogAdapter adBlog = new BlogAdapter(this, model.ListItems);
        Rv.setAdapter(adBlog);
        adBlog.notifyDataSetChanged();
        if (model.ListItems.isEmpty()) {
            Lbls.get(5).setVisibility(View.GONE);
            Lbls.get(4).setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.lblAllMenuActDetailBlog)
    public void onMoreBlogClick() {
        this.startActivity(new Intent(this, BlogActivity.class));
    }


    @OnClick(R.id.imgBackActDetailBlog)
    public void ClickBack() {
        finish();
    }

    @Subscribe
    public void EventHtmlBody(BlogHtmlBodyEvent event) {
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

    @OnClick(R.id.imgCommentActDetailBlog)
    public void ClickCommentAdd() {
        if (AppUtill.isNetworkAvailable(this)) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.setContentView(R.layout.dialog_comment_add);

            TextView Lbl = dialog.findViewById(R.id.lblTitleDialogAddComment);
            Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

            EditText[] Txt = new EditText[2];

            Txt[0] = dialog.findViewById(R.id.txtNameDialogAddComment);
            Txt[0].setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

            Txt[1] = dialog.findViewById(R.id.txtContentDialogAddComment);
            Txt[1].setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

            Button Btn = dialog.findViewById(R.id.btnSubmitDialogCommentAdd);
            Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

            Btn.setOnClickListener(v -> {
                if (Txt[0].getText().toString().isEmpty()) {
                    Toast.makeText(BlogDetailActivity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
                } else {
                    if (Txt[1].getText().toString().isEmpty()) {
                        Toast.makeText(BlogDetailActivity.this, "لطفا مقادیر را وارد نمایید", Toast.LENGTH_SHORT).show();
                    } else {
                        BlogCommentAddRequest add = new BlogCommentAddRequest();
                        add.Writer = Txt[0].getText().toString();
                        add.Comment = Txt[1].getText().toString();
                        add.LinkContentId = Request.Id;
                        RetrofitManager retro = new RetrofitManager(this);
                        IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
                        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);


                        Observable<BlogCommentResponse> call = iBlog.SetComment(headers, add);
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<BlogCommentResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(BlogCommentResponse e) {
                                        if (e.IsSuccess) {
                                            HandelDataComment(Request.Id);
                                            dialog.dismiss();
                                            Toasty.success(BlogDetailActivity.this, "نظر شما با موفقیت ثبت شد").show();
                                        } else {
                                            Toasty.warning(BlogDetailActivity.this, "لطفا مجددا تلاش کنید").show();
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
                    }
                }
            });

            dialog.show();
        } else {
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }

    }

    @OnClick(R.id.imgFavActDetailBlog)
    public void ClickFav() {
        if (!model.Item.Favorited) {
            Fav();
        } else {
            UnFav();
        }
    }

    private void Fav() {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            BlogContentFavoriteAddRequest add = new BlogContentFavoriteAddRequest();
            add.Id = model.Item.Id;

            Observable<BlogContentFavoriteAddResponse> Call = iBlog.SetContentFavoriteAdd(headers, add);
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BlogContentFavoriteAddResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogContentFavoriteAddResponse e) {
                            if (e.IsSuccess) {
                                Toasty.success(BlogDetailActivity.this, "با موفقیت ثبت شد").show();
                                model.Item.Favorited = !model.Item.Favorited;
                                if (model.Item.Favorited) {
                                    ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav_full);
                                } else {
                                    ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav);
                                }
                            } else {
                                Toasty.error(BlogDetailActivity.this, e.ErrorMessage, Toast.LENGTH_LONG, true).show();
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

    private void UnFav() {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            IBlog iBlog = retro.getRetrofitUnCached(configStaticValue.GetApiBaseUrl()).create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            BlogContentFavoriteRemoveRequest add = new BlogContentFavoriteRemoveRequest();
            add.Id = model.Item.Id;

            Observable<BlogContentFavoriteRemoveResponse> Call = iBlog.SetContentFavoriteRemove(headers, add);
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BlogContentFavoriteRemoveResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogContentFavoriteRemoveResponse e) {
                            if (e.IsSuccess) {
                                model.Item.Favorited = !model.Item.Favorited;
                                if (model.Item.Favorited) {
                                    Toasty.success(BlogDetailActivity.this, "با موفقیت ثبت شد").show();
                                    ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav_full);
                                } else {
                                    ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav);
                                }
                            } else {
                                Toasty.error(BlogDetailActivity.this, e.ErrorMessage, Toast.LENGTH_LONG, true).show();
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

    @OnClick(R.id.imgShareActDetailBlog)
    public void ClickShare() {
        String st = EasyPreference.with(this).getString("configapp", "");
        CoreMain mcr = new Gson().fromJson(st, CoreMain.class);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String message = model.Item.Title + "\n" + model.Item.description + "\n";
        if (model.Item.Body != null) {
            message = message + Html.fromHtml(model.Item.Body
                    .replace("<p>", "")
                    .replace("</p>", ""));
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n\n\n" + this.getString(R.string.app_name) + "\n" + "لینک دانلود:" + "\n" + mcr.AppUrl);
        shareIntent.setType("text/txt");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(shareIntent, "به اشتراک گزاری با...."));
    }
}
