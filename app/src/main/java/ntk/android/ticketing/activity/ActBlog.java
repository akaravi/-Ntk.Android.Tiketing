package ntk.android.ticketing.activity;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdBlog;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.utill.EndlessRecyclerViewScrollListener;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.blog.interfase.IBlog;
import ntk.base.api.blog.model.BlogContent;
import ntk.base.api.blog.model.BlogContentListRequest;
import ntk.base.api.blog.model.BlogContentListResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActBlog extends AppCompatActivity {

    @BindView(R.id.lblTitleActBlog)
    TextView LblTitle;

    @BindView(R.id.recyclerBlog)
    RecyclerView Rv;

    @BindView(R.id.swipRefreshActBlog)
    SwipeRefreshLayout Refresh;

    private int Total = 0;
    private List<BlogContent> blog = new ArrayList<>();
    private AdBlog adapter;

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
        adapter = new AdBlog(this, blog);
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
        RetrofitManager manager = new RetrofitManager(this);
        IBlog iBlog = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IBlog.class);

        BlogContentListRequest request = new BlogContentListRequest();
        request.RowPerPage = 20;
        request.CurrentPageNumber = i;
        Observable<BlogContentListResponse> call = iBlog.GetContentList(new ConfigRestHeader().GetHeaders(this), request);
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
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(ActBlog.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.imgBackActBlog)
    public void ClickBack() {
        finish();
    }
}
