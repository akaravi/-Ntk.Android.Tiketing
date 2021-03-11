package ntk.android.ticketing.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import ntk.android.base.adapter.common.MainTagAdapter;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.hypershop.HyperShopCategoryModel;
import ntk.android.base.entitymodel.hypershop.HyperShopContentModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.fragment.BaseFragment;
import ntk.android.base.services.hypershop.HyperShopCategoryService;
import ntk.android.base.services.hypershop.HyperShopContentService;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.MainFragment1_1Adapter;

public class MainFragment extends BaseFragment {
    MainFragment1_1Adapter adapter;

    @Override
    public void onCreateFragment() {
        setContentView(R.layout.main_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switcher.showProgressView();
        List<String> titles = new ArrayList() {{
            add("اخبار برگزیده");
            add("مقالات اخیر");
            add("آخرین درخواست ها");
        }};

        RecyclerView chipGroup = findViewById(R.id.tagRc);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);
        chipGroup.setLayoutManager(layoutManager);
        chipGroup.setAdapter(new MainTagAdapter(titles,
                (t) -> {
                     float y = ((RecyclerView) findViewById(R.id.rc)).getChildAt((Integer) t.getTag()).getY();
                    findViewById(R.id.nestedScrollView).post(new Runnable() {
                        @Override
                        public void run() {
                            NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollView);
                            nsv.fling(0);
                            nsv.smoothScrollTo(0, (int) y);
                        }
                    });
                }));

        RecyclerView rcAllView = findViewById(R.id.rc);
        rcAllView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = (new MainFragment1_1Adapter(titles));
        getNews();
        getCategory();
        getContent();
    }



    private void getNews() {
        FilterModel f = new FilterModel();
        f.RowPerPage = 1;
        ServiceExecute.execute(new NewsContentService(getContext()).getAll(f))
                .subscribe(new NtkObserver<ErrorException<NewsContentModel>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ErrorException<NewsContentModel> response) {
                        if (response.IsSuccess) {
                            adapter.put(0, response.ListItems);
                            if (adapter.getItemCount() == 3) {

                                ((RecyclerView) findViewById(R.id.rc)).setAdapter(adapter);
                                switcher.showContentView();
                            }
                        } else
                            Toasty.error(getContext(), response.ErrorMessage).show();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });
    }

    private void getContent() {
        FilterModel f = new FilterModel();
        f.RowPerPage = 20;
        ServiceExecute.execute(new HyperShopContentService(getContext()).getAllMicroService(f))
                .subscribe(new NtkObserver<ErrorException<HyperShopContentModel>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ErrorException<HyperShopContentModel> response) {
                        if (response.IsSuccess) {
                            adapter.put(2, response.ListItems);
                            if (adapter.getItemCount() == 3) {

                                ((RecyclerView) findViewById(R.id.rc)).setAdapter(adapter);
                                switcher.showContentView();
                            }
                        } else
                            Toasty.error(getContext(), response.ErrorMessage).show();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });
    }

    private void getCategory() {
        FilterModel f = new FilterModel();
        f.RowPerPage = 8;
        ServiceExecute.execute(new HyperShopCategoryService(getContext()).getAllMicroService(f))
                .subscribe(new NtkObserver<ErrorException<HyperShopCategoryModel>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ErrorException<HyperShopCategoryModel> response) {
                        if (response.IsSuccess) {
                            adapter.put(1, response.ListItems);
                            if (adapter.getItemCount() == 3) {
                                ((RecyclerView) findViewById(R.id.rc)).setAdapter(adapter);
                                switcher.showContentView();
                            }
                        } else
                            Toasty.error(getContext(), response.ErrorMessage).show();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toasty.error(getContext(), e.toString()).show();
                    }
                });

    }
}
