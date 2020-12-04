package ntk.android.ticketing.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.activity.common.BaseFilterModelListActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.blog.BlogContentModel;
import ntk.android.base.services.blog.BlogContentService;
import ntk.android.ticketing.adapter.BlogAdapter;

public class BlogListActivity extends BaseFilterModelListActivity<BlogContentModel> {


    @Override
    public Function<FilterDataModel, Observable<ErrorException<BlogContentModel>>> getService() {
        return new BlogContentService(this)::getAll;
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        return new BlogAdapter(this, models);
    }

    @Override
    public void ClickSearch() {
        startActivity(new Intent(this, BlogSearchActivity.class));
    }

//    @BindView(R.id.lblTitleActBlog)
//    TextView LblTitle;
//
//    @BindView(R.id.recyclerBlog)
//    RecyclerView Rv;
//
//    @BindView(R.id.swipRefreshActBlog)
//    SwipeRefreshLayout Refresh;
//
//    private int Total = 0;
//    private List<BlogContentModel> blog = new ArrayList<>();
//    private BlogAdapter adapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_blog);
//        ButterKnife.bind(this);
//        init();
//    }
//
//    private void init() {
//        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
//        Rv.setHasFixedSize(true);
//        LinearLayoutManager LMC = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        Rv.setLayoutManager(LMC);
//        adapter = new BlogAdapter(this, blog);
//        Rv.setAdapter(adapter);
//
//        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(LMC) {
//
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (totalItemsCount <= Total) {
//                    RestCall((page + 1));
//                }
//            }
//        };
//        Rv.addOnScrollListener(scrollListener);
//
//        RestCall(1);
//
//        Refresh.setOnRefreshListener(() -> {
//            blog.clear();
//            init();
//            Refresh.setRefreshing(false);
//        });
//    }
//
//    private void RestCall(int i) {
//        if (AppUtill.isNetworkAvailable(this)) {
//            switcher.showProgressView();
//            FilterDataModel request = new FilterDataModel();
//            request.RowPerPage = 20;
//            request.CurrentPageNumber = i;
//            //todo show loading
//            new BlogContentService(this).getAll(request).
//                    observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new NtkObserver<ErrorException<BlogContentModel>>() {
//                        @Override
//                        public void onNext(@NonNull ErrorException<BlogContentModel> blogContentResponse) {
//                            if (blogContentResponse.IsSuccess) {
//                                blog.addAll(blogContentResponse.ListItems);
//                                Total = blogContentResponse.TotalRowCount;
//                                adapter.notifyDataSetChanged();
//                                if (Total > 0)
//                                    switcher.showContentView();
//                                else
//                                    switcher.showEmptyView();
//
//                            }
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable e) {
//                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());
//                        }
//                    });
//        } else {
//            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());
//
//        }
//    }
//
//    @OnClick(R.id.imgBackActBlog)
//    public void ClickBack() {
//        finish();
//    }
//
//    @OnClick(R.id.imgSearchActBlog)
//    public void ClickSearch() {
//
//    }
}
