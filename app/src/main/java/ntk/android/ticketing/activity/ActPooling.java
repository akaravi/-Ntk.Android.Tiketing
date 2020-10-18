package ntk.android.ticketing.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
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
import ntk.android.ticketing.adapter.AdPoolCategory;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.FontManager;
import ntk.base.api.pooling.interfase.IPooling;
import ntk.base.api.pooling.model.PoolingCategoryResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActPooling extends BaseActivity {

    @BindView(R.id.lblTitleActPooling)
    TextView LblTitle;

    @BindView(R.id.recyclerPooling)
    RecyclerView Rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pooling);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        LblTitle.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (AppUtill.isNetworkAvailable(this)) {
            // show loading
            switcher.showProgressView();
            RetrofitManager manager = new RetrofitManager(this);
        IPooling iPooling = manager.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IPooling.class);
        Observable<PoolingCategoryResponse> call = iPooling.GetCategoryList(new ConfigRestHeader().GetHeaders(this));
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PoolingCategoryResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PoolingCategoryResponse poolingCategoryResponse) {
                        if (poolingCategoryResponse.IsSuccess) {
                            AdPoolCategory adapter = new AdPoolCategory(ActPooling.this, poolingCategoryResponse.ListItems);
                            Rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 0)
                                switcher.showContentView();
                            else
                                switcher.showEmptyView();

                        }
                    }

                    @Override
                    public void onError(Throwable e){
                        switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());

                    }

                    @Override
                    public void onComplete() {

                    }
                }); } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }

    @OnClick(R.id.imgBackActPooling)
    public void ClickBack() {
        finish();
    }
}
