package ntk.android.ticketing.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import com.balysv.materialripple.MaterialRippleLayout;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.ticketing.R;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.news.model.NewsContentOtherInfo;

public class AdTabNews extends RecyclerView.Adapter<AdTabNews.ViewHolder> {

    private List<NewsContentOtherInfo> arrayList;
    private Context context;

    public AdTabNews(Context context, List<NewsContentOtherInfo> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_tab, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Btn.setText(arrayList.get(position).Title);
        if (arrayList.get(position).TypeId == 0) {
            holder.webView.loadDataWithBaseURL("", arrayList.get(position).HtmlBody, "text/html", "UTF-8", "");
        }
        holder.Ripple.setOnClickListener(v -> holder.webView.loadDataWithBaseURL("", arrayList.get(position).HtmlBody, "text/html", "UTF-8", ""));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.BtnRecyclerTab)
        Button Btn;

        @BindView(R.id.RippleBtnRecyclerTab)
        MaterialRippleLayout Ripple;

        @BindView(R.id.WebViewActDetailNews)
        WebView webView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Btn.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            webView.getSettings().setJavaScriptEnabled(true);
        }
    }
}
