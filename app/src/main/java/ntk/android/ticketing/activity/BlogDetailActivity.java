package ntk.android.ticketing.activity;

import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import ntk.android.base.entitymodel.blog.BlogCommentModel;
import ntk.android.base.entitymodel.blog.BlogContentModel;
import ntk.android.base.entitymodel.blog.BlogContentOtherInfoModel;
import ntk.android.base.services.blog.BlogCommentService;
import ntk.android.base.services.blog.BlogContentOtherInfoService;
import ntk.android.base.services.blog.BlogContentService;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.BlogAdapter;
import ntk.android.ticketing.adapter.BlogCommentAdapter;
import ntk.android.ticketing.adapter.TabBlogAdapter;

public class BlogDetailActivity extends AbstractionDetailActivity<BlogContentModel, BlogCommentModel, BlogContentOtherInfoModel> {

    @Override
    public Function<ScoreClickDtoModel, Observable<ErrorExceptionBase>> contentRateService() {
        return new BlogContentService(this)::scoreClick;
    }

    @Override
    public Function<Long, Observable<ErrorException<BlogContentModel>>> getOneContentService() {
        return new BlogContentService(this)::getOne;
    }

    @Override
    public Function<Long, Observable<ErrorException<BlogCommentModel>>> getCommentListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new BlogCommentService(this).getAll(Request);
        };
    }

    @Override
    public Function<Long, Observable<ErrorException<BlogContentOtherInfoModel>>> getOtherInfoListService() {
        return linkLongId -> {
            FilterDataModel Request = new FilterDataModel();
            Filters f = new Filters();
            f.PropertyName = "LinkContentId";
            f.IntValue1 = linkLongId;
            Request.addFilter(f);
            return new BlogContentOtherInfoService(this).getAll(Request);
        };
    }

    @Override
    public Pair<Function<Long, Observable<ErrorExceptionBase>>, Runnable> getFavoriteService() {
        Function<Long, Observable<ErrorExceptionBase>> favorite;
        if (model.Favorited)
            favorite = new BlogContentService(this)::removeFavorite;
        else
            favorite = new BlogContentService(this)::addFavorite;
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
    public BiFunction<String, String, Observable<ErrorException<BlogCommentModel>>> addCommentService() {
        return (writer, comment) -> {
            BlogCommentModel model = new BlogCommentModel();
            model.Writer = writer;
            model.Comment = comment;
            return new BlogCommentService(this).add(model);
        };
    }

    @Override
    public RecyclerView.Adapter createCommentAdapter(List<BlogCommentModel> listItems) {
        return new BlogCommentAdapter(this,listItems);
    }

    @Override
    public void SetDataOtherinfo(ErrorException<BlogContentOtherInfoModel> model) {

        if (model.ListItems == null || model.ListItems.size() == 0) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 3;
            return;
        }
        List<BlogContentOtherInfoModel> Info = new ArrayList<>();

        for (BlogContentOtherInfoModel ai : model.ListItems) {
            switch (ai.TypeId) {
                case 21:
                    Lbls.get(7).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(6).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                case 22:
                    Lbls.get(9).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(8).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                case 23:
                    Lbls.get(11).setText(ai.Title);
                    ai.HtmlBody = ai.HtmlBody.replace("<p>", "");
                    ai.HtmlBody = ai.HtmlBody.replace("</p>", "");
                    Lbls.get(10).setText(Html.fromHtml(ai.HtmlBody));
                    break;
                default:
                    Info.add(ai);
                    break;
            }
        }
        TabBlogAdapter adapter = new TabBlogAdapter(BlogDetailActivity.this, Info);
        RvTab.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void bindContentData(ErrorException<BlogContentModel> model) {
        ImageLoader.getInstance().displayImage(model.Item.MainImageSrc, ImgHeader);
        Lbls.get(0).setText(model.Item.Title);
        Lbls.get(1).setText(model.Item.Title);
        Lbls.get(3).setText(String.valueOf(model.Item.ViewCount));
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
            ((ImageView) findViewById(R.id.imgHeartActDetailBlog)).setImageResource(R.drawable.ic_fav_full);
        }

        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        Rv.setLayoutManager(manager);

        BlogAdapter adBlog = new BlogAdapter(this, model.ListItems);
        Rv.setAdapter(adBlog);
        adBlog.notifyDataSetChanged();
        if (model.ListItems.isEmpty()) {
            Lbls.get(5).setVisibility(View.GONE);
            Lbls.get(4).setVisibility(View.GONE);
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

