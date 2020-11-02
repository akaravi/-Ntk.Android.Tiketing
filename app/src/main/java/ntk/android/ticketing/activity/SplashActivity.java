package ntk.android.ticketing.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.api.news.entity.NewsContent;
import ntk.android.base.entityModel.base.ErrorException;
import ntk.android.base.entityModel.base.FilterModel;
import ntk.android.base.services.news.NewsContentService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FilterModel request = new FilterModel();
        request.RowPerPage = 20;
        request.CurrentPageNumber = 1;
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        new NewsContentService(this).getAll(request).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ErrorException<NewsContent>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                String w = "asdwa";
            }

            @Override
            public void onNext(@NonNull ErrorException<NewsContent> newsContentErrorException) {

                String w = "asdwa";
//                newsContentErrorException.Item.Body;
            }

            @Override
            public void onError(@NonNull Throwable e) {
                String w = "asdwa";
            }

            @Override
            public void onComplete() {
                String w = "asdwa";
            }
        });

    }
}
