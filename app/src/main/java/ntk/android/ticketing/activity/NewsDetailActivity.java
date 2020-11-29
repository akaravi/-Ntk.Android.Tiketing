package ntk.android.ticketing.activity;

import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import java9.util.function.BiFunction;
import java9.util.function.Function;
import ntk.android.base.activity.abstraction.AbstractionDetailActivity;
import ntk.android.base.dtomodel.core.ScoreClickDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.news.NewsCommentModel;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.entitymodel.news.NewsContentOtherInfoModel;
import ntk.android.base.services.news.NewsCommentService;
import ntk.android.base.services.news.NewsContentOtherInfoService;
import ntk.android.base.services.news.NewsContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.NewsAdapter;
import ntk.android.ticketing.adapter.NewsCommentAdapter;
import ntk.android.ticketing.adapter.TabNewsAdapter;

;

public class NewsDetailActivity extends AbstractionDetailActivity<NewsContentModel, NewsCommentModel, NewsContentOtherInfoModel> {


    @Override
    public Function<ScoreClickDtoModel, Observable<ErrorExceptionBase>> contentRateService() {
        return new NewsContentService(this)::scoreClick;
    }

    @Override
    public Function<Long, Observable<ErrorException<NewsContentModel>>> getOneContentService() {
        return new NewsContentService(this)::getOne;
    }

    @Override
    public Function<Long, Observable<ErrorException<NewsCommentModel>>> getCommentListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new NewsCommentService(this).getAll(Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<NewsContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new NewsContentOtherInfoService(this).getAll(Request);
        };
    }

    @Override
    public BiFunction<String, String, Observable<ErrorException<NewsCommentModel>>> addCommentService() {
        return (writer, comment) -> {
            NewsCommentModel model = new NewsCommentModel();
            model.Writer = writer;
            model.Comment = comment;
            return new NewsCommentService(this).add(model);
        };
    }

    @Override
    public Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService() {

        Function<Long, Observable<ErrorExceptionBase>> favorite;
        if (model.Favorited)
            favorite = new NewsContentService(this)::removeFavorite;
        else
            favorite = new NewsContentService(this)::addFavorite;
        return new Pair<>(favorite, () -> {
            model.Favorited = !model.Favorited;
            if (model.Favorited) {
                ((ImageView) findViewById(ntk.android.base.R.id.imgHeartDetail)).setImageResource(R.drawable.ic_fav_full);
            } else {
                ((ImageView) findViewById(ntk.android.base.R.id.imgHeartDetail)).setImageResource(R.drawable.ic_fav);
            }
        });
    }

    @Override
    public RecyclerView.Adapter createCommentAdapter(List<NewsCommentModel> listItems) {
        return new NewsCommentAdapter(this, listItems);
    }

    @Override
    public void SetDataOtherinfo(ErrorException<NewsContentOtherInfoModel> model) {

        if (model.ListItems == null || model.ListItems.size() == 0) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 3;
            return;
        }
        List<NewsContentOtherInfoModel> Info = new ArrayList<>();

        for (NewsContentOtherInfoModel ai : model.ListItems) {
            switch (ai.TypeId) {
                case 21:
                    ((TextView) findViewById(ntk.android.base.R.id.lblAllMenuDetail)).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    ((TextView) findViewById(ntk.android.base.R.id.lblMenuDetail)).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                case 22:
                    throw new RuntimeException(("OtherInfo type 22"));
//                    Lbls.get(9).setText(ai.Title);
//                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
//                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
//                    Lbls.get(8).setText(Html.fromHtml(ai.HtmlBody));
//                    break;
                case 23:
                    throw new RuntimeException(("OtherInfo type 23"));
//                    Lbls.get(11).setText(ai.Title);
//                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
//                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
//                    Lbls.get(10).setText(Html.fromHtml(ai.HtmlBody));
//                    break;
                default:
                    Info.add(ai);
                    break;
            }
        }
        TabNewsAdapter adapter = new TabNewsAdapter(NewsDetailActivity.this, Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void bindContentData(ErrorException<NewsContentModel> model) {
        ImageLoader.getInstance().displayImage(model.Item.LinkMainImageIdSrc, ImgHeader);
        ((TextView) findViewById(ntk.android.base.R.id.lblTitleDetail)).setText(model.Item.Title);
        ((TextView) findViewById(ntk.android.base.R.id.lblNameCommandDetail)).setText(model.Item.Title);
        ((TextView)findViewById(ntk.android.base.R.id.lblValueSeenDetail)).setText(String.valueOf(model.Item.ViewCount));
        double rating = 0.0;
        int sumClick = model.Item.ViewCount;
        if (model.Item.ViewCount == 0) sumClick = 1;
        if (model.Item.ScoreSumPercent / sumClick > 0 && model.Item.ScoreSumPercent / sumClick <= 10) {
            rating = 0.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 10 && model.Item.ScoreSumPercent / sumClick <= 20) {
            rating = 1.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 20 && model.Item.ScoreSumPercent / sumClick <= 30) {
            rating = 1.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 30 && model.Item.ScoreSumPercent / sumClick <= 40) {
            rating = 2.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 40 && model.Item.ScoreSumPercent / sumClick <= 50) {
            rating = 2.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 50 && model.Item.ScoreSumPercent / sumClick <= 60) {
            rating = 3.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 60 && model.Item.ScoreSumPercent / sumClick <= 70) {
            rating = 3.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 70 && model.Item.ScoreSumPercent / sumClick <= 80) {
            rating = 4.0;
        } else if (model.Item.ScoreSumPercent / sumClick > 80 && model.Item.ScoreSumPercent / sumClick <= 90) {
            rating = 4.5;
        } else if (model.Item.ScoreSumPercent / sumClick > 90) {
            rating = 5.0;
        }
        Rate.setRating((float) rating);
        if (model.Item.Body != null)
            webViewBody.loadData("<html dir=\"rtl\" lang=\"\"><body>" + model.Item.Body + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        if (model.Item.Favorited) {
            ((ImageView) findViewById(ntk.android.base.R.id.imgHeartDetail)).setImageResource(R.drawable.ic_fav_full);
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        Rv.setLayoutManager(manager);

        NewsAdapter adBlog = new NewsAdapter(this, model.ListItems);
        Rv.setAdapter(adBlog);
        adBlog.notifyDataSetChanged();
        if (model.ListItems.isEmpty()) {
            findViewById(ntk.android.base.R.id.lblMenuDetail).setVisibility(View.GONE);
            findViewById(ntk.android.base.R.id.lblAllMenuDetail).setVisibility(View.GONE);
        }
    }

    @Override
    protected String createShareMassage() {
        String message = model.Title + "\n" + model.Description + "\n";
        if (model.Body != null) {
            message = message + Html.fromHtml(model.Body
                    .replace("<p>", "")
                    .replace("</p>", ""));
        }
        return message;
    }
}
