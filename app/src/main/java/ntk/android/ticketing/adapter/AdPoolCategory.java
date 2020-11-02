package ntk.android.ticketing.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.PoolingDetailActivity;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.baseModel.Filters;
import ntk.android.base.api.pooling.entity.PoolingCategory;
import ntk.android.base.api.pooling.model.PoolingContentListRequest;

public class AdPoolCategory extends RecyclerView.Adapter<AdPoolCategory.ViewHolder> {

    private List<PoolingCategory> arrayList;
    private Context context;

    public AdPoolCategory(Context context, List<PoolingCategory> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pooling, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Title);
        holder.Root.setOnClickListener(v -> {
            PoolingContentListRequest request = new PoolingContentListRequest();
            List<Filters> filters = new ArrayList<>();
            Filters f = new Filters();
            f.PropertyName = "LinkCategoryId";
            f.IntValue1 = arrayList.get(position).Id;
            filters.add(f);
            request.filters = filters;
            Intent intent = new Intent(context, PoolingDetailActivity.class);
            intent.putExtra("Request", new Gson().toJson(request));
            intent.putExtra("Title", arrayList.get(position).Title);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblRowRecyclerPooling)
        TextView LblTitle;

        @BindView(R.id.rootPooling)
        LinearLayout Root;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}