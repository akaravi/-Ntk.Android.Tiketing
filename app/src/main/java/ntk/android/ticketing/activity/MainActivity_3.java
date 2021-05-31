package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.appclass.AboutUsClass;
import ntk.android.base.dtomodel.theme.DrawerChildThemeDtoModel;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.drawer.DrawerAdapter;
import ntk.android.ticketing.fragment.MainFragment;

public class MainActivity_3 extends AbstractMainActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_3_acitivty);
        List<DrawerChildThemeDtoModel> menus = createDrawerItems();
        RecyclerView drawerRecycler = findViewById(R.id.RecyclerDrawer);
        DrawerAdapter adapter = new DrawerAdapter(this, menus, findViewById(R.id.floaingDrawer));
        drawerRecycler.setAdapter(adapter);
        drawerRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        drawerRecycler.setHasFixedSize(true);
        //set title of app
        AboutUsClass aboutUsClass = Preferences.with(this).appVariableInfo().aboutUs();
        ((TextView) findViewById(R.id.txtToolbarTitle)).setText(aboutUsClass.AboutUsTitle);
        //click on humberger
        ImageView menu = findViewById(R.id.img_drawable_back);
        menu.setImageResource(R.drawable.hamburger);
        menu.setOnClickListener(v -> ((FlowingDrawer) findViewById(R.id.floaingDrawer)).openMenu(false));
        ((KenBurnsView) findViewById(R.id.HeaderImage)).setImageResource(R.drawable.menu_header);
        //create fragment
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, new MainFragment());
        tx.addToBackStack(null);
        tx.commit();
    }

    private List<DrawerChildThemeDtoModel> createDrawerItems() {
        ArrayList<DrawerChildThemeDtoModel> list = new ArrayList<>();
        int i = 0;
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("صندوق پیام").setDrawableIcon(R.drawable.notification2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("اخبار").setDrawableIcon(R.drawable.news2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("پشتیبانی").setDrawableIcon(R.drawable.news2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("نظرسنجی").setDrawableIcon(R.drawable.polling2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("مجلات").setDrawableIcon(R.drawable.article2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("پرسش های متداول").setDrawableIcon(R.drawable.faq2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("بازخورد").setDrawableIcon(R.drawable.feedback2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("دعوت از دوستان").setDrawableIcon(R.drawable.invite2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("درباره ما").setDrawableIcon(R.drawable.about_us2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("راهنما").setDrawableIcon(R.drawable.intro2));
        return list;
    }
}
