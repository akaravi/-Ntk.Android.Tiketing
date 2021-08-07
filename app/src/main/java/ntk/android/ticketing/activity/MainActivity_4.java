package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.activity.common.NotificationsActivity;
import ntk.android.base.activity.poling.PolingDetailActivity;
import ntk.android.base.activity.ticketing.FaqActivity;
import ntk.android.base.activity.ticketing.TicketListActivity;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.MainViewPager2;
import ntk.android.ticketing.adapter.PanelInterface;
import ntk.android.ticketing.model.PanelViewModel;

public class MainActivity_4 extends AbstractMainActivity implements PanelInterface {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDirectContentView(R.layout.main_activity_4);
        ViewPager mPager = findViewById(R.id.slpager);

        try {
            mPager.setAdapter(new MainViewPager2(this, createTab1() ));
            mPager.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mPager.setCurrentItem(0, true);
    }

    private List<PanelViewModel> createTab1() {
        List<PanelViewModel> m = new ArrayList<>();
        m.add(new PanelViewModel().setTag(9).setTitle(getString(R.string.per_news)).setImageId(R.drawable.news));
        m.add(new PanelViewModel().setTag(10).setTitle(getString(R.string.polling)).setImageId(R.drawable.pooling));
        m.add(new PanelViewModel().setTag(11).setTitle(getString(R.string.invite_friends)).setImageId(R.drawable.invate));

        m.add(new PanelViewModel().setTag(12).setTitle(getString(R.string.feedback)).setImageId(R.drawable.feed_back));
        m.add(new PanelViewModel().setTag(13).setTitle(getString(R.string.faq)).setImageId(R.drawable.question));
        m.add(new PanelViewModel().setTag(14).setTitle(getString(R.string.help)).setImageId(R.drawable.intro));

        m.add(new PanelViewModel().setTag(15).setTitle((getString(R.string.Article))).setImageId(R.drawable.files));
        m.add(new PanelViewModel().setTag(16).setTitle((getString(R.string.notification_inbox))).setImageId(R.drawable.bell));
        m.add(new PanelViewModel().setTag(16).setTitle((getString(R.string.ticketing))).setImageId(R.drawable.support));

        return m;
    }

    @Override
    public void OnCardClick(PanelViewModel panel) {
        switch (panel.tag) {
            case 0:
                this.startActivity(new Intent(this, NewsListActivity.class));
                break;

            case 1:
                this.startActivity(new Intent(this, PolingDetailActivity.class));
                break;

            case 2:
                onInviteMethod();
                break;

            case 3:
                onFeedbackClick();
                break;

            case 4:
                this.startActivity(new Intent(this, FaqActivity.class));
                break;

            case 5:
                onMainIntro();
                break;

            case 6:
                this.startActivity(new Intent(this, ArticleListActivity.class));
                break;

            case 7:
                this.startActivity(new Intent(this, NotificationsActivity.class));
                break;

            case 8:
                this.startActivity(new Intent(this, TicketListActivity.class));
                break;
        }
    }
}
