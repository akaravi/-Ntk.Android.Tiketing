package ntk.android.ticketing.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;

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
import ntk.android.ticketing.adapter.AdDetailPoolCategory;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.pooling.interfase.IPooling;
import ntk.android.base.api.pooling.model.PoolingContentListRequest;
import ntk.android.base.api.pooling.model.PoolingContentListResponse;
import ntk.android.base.config.RetrofitManager;

public class PoolingDetailActivity extends AppCompatActivity {

    @BindView(R.id.lblTitleActDetailPooling)
    TextView LblTitle;

    @BindView(R.id.recyclerDetailPooling)
    RecyclerView Rv;

    private String RequestStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_pooling);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        LblTitle.setText(getIntent().getStringExtra("Title"));
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        RequestStr = getIntent().getExtras().getString("Request");

        HandelData(1, new Gson().fromJson(RequestStr, PoolingContentListRequest.class));
    }

    private void HandelData(int i, PoolingContentListRequest request) {
        RetrofitManager retro = new RetrofitManager(this);
        IPooling iPooling = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IPooling.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);


        Observable<PoolingContentListResponse> call = iPooling.GetContentList(headers, request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PoolingContentListResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PoolingContentListResponse poolingContentListResponse) {
                        if (poolingContentListResponse.IsSuccess) {
                            AdDetailPoolCategory adapter = new AdDetailPoolCategory(PoolingDetailActivity.this, poolingContentListResponse.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(PoolingDetailActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.imgBackActDetailPooling)
    public void ClickBack() {
        finish();
    }
}
