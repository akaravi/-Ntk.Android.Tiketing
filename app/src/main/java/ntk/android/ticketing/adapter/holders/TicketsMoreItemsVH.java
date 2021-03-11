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

import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.base.adapter.common.TicketAdapter;
import ntk.android.base.entitymodel.ticketing.TicketingTaskModel;
import ntk.android.ticketing.R;

public class TicketsMoreItemsVH extends RecyclerView.ViewHolder {
    TicketsMoreItemsVH(@NonNull View itemView) {
        super(itemView);
    }

    public static RecyclerView.ViewHolder create(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_recycler_more, parent, false);
        return new TicketsMoreItemsVH(inflate);
    }

    public void bind(BaseActivity base, List<TicketingTaskModel> list, String title) {
        ((TextView) itemView.findViewById(R.id.moreTextTitle)).setText(title);
        itemView.findViewById(R.id.moreBtn).setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), TicketListActivity.class);
            itemView.getContext().startActivity(intent);
        });
        RecyclerView rc = itemView.findViewById(R.id.moreRc);
        rc.setNestedScrollingEnabled(true);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rc.setAdapter(new TicketAdapter(base, list));
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rc);
    }
}
