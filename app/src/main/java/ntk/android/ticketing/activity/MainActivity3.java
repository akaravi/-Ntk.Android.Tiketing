package ntk.android.ticketing.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ntk.android.base.activity.abstraction.AbstractMainActivity;
import ntk.android.base.dtomodel.theme.DrawerChildThemeDtoModel;
import ntk.android.ticketing.R;

public class MainActivity3 extends AbstractMainActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_3_acitivty);
        List<DrawerChildThemeDtoModel> menus = createDrawerItems();
    }

    private List<DrawerChildThemeDtoModel> createDrawerItems() {
        ArrayList<DrawerChildThemeDtoModel> list = new ArrayList<>();
        int i = 0;
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("صندوق پیام").setIcon(R.drawable.notification2));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("اخبار"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("نظرسنجی"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("دعوت از دوستان"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("درباره ما"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("تماس با ما"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("بازخورد"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("پرسش های متداول"));
        list.add(new DrawerChildThemeDtoModel().setId(i++).setTitle("وبلاگ"));

    }
}
