package ntk.android.ticketing.activity;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

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
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.baseModel.Filters;
import ntk.base.api.ticket.entity.TicketingFaq;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingFaqRequest;
import ntk.base.api.ticket.model.TicketingFaqResponse;
import ntk.base.api.ticket.entity.TicketingTask;
import ntk.base.api.utill.NTKUtill;
import ntk.base.api.utill.RetrofitManager;

public class ActFaqSearch extends AppCompatActivity {

    @BindView(R.id.txtSearchActFaqSearch)
    EditText Txt;

    @BindView(R.id.recyclerFaqSearch)
    RecyclerView Rv;

    @BindView(R.id.btnRefreshActFaqSearch)
    Button btnRefresh;

    @BindView(R.id.mainLayoutActFaqSearch)
    CoordinatorLayout layout;

    private ArrayList<TicketingFaq> faqs = new ArrayList<>();
    private AdFaq adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_faq_search);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new GridLayoutManager(this, 2));

        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });
        adapter = new AdFaq(this, faqs);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager manager = new RetrofitManager(this);
            ITicket iTicket = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);


            TicketingFaqRequest request = new TicketingFaqRequest();
            List<Filters> filters = new ArrayList<>();
            Filters fa = new Filters();
            fa.PropertyName = "Answer";
            fa.StringValue = Txt.getText().toString();
            fa.ClauseType = NTKUtill.ClauseType_Or;
            fa.SearchType = NTKUtill.Search_Type_Contains;
            filters.add(fa);

            Filters fq = new Filters();
            fq.PropertyName = "Question";
            fq.StringValue = Txt.getText().toString();
            fq.ClauseType = NTKUtill.ClauseType_Or;
            fq.SearchType = NTKUtill.Search_Type_Contains;
            filters.add(fq);

            request.filters = filters;

            Observable<TicketingFaqResponse> Call = iTicket.GetTicketFaqActList(new ConfigRestHeader().GetHeaders(this), request);
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<TicketingFaqResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingFaqResponse response) {
                            if (response.IsSuccess) {
                                if (response.ListItems.size() != 0) {
                                    faqs.addAll(response.ListItems);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toasty.warning(ActFaqSearch.this, "نتیجه ای یافت نشد", Toasty.LENGTH_LONG, true).show();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            btnRefresh.setVisibility(View.VISIBLE);
                            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            btnRefresh.setVisibility(View.VISIBLE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }

    @OnClick(R.id.imgBackActFaqSearch)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.btnRefreshActFaqSearch)
    public void ClickRefresh() {
        btnRefresh.setVisibility(View.GONE);
        init();
    }
}
