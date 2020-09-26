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
import ntk.android.ticketing.adapter.AdTicket;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.baseModel.Filters;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingListRequest;
import ntk.base.api.ticket.model.TicketingListResponse;
import ntk.base.api.ticket.entity.TicketingTask;
import ntk.base.api.utill.NTKUtill;
import ntk.base.api.utill.RetrofitManager;

public class ActSearch extends AppCompatActivity {

    @BindView(R.id.txtSearchActSearch)
    EditText Txt;

    @BindView(R.id.recyclerSearch)
    RecyclerView Rv;

    @BindView(R.id.btnRefreshActSearch)
    Button btnRefresh;

    @BindView(R.id.mainLayoutActSearch)
    CoordinatorLayout layout;

    private ArrayList<TicketingTask> tickets = new ArrayList<>();
    private AdTicket adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
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
        adapter = new AdTicket(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void Search() {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager manager = new RetrofitManager(this);
            ITicket iTicket = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);


            TicketingListRequest request = new TicketingListRequest();
            List<Filters> filters = new ArrayList<>();
            Filters ft = new Filters();
            ft.PropertyName = "Title";
            ft.StringValue = Txt.getText().toString();
            ft.ClauseType = NTKUtill.ClauseType_Or;
            ft.SearchType = NTKUtill.Search_Type_Contains;
            filters.add(ft);

            Filters fd = new Filters();
            fd.PropertyName = "Description";
            fd.StringValue = Txt.getText().toString();
            fd.ClauseType = NTKUtill.ClauseType_Or;
            fd.SearchType = NTKUtill.Search_Type_Contains;
            filters.add(fd);

            Filters fb = new Filters();
            fb.PropertyName = "Body";
            fb.StringValue = Txt.getText().toString();
            fb.ClauseType = NTKUtill.ClauseType_Or;
            fb.SearchType = NTKUtill.Search_Type_Contains;

            filters.add(fb);

            request.filters = filters;

            Observable<TicketingListResponse> Call = iTicket.GetTicketList(new ConfigRestHeader().GetHeaders(this), request);
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<TicketingListResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingListResponse response) {
                            if (response.IsSuccess) {
                                if (response.ListItems.size() != 0) {
                                    tickets.addAll(response.ListItems);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toasty.warning(ActSearch.this, "نتیجه ای یافت نشد", Toasty.LENGTH_LONG, true).show();
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

    @OnClick(R.id.imgBackActSearch)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.btnRefreshActSearch)
    public void ClickRefresh() {
        btnRefresh.setVisibility(View.GONE);
        init();
    }
}
