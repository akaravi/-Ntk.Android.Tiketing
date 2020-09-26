package ntk.android.ticketing.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import ntk.android.ticketing.utill.EndlessRecyclerViewScrollListener;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingListRequest;
import ntk.base.api.ticket.model.TicketingListResponse;
import ntk.base.api.ticket.entity.TicketingTask;
import ntk.base.api.utill.NTKUtill;
import ntk.base.api.utill.RetrofitManager;


public class ActSupport extends AppCompatActivity {

    @BindView(R.id.recyclerFrSupport)
    RecyclerView Rv;

    @BindView(R.id.FabFrSupport)
    FloatingActionButton Fab;

    @BindView(R.id.RefreshTicket)
    SwipeRefreshLayout Refresh;

    @BindView(R.id.mainLayoutActSupport)
    CoordinatorLayout layout;

    private ArrayList<TicketingTask> tickets = new ArrayList<>();
    private AdTicket adapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private int TotalTag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_support);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);

        adapter = new AdTicket(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount <= TotalTag) {
                    HandelData((page + 1));
                }
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if (dy > 0 || dy < 0 && Fab.isShown())
                    Fab.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

        Rv.addOnScrollListener(scrollListener);

        Refresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorAccent,
                R.color.colorAccent);

        Refresh.setOnRefreshListener(() -> {
            tickets.clear();
            HandelData(1);
            Refresh.setRefreshing(false);
        });

        HandelData(1);
    }


    private void HandelData(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            ITicket iTicket = retro.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

            TicketingListRequest request = new TicketingListRequest();
            request.RowPerPage = 10;
            request.CurrentPageNumber = i;
            request.SortType = NTKUtill.Descnding_Sort;
            request.SortColumn = "Id";

            Observable<TicketingListResponse> Call = iTicket.GetTicketList(headers, request);
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TicketingListResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingListResponse model) {
                            tickets.addAll(model.ListItems);
                            adapter.notifyDataSetChanged();
                            TotalTag = model.TotalRowCount;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
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
            Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    init();
                }
            }).show();
        }
    }

    @OnClick(R.id.FabFrSupport)
    public void ClickSendTicket() {
        startActivity(new Intent(this, ActSendTicket.class));
    }
}