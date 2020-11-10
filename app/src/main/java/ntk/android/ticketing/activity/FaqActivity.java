package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.ticketing.TicketingFaqModel;
import ntk.android.base.services.ticketing.TicketingFaqService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.FaqAdapter;

public class FaqActivity extends BaseActivity {

    @BindView(R.id.lblTitleActFaq)
    TextView Lbl;

    @BindView(R.id.recyclerFaq)
    RecyclerView Rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_faq);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("پرسش های متداول");
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (AppUtill.isNetworkAvailable(this)) {
            // show loading
            switcher.showProgressView();
            RetrofitManager retro = new RetrofitManager(this);
            ITicket iTicket = retro.getCachedRetrofit().create(ITicket.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            FilterDataModel request = new FilterDataModel();
            request.RowPerPage = 1000;


            new TicketingFaqService(this).getAll(request).
                    subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NtkObserver<ErrorException<TicketingFaqModel>>() {
                        @Override
                        public void onNext(ErrorException<TicketingFaqModel> model) {
                            FaqAdapter adapter = new FaqAdapter(FaqActivity.this, model.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 0)
                                switcher.showContentView();
                            else
                                switcher.showEmptyView();

                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());

                        }
                    });
        } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }

    @OnClick(R.id.imgBackActFaq)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.imgSearchActFaq)
    public void ClickSearch() {
        startActivity(new Intent(this, FaqSearchActivity.class));
    }
}
