package ntk.android.ticketing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.entitymodel.news.NewsContentOtherInfoModel;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;

public class TabNewsAdapter extends BaseRecyclerAdapter<NewsContentOtherInfoModel,TabNewsAdapter.ViewHolder> {

    private final Context context;

    public TabNewsAdapter(Context context, List<NewsContentOtherInfoModel> arrayList) {
       super(arrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup,R.layout.row_recycler_tab);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        NewsContentOtherInfoModel item = list.get(position);
        holder.Btn.setText(item.Title);
        if (item.TypeId == 0) {
            holder.webView.loadData("<html dir=\"rtl\" lang=\"\"><body>" + item.HtmlBody + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        }
        holder.Ripple.setOnClickListener(v -> holder.webView.loadData("<html dir=\"rtl\" lang=\"\"><body>" + item.HtmlBody + "</body></html>", "text/html; charset=utf-8", "UTF-8"));
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
            webView.getSettings().setBuiltInZoomControls(true);
        }
    }
}
