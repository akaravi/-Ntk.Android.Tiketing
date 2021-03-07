package ntk.android.ticketing.activity;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.activity.abstraction.AbstractSearchActivity;
import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.ticketing.adapter.ArticleAdapter;

public class ArticleSearchActivity extends AbstractSearchActivity<ArticleContentModel> {

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new ArticleAdapter(this, models);
    }

    @Override
    public Function<FilterModel, Observable<ErrorException<ArticleContentModel>>> getService() {
        return new ArticleContentService(this)::getAll;
    }


}
