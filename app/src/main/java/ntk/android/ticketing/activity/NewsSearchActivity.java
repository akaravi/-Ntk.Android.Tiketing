package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.api.news.entity.NewsContent;
import ntk.android.base.api.news.interfase.INews;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.NewsAdapter;

;

public class NewsSearchActivity extends BaseActivity {

    @BindView(R.id.txtSearchActNewsSearch)
    EditText Txt;

    @BindView(R.id.recyclerNewsSearch)
    RecyclerView Rv;

    @BindView(R.id.btnRefreshActNewsSearch)
    Button btnRefresh;

    @BindView(R.id.mainLayoutActNewsSearch)
    CoordinatorLayout layout;

    private ArrayList<NewsContentModel> news = new ArrayList<>();
    private NewsAdapter adapter;
    boolean searchLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_search);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new GridLayoutManager(this, 2));

        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });
        adapter = new NewsAdapter(this, news);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {
                FilterDataModel request = new FilterDataModel();
                List<Filters> filters = new ArrayList<>();
                Filters ft = new Filters();
                ft.PropertyName = "Title";
                ft.StringValue = Txt.getText().toString();
                ft.ClauseType = NTKUtill.ClauseType_Or;
                ft.SearchType = NTKUtill.Search_Type_Contains;
                filters.add(ft);

                Filters fd = new Filters();
                fd.PropertyName = "Description";
                fd.StringValue = Txt.getText().toString();
                fd.ClauseType = NTKUtill.ClauseType_Or;
                fd.SearchType = NTKUtill.Search_Type_Contains;
                filters.add(fd);

                Filters fb = new Filters();
                fb.PropertyName = "Body";
                fb.StringValue = Txt.getText().toString();
                fb.ClauseType = NTKUtill.ClauseType_Or;
                fb.SearchType = NTKUtill.Search_Type_Contains;

                filters.add(fb);

                request.filters = filters;
                switcher.showProgressView();
                new NewsContentService(this).getAll(request).
                        observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new NtkObserver<ErrorException<NewsContentModel>>() {
                            @Override
                            public void onNext(@NonNull ErrorException<NewsContentModel> response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        news.addAll(response.ListItems);
                                        adapter.notifyDataSetChanged();
                                        switcher.showContentView();
                                    } else {
                                        switcher.showEmptyView();
                                    }
                                } else {
                                    switcher.showErrorView(response.ErrorMessage, () -> init());
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                searchLock = false;
                                btnRefresh.setVisibility(View.VISIBLE);
                                switcher.showErrorView("خطا در دسترسی به سامانه", () -> init());
                            }
                        });
            } else {
                btnRefresh.setVisibility(View.VISIBLE);
                searchLock = false;
                switcher.showErrorView("عدم دسترسی به اینترنت", () -> Search());
            }
        }
    }

    @OnClick(R.id.imgBackActNewsSearch)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.btnRefreshActNewsSearch)
    public void ClickRefresh() {
        btnRefresh.setVisibility(View.GONE);
        init();
    }
}
