package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.base.entitymodel.blog.BlogContentModel;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.BlogDetailActivity;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private List<BlogContentModel> arrayList;
    private Context context;

    public BlogAdapter(Context context, List<BlogContentModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_blog, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Title);
        holder.LblDescrption.setText(arrayList.get(position).Description);
        holder.LblLike.setText(String.valueOf(arrayList.get(position).ViewCount));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(arrayList.get(position).MainImageSrc, holder.Img, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.Progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.Progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        double rating = 0.0;
        int sumClick = arrayList.get(position).ViewCount;
        if (arrayList.get(position).ViewCount == 0) sumClick = 1;
        if (arrayList.get(position).ScoreSumPercent / sumClick > 0 && arrayList.get(position).ScoreSumPercent / sumClick <= 10) {
            rating = 0.5;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 10 && arrayList.get(position).ScoreSumPercent / sumClick <= 20) {
            rating = 1.0;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 20 && arrayList.get(position).ScoreSumPercent / sumClick <= 30) {
            rating = 1.5;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 30 && arrayList.get(position).ScoreSumPercent / sumClick <= 40) {
            rating = 2.0;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 40 && arrayList.get(position).ScoreSumPercent / sumClick <= 50) {
            rating = 2.5;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 50 && arrayList.get(position).ScoreSumPercent / sumClick <= 60) {
            rating = 3.0;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 60 && arrayList.get(position).ScoreSumPercent / sumClick <= 70) {
            rating = 3.5;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 70 && arrayList.get(position).ScoreSumPercent / sumClick <= 80) {
            rating = 4.0;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 80 && arrayList.get(position).ScoreSumPercent / sumClick <= 90) {
            rating = 4.5;
        } else if (arrayList.get(position).ScoreSumPercent / sumClick > 90) {
            rating = 5.0;
        }
        holder.Rate.setRating((float) rating);
        holder.Root.setOnClickListener(view -> {
            Intent intent = new Intent(context, BlogDetailActivity.class);
            BlogContentModel request = new BlogContentModel();
            request.Id = arrayList.get(position).Id;
            intent.putExtra("Request", new Gson().toJson(request));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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
