package ntk.android.ticketing.activity;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.BiFunction;
import ntk.android.base.activity.common.BaseFilterModelCategoryUsedListActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.ticketing.adapter.NewsAdapter;

public class NewsWithCategoryUsedActivity extends BaseFilterModelCategoryUsedListActivity<NewsContentModel, Long> {


    @Override
    public RecyclerView.Adapter createAdapter() {
        return new NewsAdapter(this, models);
    }


    @Override
    public Long convertID() {
        return (Long) id;
    }

    @Override
    public BiFunction<Long, FilterModel, Observable<ErrorException<NewsContentModel>>> getService() {
        return new NewsContentService(this)::getAllWithHierarchyCategoryId;
    }
}
