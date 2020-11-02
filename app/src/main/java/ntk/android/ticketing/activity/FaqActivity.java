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
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.FaqAdapter;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.api.ticket.model.TicketingFaqRequest;
import ntk.android.base.api.ticket.model.TicketingFaqResponse;
import ntk.android.base.config.RetrofitManager;

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

            TicketingFaqRequest request = new TicketingFaqRequest();
            request.RowPerPage = 1000;

            Observable<TicketingFaqResponse> Call = iTicket.GetTicketFaqActList(headers, request);
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TicketingFaqResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingFaqResponse model) {
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

                        @Override
                        public void onComplete() {

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
