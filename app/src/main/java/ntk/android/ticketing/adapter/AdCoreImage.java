package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.NewsDetailActivity;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.news.entity.NewsContent;
import ntk.android.base.api.news.model.NewsContentViewRequest;

public class AdCoreImage extends RecyclerView.Adapter<AdCoreImage.ViewHolder> {
    private List<NewsContent> list;
    private Context context;

    public AdCoreImage(Context context, List<NewsContent> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(list.get(position).imageSrc, holder.Img, options);
        holder.Lbl.setText(list.get(position).Title);
        holder.Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsContentViewRequest request = new NewsContentViewRequest();
                request.Id = list.get(position).Id;
                context.startActivity(new Intent(context, NewsDetailActivity.class).putExtra("Request", new Gson().toJson(request)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ImageRecyclerImage)
        ImageView Img;

        @BindView(R.id.LblRecyclerImage)
        TextView Lbl;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbl.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
