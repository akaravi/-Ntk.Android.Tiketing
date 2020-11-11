package ntk.android.ticketing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.api.blog.entity.BlogComment;
import ntk.android.base.api.blog.interfase.IBlog;
import ntk.android.base.api.blog.model.BlogCommentResponse;
import ntk.android.base.api.blog.model.BlogCommentViewRequest;
import ntk.android.base.api.utill.NTKClientAction;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.base.entitymodel.base.Filters;
import ntk.android.base.entitymodel.blog.BlogCommentModel;
import ntk.android.base.services.blog.BlogCommentService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;

public class CommentBlogAdapter extends RecyclerView.Adapter<CommentBlogAdapter.ViewHolder> {

    private List<BlogCommentModel> arrayList;
    private Context context;

    public CommentBlogAdapter(Context context, List<BlogCommentModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_comment, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Lbls.get(0).setText(arrayList.get(position).writer);
        if (arrayList.get(position).CreatedDate != null) {
            holder.Lbls.get(1).setText(AppUtill.GregorianToPersian(arrayList.get(position).CreatedDate.toString()));//todo convert
        } else {
            holder.Lbls.get(1).setText("");
        }
        holder.Lbls.get(2).setText(String.valueOf(arrayList.get(position).sumDisLikeClick));
        holder.Lbls.get(3).setText(String.valueOf(arrayList.get(position).sumLikeClick));
        holder.Lbls.get(4).setText(String.valueOf(arrayList.get(position).comment));

        holder.ImgLike.setOnClickListener(v -> {
            FilterDataModel request = new FilterDataModel();
            request.id = arrayList.get(position).Id;
            request.ActionClientOrder = NTKClientAction.LikeClientAction;
            RetrofitManager retro = new RetrofitManager(context);
            IBlog iBlog = retro.getRetrofitUnCached().create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(context);
            Observable<BlogCommentResponse> call = iBlog.GetCommentView(headers, request);
            new BlogCommentService(context).getAll(request).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BlogCommentResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogCommentResponse model) {
                            if (model.IsSuccess) {
                                arrayList.get(position).SumLikeClick = arrayList.get(position).SumLikeClick + 1;
                                notifyDataSetChanged();
                            } else {
                                Toasty.warning(context, model.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.warning(context, "قبلا در این محتوا ثبت نطر ئاشته اید", Toasty.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        });

        holder.ImgDisLike.setOnClickListener(v -> {
            FilterDataModel request = new FilterDataModel();
            request.filters = new ArrayList<>();
            {
                Filters f = new Filters();
                f.PropertyName = ("Id");
                f.IntValue2 = f.IntValue1 = arrayList.get(position).Id;
                request.filters.add(f);
            }
            {
                Filters f = new Filters();
                f.PropertyName = ("ActionClientOrder");
                f.IntValue2 = f.IntValue1 = (long) NTKClientAction.DisLikeClientAction;
                request.filters.add(f);
            }
            RetrofitManager retro = new RetrofitManager(context);
            IBlog iBlog = retro.getRetrofitUnCached().create(IBlog.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(context);
            Observable<BlogCommentResponse> call = iBlog.GetCommentView(headers, request);
            call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BlogCommentResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BlogCommentResponse model) {
                            if (model.IsSuccess) {
                                arrayList.get(position).SumDisLikeClick = arrayList.get(position).SumDisLikeClick - 1;
                                notifyDataSetChanged();
                            } else {
                                Toasty.warning(context, model.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.warning(context, "قبلا در این محتوا ثبت نطر ئاشته اید", Toasty.LENGTH_LONG, true).show();
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

        @BindViews({R.id.lblUserNameRecyclerComment,
                R.id.lblDateRecyclerComment,
                R.id.lblDesLikeRecyclerComment,
                R.id.lblLikeRecyclerComment,
                R.id.lblContentRecyclerComment
        })
        List<TextView> Lbls;

        @BindView(R.id.imgDisLikeRecyclerComment)
        ImageView ImgDisLike;

        @BindView(R.id.imgLikeRecyclerComment)
        ImageView ImgLike;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Lbls.get(0).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(1).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(2).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(3).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            Lbls.get(4).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}
