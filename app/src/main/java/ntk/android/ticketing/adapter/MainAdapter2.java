package ntk.android.ticketing.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.model.PanelViewModel;


public class MainAdapter2 extends BaseRecyclerAdapter<PanelViewModel, MainAdapter2.vh> {


    PanelInterface myInterface;

    public MainAdapter2(List<PanelViewModel> PanelViewModels, PanelInterface panelInterface) {
        super(PanelViewModels);
        myInterface = panelInterface;
    }

    @Override
    public vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vh(inflate(parent, R.layout.item_main_list_4));
    }


    @Override
    public void onBindViewHolder(@NonNull vh holder, int position) { // called for every row in recycler view

        final PanelViewModel curr = getItem(position);
        if (curr.Title != null) {
            holder.setData(curr, position);

            holder.itemView.setOnClickListener(v -> myInterface.OnCardClick(curr));
        }

    }


    public class vh extends RecyclerView.ViewHolder {
        ImageView listimage;
        TextView listtitle;
        TextView badge;
        ImageView lockImae;

        public vh(View itemView) {
            super(itemView);

            listimage = (ImageView) itemView.findViewById(R.id.listimage);
            listtitle = (TextView) itemView.findViewById(R.id.listtitle);
            badge = (TextView) itemView.findViewById(R.id.badge);
            lockImae = itemView.findViewById(R.id.luckImage);

            listtitle.setTypeface(FontManager.T1_Typeface(itemView.getContext()));
        }

        public void setData(PanelViewModel curr, int position) {
            listimage.setImageResource(curr.ImageId);
            listtitle.setText(curr.Title);
            if (curr.badgeCount > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText("" + curr.badgeCount);
            } else
                badge.setVisibility(View.GONE);
        }

    }


}