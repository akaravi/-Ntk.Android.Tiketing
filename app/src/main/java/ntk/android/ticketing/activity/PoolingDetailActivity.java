package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.polling.PollingContentModel;
import ntk.android.base.services.pooling.PollingContentService;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.DetailPoolCategoryAdapter;

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

        HandelData(1, new Gson().fromJson(RequestStr, FilterDataModel.class));
    }

    private void HandelData(int i, FilterDataModel request) {
        new PollingContentService(this).getAll(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new NtkObserver<ErrorException<PollingContentModel>>() {

                    @Override
                    public void onNext(@NonNull ErrorException<PollingContentModel> poolingContentListResponse) {
                        if (poolingContentListResponse.IsSuccess) {
                            DetailPoolCategoryAdapter adapter = new DetailPoolCategoryAdapter(PoolingDetailActivity.this, poolingContentListResponse.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(PoolingDetailActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }

    @OnClick(R.id.imgBackActDetailPooling)
    public void ClickBack() {
        finish();
    }
}
