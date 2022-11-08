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
import ntk.android.base.activity.common.BaseFilterModelListActivity;
import ntk.android.base.activity.common.BaseFilterModelWithCategoryActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.entitymodel.news.NewsCategoryModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsCategoryService;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.NewsAdapter;
import ntk.android.ticketing.adapter.NewsCategoryContentAdapter;

public class NewsListActivity extends BaseFilterModelWithCategoryActivity<NewsContentModel, NewsCategoryModel> {
    @Override
    protected void onCreated() {
        super.onCreated();
        ((TextView) findViewById(R.id.lblTitle)).setText(R.string.per_news);
    }

    @Override
    public Function<FilterModel, Observable<ErrorException<NewsContentModel>>> getService() {
        return new NewsContentService(this)::getAll;
    }


    @Override
    public RecyclerView.Adapter createAdapter() {
        return new NewsAdapter(this, models);
    }

    @Override
    public void ClickSearch() {
        startActivity(new Intent(this, NewsSearchActivity.class));
    }

    @Override
    protected void onCategoryResponse(ErrorException<NewsCategoryModel> response, Dialog dialog) {
        RecyclerView rc = dialog.findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(NewsListActivity.this, LinearLayoutManager.VERTICAL, false));
        rc.setAdapter(new NewsCategoryContentAdapter(response.ListItems, newsCategoryModel -> {
            Intent i = new Intent(NewsListActivity.this, NewsWithCategoryUsedActivity.class);
            i.putExtra(Extras.EXTRA_FIRST_ARG, new Gson().toJson(request));
            i.putExtra(Extras.EXTRA_SECOND_ARG, newsCategoryModel.Id);
            i.putExtra(Extras.Extra_THIRD_ARG, "مرتبط با  " + newsCategoryModel.Title);
            startActivity(i);
            return null;
        }));
    }

    @Override
    public Function<FilterModel, Observable<ErrorException<NewsCategoryModel>>> getCatService() {
        return new NewsCategoryService(this)::getAll;
    }
}

