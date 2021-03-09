package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.activity.common.NotificationsActivity;
import ntk.android.base.activity.poling.PolingDetailActivity;
import ntk.android.base.activity.ticketing.FaqActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.base.activity.ticketing.TicketSearchActivity;
import ntk.android.ticketing.R;
import ntk.android.ticketing.view.gridmenu.GridMenu;
import ntk.android.ticketing.view.gridmenu.GridMenuFragment;

public class MainActivity_2 extends AbstractMainActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_2_activity);
        GridMenuFragment fragment = GridMenuFragment.newInstance(R.drawable.splash);
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu("اخبار", R.drawable.news));
        menus.add(new GridMenu("نظرسنجی", R.drawable.bell));
        menus.add(new GridMenu("دعوت از دوستان", R.drawable.invate));
        menus.add(new GridMenu("بازخورد", R.drawable.feed_back));
        menus.add(new GridMenu("پرسش های متداول", R.drawable.question));
        menus.add(new GridMenu("راهنما", R.drawable.intro));
        menus.add(new GridMenu("مجلات", R.drawable.files));
        menus.add(new GridMenu("درباره ما", R.drawable.about_us));
        menus.add(new GridMenu("پشتیبانی", R.drawable.support));
        fragment.setupMenu(menus);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, fragment);
        tx.addToBackStack(null);
        tx.commit();
        fragment.setOnClickMenuListener((gridMenu, position) -> routeTo(position));
    }

    private void routeTo(int position) {
        if (position == 8) {
            this.startActivity(new Intent(this, TicketListActivity.class));
        } else if (position == 1) {
            this.startActivity(new Intent(this, NotificationsActivity.class));
        } else if (position == 0) {
            this.startActivity(new Intent(this, NewsListActivity.class));
        } else if (position == 3) {
            onFeedbackClick();
        } else if (position == 1) {
            this.startActivity(new Intent(this, PolingDetailActivity.class));
        } else if (position == 2) {
            onInviteMethod();
        } else if (position == 4) {
            this.startActivity(new Intent(this, FaqActivity.class));
        } else if (position == 1) {
            this.startActivity(new Intent(this, ArticleListActivity.class));
        } else if (position == 6) {
            this.startActivity(new Intent(this, AboutUsActivity.class));
        } else if (position == 5) {
            onMainIntro();
        }
    }
}
