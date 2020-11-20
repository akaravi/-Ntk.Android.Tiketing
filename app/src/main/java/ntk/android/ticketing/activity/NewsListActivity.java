package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.activity.abstraction.AbstractionListActivity;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.NewsAdapter;

public class NewsListActivity extends AbstractionListActivity<NewsContentModel> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.lblTitle)).setText("اخبار");
    }

    @Override
    public Function<FilterDataModel, Observable<ErrorException<NewsContentModel>>> getService() {
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

}
