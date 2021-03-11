package ntk.android.ticketing.adapter.drawer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.activity.common.IntroActivity;
import ntk.android.base.activity.common.NotificationsActivity;
import ntk.android.base.activity.poling.PolingActivity;
import ntk.android.base.activity.ticketing.FaqActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.base.adapter.BaseRecyclerAdapter;
import ntk.android.base.dtomodel.theme.DrawerChildThemeDtoModel;
import ntk.android.base.room.NotificationStorageService;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.activity.AboutUsActivity;
import ntk.android.ticketing.activity.ArticleListActivity;
import ntk.android.ticketing.activity.NewsListActivity;


public class DrawerAdapter extends BaseRecyclerAdapter<DrawerChildThemeDtoModel, DrawerAdapter.ViewHolder> {


    private Context context;

    private FlowingDrawer Drawer;

    public DrawerAdapter(Context context, List<DrawerChildThemeDtoModel> children, FlowingDrawer drawer) {
        super(children);
        this.context = context;
        this.Drawer = drawer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflate(viewGroup, R.layout.drawer_theme_1_item);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DrawerChildThemeDtoModel item = getItem(position);
        if (item.Icon != null)
            ImageLoader.getInstance().displayImage(item.Icon, holder.Img);
        else if (item.drawableIcon != 0)
            holder.Img.setImageResource(item.drawableIcon);
        if (item.Type == 1) {
            int n = new NotificationStorageService().getAllUnread(context).size();
            if (n != 0) {
                holder.Lbls.get(1).setText(String.valueOf(n));
                holder.Lbls.get(1).setVisibility(View.VISIBLE);
            }
        }
        holder.Lbls.get(0).setText(item.Title);

        holder.Root.setOnClickListener(v -> {
            switch (item.Id) {
                case 0:
                    ClickInbox();
                    break;
                case 1:
                    ClickNews();
                    break;
                case 2:
                    ClickContact();
                    break;
                case 3:
                    ClickPooling();
                    break;
                case 4:
                    ClickArticle();
                    break;
                case 5:
                    ClickQuestion();
                    break;
                case 6:
                    ClickFeedBack();
                    break;
                case 7:
                    ClickShare();
                    break;
                case 8:
                    ClickAbout();
                    break;
                case 9:
                    ClickAbout();
                    break;
                case 10:
                    ClickIntro();
                    break;
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ImgRecyclerDrawerChild)
        ImageView Img;

        @BindViews({R.id.lblRecyclerDrawerChild, R.id.lblBadgeRecyclerDrawerChild})
        List<TextView> Lbls;

        @BindView(R.id.RootContainerRecyclerDrawerChild)
        RelativeLayout Root;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbls.get(0).setTypeface(FontManager.T1_Typeface(context));
            Lbls.get(1).setTypeface(FontManager.T1_Typeface(context));
        }
    }

    private void ClickInbox() {
        context.startActivity(new Intent(context, NotificationsActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickNews() {
        context.startActivity(new Intent(context, NewsListActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickPooling() {
        context.startActivity(new Intent(context, PolingActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickShare() {
        ((AbstractMainActivity) context).onInviteMethod();
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickAbout() {
        context.startActivity(new Intent(context, AboutUsActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickContact() {
        context.startActivity(new Intent(context, TicketListActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickFeedBack() {
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
        ((AbstractMainActivity) context).onFeedbackClick();
    }

    private void ClickQuestion() {
        context.startActivity(new Intent(context, FaqActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }

    private void ClickArticle() {
        context.startActivity(new Intent(context, ArticleListActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    } private void ClickIntro() {
        context.startActivity(new Intent(context, IntroActivity.class));
        if (Drawer != null) {
            Drawer.closeMenu(true);
        }
    }
}
