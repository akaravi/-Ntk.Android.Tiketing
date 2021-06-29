package ntk.android.ticketing;

import ntk.android.base.ApplicationStyle;
import ntk.android.base.view.ViewController;
import ntk.android.ticketing.activity.MainActivity_1;
import ntk.android.ticketing.activity.MainActivity_2;
import ntk.android.ticketing.activity.MainActivity_3;

public class MyAppStyle extends ApplicationStyle {

    @Override
    public ViewController getViewController() {
        ViewController vc = new ViewController() {
        };
        vc.setLoading_view(R.layout.app_base_loading)
                .setEmpty_view(R.layout.app_base_empty)
                .setError_view(R.layout.app_base_error)
                .setError_button(R.id.btn_error_tryAgain)
                .setError_label(R.id.tvError);
        return vc;
    }

    @Override
    public Class<?> getMainActivity() {
        if (theme.equals("theme3"))
            return MainActivity_3.class;
        else if (theme.equals("theme2"))
            return MainActivity_2.class;
        else
            return MainActivity_1.class;
    }

}