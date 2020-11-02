package ntk.android.ticketing.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ntk.android.ticketing.adapter.AdPoolCategory;
import ntk.android.base.api.pooling.interfase.IPooling;
import ntk.android.base.api.pooling.model.PoolingCategoryResponse;
import ntk.android.base.config.RetrofitManager;

public class PoolingActivity extends BaseActivity {

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
            IPooling iPooling = new RetrofitManager(this).getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IPooling.class);
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
                                AdPoolCategory adapter = new AdPoolCategory(PoolingActivity.this, poolingCategoryResponse.ListItems);
                                Rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() > 0)
                                    switcher.showContentView();
                                else
                                    switcher.showEmptyView();

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.showErrorView("خطای سامانه مجددا تلاش کنید", () -> init());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            switcher.showErrorView("عدم دسترسی به اینترنت", () -> init());

        }
    }

    @OnClick(R.id.imgBackActPooling)
    public void ClickBack() {
        finish();
    }
}
