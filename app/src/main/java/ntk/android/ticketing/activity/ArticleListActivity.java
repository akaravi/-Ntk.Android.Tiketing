package ntk.android.ticketing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.Extras;
import ntk.android.base.activity.common.BaseFilterModelWithCategoryActivity;
import ntk.android.base.entitymodel.article.ArticleCategoryModel;
import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.services.article.ArticleCategoryService;
import ntk.android.base.services.article.ArticleContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.ArticleAdapter;
import ntk.android.ticketing.adapter.ArticleCategoryContentAdapter;

public class ArticleListActivity extends BaseFilterModelWithCategoryActivity<ArticleContentModel, ArticleCategoryModel> {
    @Override
    protected void onCreated() {
        super.onCreated();
        ((TextView) findViewById(R.id.lblTitle)).setText(R.string.per_article);
    }

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

    @Override
    protected void onCategoryResponse(ErrorException<ArticleCategoryModel> response, Dialog dialog) {
        RecyclerView rc = dialog.findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(ArticleListActivity.this, LinearLayoutManager.VERTICAL, false));
        rc.setAdapter(new ArticleCategoryContentAdapter(response.ListItems, articleCategoryModel -> {
            Intent i = new Intent(ArticleListActivity.this, ArticleWithCategoryUsedActivity.class);
            i.putExtra(Extras.EXTRA_FIRST_ARG, new Gson().toJson(request));
            i.putExtra(Extras.EXTRA_SECOND_ARG, articleCategoryModel.Id);
            i.putExtra(Extras.Extra_THIRD_ARG, "مرتبط با  " + articleCategoryModel.Title);
            startActivity(i);
            return null;
        }));
    }

    @Override
    public Function<FilterModel, Observable<ErrorException<ArticleCategoryModel>>> getCatService() {
        return new ArticleCategoryService(this)::getAll;
    }
}
