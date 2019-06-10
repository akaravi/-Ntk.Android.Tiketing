package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.ActDetailNews;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.news.model.NewsContent;
import ntk.base.api.news.model.NewsContentViewRequest;

public class AdNews extends RecyclerView.Adapter<AdNews.ViewHolder> {

    private List<NewsContent> arrayList;
    private Context context;

    public AdNews(Context context, List<NewsContent> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_news, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Title);
        holder.LblDescrption.setText(arrayList.get(position).description);
        holder.LblLike.setText(String.valueOf(arrayList.get(position).viewCount));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(arrayList.get(position).imageSrc, holder.Img, options, new ImageLoadingListener() {
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
        double rating = 5.0;
        int sumClick = arrayList.get(position).ScoreSumClick;
        if (arrayList.get(position).ScoreSumClick == 0) sumClick = 1;
        switch (arrayList.get(position).ScoreSumPercent / sumClick) {
            case 10:
                rating = 0.5;
                break;
            case 20:
                rating = 1.0;
                break;
            case 30:
                rating = 1.5;
                break;
            case 40:
                rating = 2.0;
                break;
            case 50:
                rating = 2.5;
                break;
            case 60:
                rating = 3.0;
                break;
            case 70:
                rating = 3.5;
                break;
            case 80:
                rating = 4.0;
                break;
            case 90:
                rating = 4.5;
                break;
            case 100:
                rating = 5.0;
                break;
        }
        holder.Rate.setRating((float) rating);
        holder.Root.setOnClickListener(view -> {
            Intent intent = new Intent(context, ActDetailNews.class);
            NewsContentViewRequest request = new NewsContentViewRequest();
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

        @BindView(R.id.lblTitleRowRecyclerNews)
        TextView LblTitle;

        @BindView(R.id.lblDescriptionRowRecyclerNews)
        TextView LblDescrption;

        @BindView(R.id.lblLikeRowRecyclerNews)
        TextView LblLike;

        @BindView(R.id.imgRowRecyclerNews)
        ImageView Img;

        @BindView(R.id.ratingBarRowRecyclerNews)
        RatingBar Rate;

        @BindView(R.id.rootNews)
        CardView Root;

        @BindView(R.id.ProgressRecyclerNews)
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
