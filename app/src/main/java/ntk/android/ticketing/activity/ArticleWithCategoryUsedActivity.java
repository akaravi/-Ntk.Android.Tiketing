package ntk.android.ticketing.activity;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.BiFunction;
import ntk.android.base.activity.common.BaseFilterModelCategoryUsedListActivity;
import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.ticketing.adapter.ArticleAdapter;

public class ArticleWithCategoryUsedActivity extends BaseFilterModelCategoryUsedListActivity<ArticleContentModel, Long> {


    @Override
    public RecyclerView.Adapter createAdapter() {
        return new ArticleAdapter(this, models);
    }


    @Override
    public Long convertID() {
        return (Long) id;
    }

    @Override
    public BiFunction<Long, FilterModel, Observable<ErrorException<ArticleContentModel>>> getService() {
        return new ArticleContentService(this)::getAllWithHierarchyCategoryId;
    }
}