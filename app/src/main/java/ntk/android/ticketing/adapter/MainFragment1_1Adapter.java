
package ntk.android.ticketing.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ntk.android.ticketing.adapter.holders.ArticlesMoreItemsVH;
import ntk.android.ticketing.adapter.holders.NewsMoreItemsVH;
import ntk.android.ticketing.adapter.holders.TicketsMoreItemsVH;


public class MainFragment1_1Adapter extends RecyclerView.Adapter {
    HashMap<Integer, List> models;
    List<String> strings;

    public MainFragment1_1Adapter(List<String> titles) {
        strings = titles;
        models = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return NewsMoreItemsVH.create(parent);
            case 1:
                return TicketsMoreItemsVH.create(parent);
            default:
                return ArticlesMoreItemsVH.create(parent);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                ((NewsMoreItemsVH) holder).bind(models.get(viewType), strings.get(position));
                break;
                case 1://todo
                ((TicketsMoreItemsVH) holder).bind(null,models.get(viewType), strings.get(position));
                break;
            case 3:
                ((ArticlesMoreItemsVH) holder).bind(models.get(viewType), strings.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        List<Integer> keys = new ArrayList(models.keySet());
        return keys.get(position);
    }

    public void put(int i, List<?> listItems) {
        models.put(i, listItems);
    }
}
