package ntk.android.ticketing.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.activity.common.BaseFilterModelListActivity;
import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.ticketing.adapter.ArticleAdapter;

public class ArticleListActivity extends BaseFilterModelListActivity<ArticleContentModel> {


    @Override
    public Function<FilterModel, Observable<ErrorException<ArticleContentModel>>> getService() {
        return new ArticleContentService(this)::getAll;
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        return new ArticleAdapter(this, models);
    }

    @Override
    public void ClickSearch() {
        startActivity(new Intent(this, ArticleSearchActivity.class));
    }

}
