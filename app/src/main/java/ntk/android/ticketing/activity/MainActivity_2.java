package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.activity.common.NotificationsActivity;
import ntk.android.base.activity.poling.PolingDetailActivity;
import ntk.android.base.activity.ticketing.FaqActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.base.utill.FontManager;
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
        menus.add(new GridMenu(getString(R.string.per_news), R.drawable.news));
        menus.add(new GridMenu(getString(R.string.polling), R.drawable.pooling));
        menus.add(new GridMenu(getString(R.string.invite_friends), R.drawable.invate));
        menus.add(new GridMenu(getString(R.string.feedback), R.drawable.feed_back));
        menus.add(new GridMenu(getString(R.string.faq), R.drawable.question));
        menus.add(new GridMenu(getString(R.string.help), R.drawable.intro));
        menus.add(new GridMenu(getString(R.string.Article), R.drawable.files));
        menus.add(new GridMenu(getString(R.string.notification_inbox), R.drawable.bell));
        menus.add(new GridMenu(getString(R.string.ticketing), R.drawable.support));
        ((TextView) findViewById(R.id.main2Title)).setTypeface(FontManager.T1_Typeface(this));
        fragment.setupMenu(menus);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, fragment);
        tx.addToBackStack(null);
        tx.commit();
        fragment.setOnClickMenuListener((gridMenu, position) -> routeTo(position));
        findViewById(R.id.questionIconBtn).setOnClickListener(view -> {
            routeTo(9);
        });
    }

    private void routeTo(int position) {
        if (position == 0) {
            this.startActivity(new Intent(this, NewsListActivity.class));
        } else if (position == 1) {
            this.startActivity(new Intent(this, PolingDetailActivity.class));
        } else if (position == 2) {
            onInviteMethod();
        } else if (position == 3) {
            onFeedbackClick();
        } else if (position == 4) {
            this.startActivity(new Intent(this, FaqActivity.class));
        } else if (position == 5) {
            onMainIntro();
        } else if (position == 6) {
            this.startActivity(new Intent(this, ArticleListActivity.class));
        } else if (position == 7) {
            this.startActivity(new Intent(this, NotificationsActivity.class));
        } else if (position == 8) {
            this.startActivity(new Intent(this, TicketListActivity.class));
        } else if (position == 9) {
            this.startActivity(new Intent(this, AboutUsActivity.class));
        }
    }
}
