package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.FaqAdapter;
import ntk.android.base.api.baseModel.Filters;
import ntk.android.base.api.ticket.entity.TicketingFaq;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.api.ticket.model.TicketingFaqRequest;
import ntk.android.base.api.ticket.model.TicketingFaqResponse;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.RetrofitManager;

public class FaqSearchActivity extends BaseActivity {

    @BindView(R.id.txtSearchActFaqSearch)
    EditText Txt;

    @BindView(R.id.recyclerFaqSearch)
    RecyclerView Rv;

    @BindView(R.id.btnRefreshActFaqSearch)
    Button btnRefresh;

    @BindView(R.id.mainLayoutActFaqSearch)
    CoordinatorLayout layout;

    private ArrayList<TicketingFaq> faqs = new ArrayList<>();
    private FaqAdapter adapter;
    boolean searchLock;

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
        adapter = new FaqAdapter(this, faqs);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {

                ITicket iTicket = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);


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
                switcher.showProgressView();
                Observable<TicketingFaqResponse> Call = iTicket.GetTicketFaqActList(new ConfigRestHeader().GetHeaders(this), request);
                Call.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<TicketingFaqResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(TicketingFaqResponse response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        faqs.addAll(response.ListItems);
                                        adapter.notifyDataSetChanged();
                                        switcher.showContentView();
                                    } else {
                                        switcher.showEmptyView();
                                    }
                                } else {
                                    switcher.showErrorView(response.ErrorMessage, () -> init());
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                searchLock = false;
                                btnRefresh.setVisibility(View.VISIBLE);
                                switcher.showErrorView("خطا در دسترسی به سامانه", () -> init());

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                btnRefresh.setVisibility(View.VISIBLE);
                searchLock = false;
                switcher.showErrorView("عدم دسترسی به اینترنت", () -> Search());
            }
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
