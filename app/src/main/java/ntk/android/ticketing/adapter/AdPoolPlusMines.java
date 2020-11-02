package ntk.android.ticketing.adapter;

import android.content.Context;
import android.os.Vibrator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AdPoolPlusMines extends RecyclerView.Adapter<AdPoolPlusMines.ViewHolder> {

    private List<PoolingOption> arrayList;
    private Context context;
    private PoolingContent PC;
    private Button BtnSend;
    private Button BtnChart;
    private int Score = 0;
    private Map<Long, Integer> MapVote;

    public AdPoolPlusMines(Context context, List<PoolingOption> arrayList, PoolingContent pc, Button send, Button chart) {
        this.arrayList = arrayList;
        this.context = context;
        this.PC = pc;
        this.BtnSend = send;
        this.BtnChart = chart;
        MapVote = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_pool_plus_minse, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Title.setText(arrayList.get(position).Option);
        holder.Plus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            Score = 0;
            int val = Integer.parseInt(holder.Number.getText().toString());
            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                Score = Score + map.getValue();
            }
            if (Score < PC.MaxVoteForThisContent) {
                if (val < PC.MaxVoteForEachOption) {
                    val = val + 1;
                    holder.Number.setText(String.valueOf(val));
                    MapVote.put(Long.parseLong(String.valueOf(arrayList.get(position).Id)), val);
                } else {
                    Toasty.warning(context, "تعداد پاسخ مجاز برای این گزینه " + PC.MaxVoteForEachOption, Toasty.LENGTH_LONG, true).show();
                }
            } else {
                Toasty.warning(context, "تعداد پاسخ مجاز برای این نظر سنجی " + PC.MaxVoteForThisContent, Toasty.LENGTH_LONG, true).show();
            }
        });
        holder.Minus.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            int val = Integer.parseInt(holder.Number.getText().toString());
            if (val == 0) {
                Toasty.warning(context, "امکان دادن امتیاز منفی وجود ندارد", Toasty.LENGTH_LONG, true).show();
            } else {
                val = val - 1;
                holder.Number.setText(String.valueOf(val));
                MapVote.put(Long.parseLong(String.valueOf(arrayList.get(position).Id)), (val));
            }
        });
        BtnSend.setOnClickListener(v -> {
            PoolingSubmitRequest request = new PoolingSubmitRequest();
            request.ContentId = arrayList.get(position).LinkPollingContentId;
            request.votes = new ArrayList<>();
            for (Map.Entry<Long, Integer> map : MapVote.entrySet()) {
                PoolingVote vote = new PoolingVote();
                vote.OptionId = map.getKey();
                vote.OptionScore = map.getValue();
                request.votes.add(vote);
            }

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
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblRecyclerPoolPlus)
        TextView Number;

        @BindView(R.id.lblTitleRecyclerPoolPlus)
        TextView Title;

        @BindView(R.id.imgPlusRecyclerPoolPlus)
        ImageView Plus;

        @BindView(R.id.imgMinusRecyclerPoolPlus)
        ImageView Minus;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Number.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Title.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}