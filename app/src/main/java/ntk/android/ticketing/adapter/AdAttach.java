package ntk.android.ticketing.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.event.RemoveAttachEvent;
import ntk.android.base.utill.FontManager;

public class AdAttach extends RecyclerView.Adapter<AdAttach.ViewHolder> {

    private List<String> arrayList;
    private Context context;

    public AdAttach(Context context, List<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_attach, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String[] strs = arrayList.get(position).split("/");
        String FileName = strs[strs.length - 1];
        holder.Lbl.setText(FileName);
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RemoveAttachEvent(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblTitleRecyclerAttach)
        TextView Lbl;

        @BindView(R.id.imgRemoveRecyclerAttach)
        Button Delete;

        @BindView(R.id.imgRecyclerAttach)
        ImageView Img;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbl.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
