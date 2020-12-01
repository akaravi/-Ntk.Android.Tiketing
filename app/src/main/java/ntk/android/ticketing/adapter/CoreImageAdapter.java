package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.base.Extras;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.NewsDetailActivity;

public class CoreImageAdapter extends BaseRecyclerAdapter<NewsContentModel,CoreImageAdapter.ViewHolder> {

    private final Context context;

    public CoreImageAdapter(Context context, List<NewsContentModel> list) {
        super(list);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup,R.layout.row_recycler_image);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

       loadImage(list.get(position).LinkMainImageIdSrc, holder.Img);
        holder.Lbl.setText(list.get(position).Title);
        holder.Img.setOnClickListener(v -> context.startActivity(new Intent(context, NewsDetailActivity.class)
                .putExtra(Extras.EXTRA_FIRST_ARG, list.get(position).Id)));

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
