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
import ntk.android.ticketing.adapter.AdTicket;
import ntk.android.base.api.baseModel.Filters;
import ntk.android.base.api.ticket.entity.TicketingTask;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.api.ticket.model.TicketingListRequest;
import ntk.android.base.api.ticket.model.TicketingTaskResponse;
import ntk.android.base.api.utill.NTKUtill;
import ntk.android.base.config.RetrofitManager;

public class TicketSearchActivity extends BaseActivity {

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
    boolean searchLock;

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
        if (!searchLock) {
            searchLock = true;
            if (AppUtill.isNetworkAvailable(this)) {
                ITicket iTicket = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);


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
                switcher.showProgressView();
                Observable<TicketingTaskResponse> Call = iTicket.GetTicketTaskActList(new ConfigRestHeader().GetHeaders(this), request);
                Call.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<TicketingTaskResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(TicketingTaskResponse response) {
                                searchLock = false;
                                if (response.IsSuccess) {
                                    if (response.ListItems.size() != 0) {
                                        tickets.addAll(response.ListItems);
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
