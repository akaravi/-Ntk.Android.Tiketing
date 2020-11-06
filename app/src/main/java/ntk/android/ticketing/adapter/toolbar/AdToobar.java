package ntk.android.ticketing.adapter.toolbar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.base.utill.EasyPreference;
import ntk.android.ticketing.R;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.ticketing.event.toolbar.EVHamberMenuClick;
import ntk.android.ticketing.event.toolbar.EVSearchClick;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.baseModel.theme.Toolbar;

public class AdToobar extends RecyclerView.Adapter<AdToobar.ViewHolder> {

    private List<Toolbar> toolbars;
    private Context context;
    private int Click;

    public AdToobar(Context context, List<Toolbar> toolbar) {
        this.toolbars = toolbar;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.toolbar_theme, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageLoader.getInstance().displayImage(toolbars.get(position).HamberMenu.Image, holder.Imgs.get(0));
        ImageLoader.getInstance().displayImage(toolbars.get(position).SearchBox.Image, holder.Imgs.get(1));
        ImageLoader.getInstance().displayImage(toolbars.get(position).Cart.Image, holder.Imgs.get(2));
        holder.Imgs.get(0).setColorFilter(Color.parseColor(toolbars.get(position).HamberMenu.Color), PorterDuff.Mode.SRC_IN);
        holder.Imgs.get(1).setColorFilter(Color.parseColor(toolbars.get(position).SearchBox.Color), PorterDuff.Mode.SRC_IN);
        holder.Imgs.get(2).setColorFilter(Color.parseColor(toolbars.get(position).Cart.Color), PorterDuff.Mode.SRC_IN);
        holder.Container.setBackgroundColor(Color.parseColor(toolbars.get(position).BackgroundColor));
        holder.Line.setBackgroundColor(Color.parseColor(toolbars.get(position).ColorBelowLine));

        holder.Ripples.get(0).setOnClickListener(v -> EventBus.getDefault().post(new EVHamberMenuClick(true)));
        holder.Ripples.get(1).setOnClickListener(v -> EventBus.getDefault().post(new EVSearchClick(true)));

        holder.Container.setOnClickListener(v -> {
            if (Click < 10) {
                Click = Click + 1;
            } else {
                Click = 8;
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                dialog.setContentView(R.layout.dialog_url);
                dialog.show();
                TextView tv = dialog.findViewById(R.id.lblTitleDialogUrl);
                tv.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));

                TextView txtApiBaseUrlUseed = dialog.findViewById(R.id.txtLinkDialogUrl);
                int ApiBaseUrlUseed = EasyPreference.with(context).getInt("ApiBaseUrlUseed", 0);
                txtApiBaseUrlUseed.setText("Use Count : " + ApiBaseUrlUseed + " Of 10");

                EditText txtApiBaseUrl = dialog.findViewById(R.id.txtLinkDialogUrl);
                txtApiBaseUrl.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
                String strUrl = EasyPreference.with(context).getString("ApiBaseUrl", new ConfigStaticValue(context).GetApiBaseUrl());

                txtApiBaseUrl.setText(strUrl);

                EditText txtApiBaseAppId = dialog.findViewById(R.id.txtLinkDialogUrl);
                txtApiBaseAppId.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
                txtApiBaseAppId.setText(EasyPreference.with(context).getString("ApiBaseAppId", new ConfigStaticValue(context).ApiBaseAppId + ""));

                Button btn = dialog.findViewById(R.id.btnSubmitDialogUrl);
                btn.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));

                btn.setOnClickListener(v1 -> {
                    EasyPreference.with(context).addString("ApiBaseUrl", txtApiBaseUrl.getText().toString());
                    EasyPreference.with(context).addString("ApiBaseAppId", txtApiBaseAppId.getText().toString());
                    EasyPreference.with(context).addInt("ApiBaseUrlUseed", 0);

                    Click = 5;
                    dialog.dismiss();
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolbars.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R.id.RippleHamberRecyclerToolbar, R.id.RippleSearchRecyclerToolbar, R.id.RippleShoppingCartRecyclerToolbar})
        List<MaterialRippleLayout> Ripples;

        @BindViews({R.id.ImgHamberRecyclerToolbar, R.id.ImgSearchCartRecyclerToolbar, R.id.ImgShoppingCartRecyclerToolbar})
        List<ImageView> Imgs;

        @BindView(R.id.ContainerRecyclerToolbar)
        RelativeLayout Container;

        @BindView(R.id.LineRecyclerToolbar)
        View Line;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.findViewById(R.id.RippleShoppingCartRecyclerToolbar).setVisibility(View.GONE);
        }
    }
}
