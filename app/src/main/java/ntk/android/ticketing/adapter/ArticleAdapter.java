package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.base.Extras;
import ntk.android.base.adapter.BaseRecyclerAdapter;

import ntk.android.base.entitymodel.article.ArticleContentModel;
import ntk.android.base.services.base.CmsApiScoreApi;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.ArticleDetailActivity;

public class ArticleAdapter extends BaseRecyclerAdapter<ArticleContentModel, ArticleAdapter.ViewHolder> {


    private final Context context;

    public ArticleAdapter(Context context, List<ArticleContentModel> arrayList) {
        super(arrayList);
        this.context = context;
        drawable=R.drawable.article_place_holder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_article, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ArticleContentModel item = getItem(position);
        holder.LblTitle.setText(item.Title);
        holder.LblDescrption.setText(item.Description);
        holder.LblLike.setText(String.valueOf(item.ViewCount));
        loadImage(item.LinkMainImageIdSrc, holder.Img, holder.Progress);
        double rating = CmsApiScoreApi.CONVERT_TO_RATE(item.ViewCount, item.ScoreSumPercent);
        holder.Rate.setRating((float) rating);
        holder.Root.setOnClickListener(view -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra(Extras.EXTRA_FIRST_ARG, (item.Id));
            context.startActivity(intent);
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblTitleRowRecyclerArticle)
        TextView LblTitle;

        @BindView(R.id.lblDescriptionRowRecyclerArticle)
        TextView LblDescrption;

        @BindView(R.id.lblLikeRowRecyclerArticle)
        TextView LblLike;

        @BindView(R.id.imgRowRecyclerArticle)
        ImageView Img;

        @BindView(R.id.ratingBarRowRecyclerArticle)
        RatingBar Rate;

        @BindView(R.id.rootArticle)
        CardView Root;

        @BindView(R.id.ProgressRecyclerArticle)
        ProgressBar Progress;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LblTitle.setTypeface(FontManager.T1_Typeface(context));
            LblDescrption.setTypeface(FontManager.T1_Typeface(context));
            LblLike.setTypeface(FontManager.T1_Typeface(context));
            Progress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
    }
}
