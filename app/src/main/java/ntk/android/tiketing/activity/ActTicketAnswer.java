package ntk.android.tiketing.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import com.codekidlabs.storagechooser.StorageChooser;
import com.google.gson.Gson;
import com.tedpark.tedpermission.rx2.TedRx2Permission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
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
import ntk.android.tiketing.R;
import ntk.android.tiketing.adapter.AdAttach;
import ntk.android.tiketing.adapter.AdTicketAnswer;
import ntk.android.tiketing.config.ConfigRestHeader;
import ntk.android.tiketing.config.ConfigStaticValue;
import ntk.android.tiketing.event.EvRemoveAttach;
import ntk.android.tiketing.utill.FontManager;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingAnswer;
import ntk.base.api.ticket.model.TicketingAnswerListRequest;
import ntk.base.api.ticket.model.TicketingAnswerListResponse;
import ntk.base.api.utill.RetrofitManager;


public class ActTicketAnswer extends AppCompatActivity {

    @BindView(R.id.recyclerAnswer)
    RecyclerView Rv;

    @BindView(R.id.lblTitleActTicketAnswer)
    TextView Lbl;

    @BindView(R.id.btnSubmitActTicketAnswer)
    Button Btn;

    private ArrayList<TicketingAnswer> tickets = new ArrayList<>();
    private AdTicketAnswer adapter;
    private List<String> attaches = new ArrayList<>();
    private AdAttach AdAtach;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ticket_answer);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("پاسخ تیکت شماره");
        Rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);

        adapter = new AdTicketAnswer(this, tickets);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        HandelData(1);

        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdAtach = new AdAttach(this, attaches);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void HandelData(int i) {
        RetrofitManager retro = new RetrofitManager(this);
        ITicket iTicket = retro.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

        Observable<TicketingAnswerListResponse> Call = iTicket.GetTicketAnswerList(headers, new Gson().fromJson(getIntent().getExtras().getString("Request"), TicketingAnswerListRequest.class));
        Call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TicketingAnswerListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TicketingAnswerListResponse model) {
                        tickets.addAll(model.ListItems);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(ActTicketAnswer.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.imgBackActTicketAnswer)
    public void ClickBack() {
        finish();
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.RippleAttachActTicketAnswer)
    public void ClickAttach() {
        TedRx2Permission.with(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .request()
                .subscribe(tedPermissionResult -> {
                    if (tedPermissionResult.isGranted()) {
                        StorageChooser.Theme theme = new StorageChooser.Theme(getApplicationContext());
                        theme.setScheme(getResources().getIntArray(R.array.paranoid_theme));
                        StorageChooser chooser = new StorageChooser.Builder()
                                .withActivity(this)
                                .allowCustomPath(true)
                                .setType(StorageChooser.FILE_PICKER)
                                .disableMultiSelect()
                                .setTheme(theme)
                                .withMemoryBar(true)
                                .withFragmentManager(getFragmentManager())
                                .build();
                        chooser.show();
                        chooser.setOnSelectListener(this::UploadFile);
                    } else {
                    }
                }, throwable -> {
                });

    }

    private void UploadFile(String s) {
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        RetrofitManager manager = new RetrofitManager(this);
        Observable<String> observable = manager.FileUpload(null, s, headers);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String url) {
                        String[] strs = s.split("/");
                        String FileName = strs[strs.length - 1];
                        attaches.add(FileName + " - " + url);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void EventRemove(EvRemoveAttach event) {
        attaches.remove(event.GetPosition());
        adapter.notifyDataSetChanged();
    }
}