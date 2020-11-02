package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.NewsAdapter;
import ntk.android.base.api.news.entity.NewsContent;
import ntk.android.base.api.news.interfase.INews;
import ntk.android.base.api.news.model.NewsContentListRequest;
import ntk.android.base.api.news.model.NewsContentResponse;
import ntk.android.base.config.RetrofitManager;

public class NewsListActivity extends BaseActivity {

    @BindView(R.id.lblTitleActNews)
    TextView LblTitle;

    @BindView(R.id.recyclerNews)
    RecyclerView Rv;

    @BindView(R.id.swipRefreshActNews)
    SwipeRefreshLayout Refresh;

    private int Total = 0;
    private List<NewsContent> news = new ArrayList<>();
    private NewsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //todo show loading
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Rv.setHasFixedSize(true);
        LinearLayoutManager LMC = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(LMC);
        adapter = new NewsAdapter(this, news);
        Rv.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(LMC) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= Total) {
                    RestCall((page + 1));
                }
            }
        };
        Rv.addOnScrollListener(scrollListener);

        RestCall(1);

        Refresh.setOnRefreshListener(() -> {
            news.clear();
            init();
            Refresh.setRefreshing(false);
        });
    }

    private void RestCall(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showProgressView();
            INews iNews = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(INews.class);

            NewsContentListRequest request = new NewsContentListRequest();
            request.RowPerPage = 20;
            request.CurrentPageNumber = i;

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
                                news.addAll(newsContentResponse.ListItems);
                                Total = newsContentResponse.TotalRowCount;
                                adapter.notifyDataSetChanged();
                                if (Total > 0)
                                    switcher.showContentView();
                                else
                                    switcher.showEmptyView();

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }

    @OnClick(R.id.imgBackActNews)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.imgSearchActNews)
    public void ClickSearch() {
        startActivity(new Intent(this, NewsSearchActivity.class));
    }
}
