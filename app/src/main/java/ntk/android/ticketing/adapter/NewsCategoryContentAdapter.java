package ntk.android.ticketing.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import java9.util.function.Function;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.entitymodel.news.NewsCategoryModel;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;

public class NewsCategoryContentAdapter extends BaseRecyclerAdapter<NewsCategoryModel, NewsCategoryContentAdapter.ViewHolder> {
    Function<NewsCategoryModel, Void> function;

    public NewsCategoryContentAdapter(List<NewsCategoryModel> list, Function<NewsCategoryModel, Void> func) {
        super(list);
        function = func;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(parent, R.layout.common_category));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsCategoryModel item = getItem(position);
        holder.tv.setText(item.Title);
        if (item.LinkMainImageIdSrc != null && !item.LinkMainImageIdSrc.equals(""))
            loadImage(item.LinkMainImageIdSrc, holder.image);
        else
            holder.image.setImageResource(R.drawable.categoty);
        holder.itemView.setOnClickListener(v -> function.apply(item));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.textView);
            image = itemView.findViewById(R.id.image);
            tv.setTypeface(FontManager.T1_Typeface(itemView.getContext()));
        }
    }
}
