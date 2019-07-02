package ntk.android.ticketing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.ticket.model.TicketingAnswer;

public class AdTicketAnswer extends RecyclerView.Adapter<AdTicketAnswer.ViewHolder> {

    private List<TicketingAnswer> arrayList;
    private Context context;

    public AdTicketAnswer(Context context, List<TicketingAnswer> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_ticket_answer, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(1).setText(arrayList.get(position).HtmlBody + "");
        holder.Lbls.get(2).setText(AppUtill.GregorianToPersian(arrayList.get(position).CreatedDate) + "");
        if (arrayList.get(position).UpdatedBy != null) {
            holder.Lbls.get(3).setText(arrayList.get(position).UpdatedBy + "");
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R.id.lblNameRecyclerTicketAnswer,
                R.id.lblTypeRecyclerTicketAnswer,
                R.id.lblDateRecyclerTicketAnswer,
                R.id.lblTypeDepartmanRecyclerTicketAnswer})
        List<TextView> Lbls;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(3).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
