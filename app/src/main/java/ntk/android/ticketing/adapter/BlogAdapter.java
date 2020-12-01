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
import ntk.android.base.entitymodel.blog.BlogContentModel;
import ntk.android.base.services.base.CmsApiScoreApi;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.BlogDetailActivity;

public class BlogAdapter extends BaseRecyclerAdapter<BlogContentModel, BlogAdapter.ViewHolder> {


    private final Context context;

    public BlogAdapter(Context context, List<BlogContentModel> arrayList) {
        super(arrayList);

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_blog, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BlogContentModel item = getItem(position);
        holder.LblTitle.setText(item.Title);
        holder.LblDescrption.setText(item.Description);
        holder.LblLike.setText(String.valueOf(item.ViewCount));
        loadImage(item.LinkMainImageIdSrc, holder.Img, holder.Progress);
        double rating = CmsApiScoreApi.CONVERT_TO_RATE(item.ViewCount, item.ScoreSumPercent);
        holder.Rate.setRating((float) rating);
        holder.Root.setOnClickListener(view -> {
            Intent intent = new Intent(context, BlogDetailActivity.class);
            intent.putExtra(Extras.EXTRA_FIRST_ARG, (item.Id));
            context.startActivity(intent);
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblTitleRowRecyclerBlog)
        TextView LblTitle;

        @BindView(R.id.lblDescriptionRowRecyclerBlog)
        TextView LblDescrption;

        @BindView(R.id.lblLikeRowRecyclerBlog)
        TextView LblLike;

        @BindView(R.id.imgRowRecyclerBlog)
        ImageView Img;

        @BindView(R.id.ratingBarRowRecyclerBlog)
        RatingBar Rate;

        @BindView(R.id.rootBlog)
        CardView Root;

        @BindView(R.id.ProgressRecyclerBlog)
        ProgressBar Progress;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            LblDescrption.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            LblLike.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Progress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
    }
}
