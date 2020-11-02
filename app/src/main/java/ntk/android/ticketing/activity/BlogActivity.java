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
import ntk.android.base.api.blog.entity.BlogContent;
import ntk.android.base.api.blog.interfase.IBlog;
import ntk.android.base.api.blog.model.BlogContentListRequest;
import ntk.android.base.api.blog.model.BlogContentListResponse;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EndlessRecyclerViewScrollListener;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.BlogAdapter;

public class BlogActivity extends BaseActivity {

    @BindView(R.id.lblTitleActBlog)
    TextView LblTitle;

    @BindView(R.id.recyclerBlog)
    RecyclerView Rv;

    @BindView(R.id.swipRefreshActBlog)
    SwipeRefreshLayout Refresh;

    private int Total = 0;
    private List<BlogContent> blog = new ArrayList<>();
    private BlogAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_blog);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Rv.setHasFixedSize(true);
        LinearLayoutManager LMC = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(LMC);
        adapter = new BlogAdapter(this, blog);
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
            blog.clear();
            init();
            Refresh.setRefreshing(false);
        });
    }

    private void RestCall(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showProgressView();

            IBlog iBlog = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IBlog.class);

            BlogContentListRequest request = new BlogContentListRequest();
            request.RowPerPage = 20;
            request.CurrentPageNumber = i;
            Observable<BlogContentListResponse> call = iBlog.GetContentList(new ConfigRestHeader().GetHeaders(this), request);
            //todo show loading
            call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BlogContentListResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogContentListResponse blogContentResponse) {
                            if (blogContentResponse.IsSuccess) {
                                blog.addAll(blogContentResponse.ListItems);
                                Total = blogContentResponse.TotalRowCount;
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

    @OnClick(R.id.imgBackActBlog)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.imgSearchActBlog)
    public void ClickSearch() {
        startActivity(new Intent(this, BlogSearchActivity.class));
    }
}
