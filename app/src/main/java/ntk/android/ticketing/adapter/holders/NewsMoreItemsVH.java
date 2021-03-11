package ntk.android.ticketing.adapter.holders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.entitymodel.news.NewsContentModel;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.NewsListActivity;
import ntk.android.ticketing.adapter.NewsAdapter;

public class NewsMoreItemsVH extends RecyclerView.ViewHolder {
    NewsMoreItemsVH(@NonNull View itemView) {
        super(itemView);
    }

    public static RecyclerView.ViewHolder create(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_recycler_more, parent, false);
        return new NewsMoreItemsVH(inflate);
    }

    public void bind(List<NewsContentModel> list, String title) {
        ((TextView) itemView.findViewById(R.id.moreTextTitle)).setText(title);
        itemView.findViewById(R.id.moreBtn).setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), NewsListActivity.class);
            itemView.getContext().startActivity(intent);
        });
        RecyclerView rc = itemView.findViewById(R.id.moreRc);
        rc.setNestedScrollingEnabled(true);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rc.setAdapter(new NewsAdapter(itemView.getContext(), list));
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rc);
    }
}
