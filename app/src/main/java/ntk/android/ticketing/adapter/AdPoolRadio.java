package ntk.android.ticketing.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.ticketing.R;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.pooling.interfase.IPooling;
import ntk.android.base.api.pooling.entity.PoolingContent;
import ntk.android.base.api.pooling.entity.PoolingOption;
import ntk.android.base.api.pooling.model.PoolingSubmitRequest;
import ntk.android.base.api.pooling.model.PoolingSubmitResponse;
import ntk.android.base.api.pooling.entity.PoolingVote;
import ntk.android.base.config.RetrofitManager;

public class AdPoolRadio extends RecyclerView.Adapter<AdPoolRadio.ViewHolder> {

    private List<PoolingOption> arrayList;
    private Context context;
    private int lastSelectedPosition = -1;
    private PoolingContent PC;
    private Button BtnChart;

    public AdPoolRadio(Context context, List<PoolingOption> arrayList, PoolingContent pc, Button chart) {
        this.arrayList = arrayList;
        this.context = context;
        this.BtnChart = chart;
        this.PC = pc;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pool_radio, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.LblTitle.setText(arrayList.get(position).Option);
        holder.Radio.setChecked(lastSelectedPosition == position);
        holder.Radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PoolingSubmitRequest request = new PoolingSubmitRequest();
                request.ContentId = arrayList.get(position).LinkPollingContentId;
                PoolingVote vote = new PoolingVote();
                vote.OptionId = Long.parseLong(String.valueOf(arrayList.get(position).Id));
                vote.OptionScore = 1;
                List<PoolingVote> votes = new ArrayList<>();
                votes.add(vote);
                request.votes = votes;


                RetrofitManager retro = new RetrofitManager(context);
                IPooling iPooling = retro.getRetrofitUnCached().create(IPooling.class);
                Map<String, String> headers = new ConfigRestHeader().GetHeaders(context);

                Observable<PoolingSubmitResponse> observable = iPooling.SetSubmitPooling(headers, request);
                observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<PoolingSubmitResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(PoolingSubmitResponse poolingSubmitResponse) {
                                if (poolingSubmitResponse.IsSuccess) {
                                    Toasty.info(context, "نظر شما با موققثیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                    if (PC.ViewStatisticsAfterVote) {
                                        BtnChart.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Toasty.warning(context, poolingSubmitResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toasty.warning(context, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblRecyclerPoolRadio)
        TextView LblTitle;

        @BindView(R.id.RadioRecyclerPoolRadio)
        RadioButton Radio;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LblTitle.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Radio.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }
}