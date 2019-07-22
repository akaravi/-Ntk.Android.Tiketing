package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdFaq;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingFaqListRequest;
import ntk.base.api.ticket.model.TicketingFaqListResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActFaq extends AppCompatActivity {

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

        RetrofitManager retro = new RetrofitManager(this);
        ITicket iTicket = retro.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

        TicketingFaqListRequest request = new TicketingFaqListRequest();
        request.RowPerPage = 100;

        Observable<TicketingFaqListResponse> Call = iTicket.GetTicketFaqList(headers, request);
        Call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TicketingFaqListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TicketingFaqListResponse model) {
                        AdFaq adapter = new AdFaq(ActFaq.this, model.ListItems);
                        Rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(ActFaq.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.imgBackActFaq)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.imgSearchActFaq)
    public void ClickSearch() {
        startActivity(new Intent(this, ActFaqSearch.class));
    }
}
