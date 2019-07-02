package ntk.android.ticketing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.ActTicketAnswer;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.model.Filters;
import ntk.base.api.ticket.model.TicketingAnswerListRequest;
import ntk.base.api.ticket.model.TicketingTask;

public class AdTicket extends RecyclerView.Adapter<AdTicket.ViewHolder> {

    private List<TicketingTask> arrayList;
    private Context context;

    public AdTicket(Context context, ArrayList<TicketingTask> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_ticket, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(arrayList.get(position).Title);
        holder.Lbls.get(1).setText(arrayList.get(position).LinkTicketTypeId);
        holder.Lbls.get(3).setText(arrayList.get(position).DeviceInformation);
        switch (arrayList.get(position).TicketStatus) {
            case 1:
                holder.Lbls.get(2).setBackgroundResource(R.drawable.circlegreen);
                holder.Lbls.get(2).setText("پاسخ داده شد");
                break;
            case 2:
                holder.Lbls.get(2).setBackgroundResource(R.drawable.circlered);
                holder.Lbls.get(2).setText("در حال رسیدگی");
                break;
            case 3:
                holder.Lbls.get(2).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(2).setText("انتظار پاسخ");
                break;
            case 4:
                holder.Lbls.get(2).setBackgroundResource(R.drawable.circle_oranje);
                holder.Lbls.get(2).setText("پاسخ مشتری");
                break;
            case 5:
                holder.Lbls.get(2).setBackgroundResource(R.drawable.circle_blue_full);
                holder.Lbls.get(2).setText("بسته شد");
                break;
        }
        holder.Root.setOnClickListener(v -> {
            TicketingAnswerListRequest request = new TicketingAnswerListRequest();
            List<Filters> filters = new ArrayList<>();
            Filters f = new Filters();
            f.PropertyName = "LinkTicketId";
            f.IntValue1 = arrayList.get(position).Id;
            filters.add(f);
            request.filters = filters;
            Intent intent = new Intent(context, ActTicketAnswer.class);
            intent.putExtra("Request", new Gson().toJson(request));
            intent.putExtra("TicketId", arrayList.get(position).Id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R.id.lblNameRecyclerTicket,
                R.id.lblTypeRecyclerTicket,
                R.id.lblStateRecyclerTicket,
                R.id.lblDateRecyclerTicket,
                R.id.lblTypeDepartmanRecyclerTicket})
        List<TextView> Lbls;

        @BindView(R.id.rootTicket)
        LinearLayout Root;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(3).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(4).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
