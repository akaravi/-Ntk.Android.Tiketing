package ntk.android.ticketing.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdAttach;
import ntk.android.ticketing.adapter.AdTicketAnswer;
import ntk.android.ticketing.event.RemoveAttachEvent;
import ntk.android.base.api.file.entity.FileUploadModel;
import ntk.android.base.api.file.interfase.IFile;
import ntk.android.base.api.ticket.entity.TicketingAnswer;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.api.ticket.model.TicketingAnswerListRequest;
import ntk.android.base.api.ticket.model.TicketingAnswerResponse;
import ntk.android.base.api.ticket.model.TicketingAnswerSubmitRequest;
import ntk.android.base.config.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class TicketAnswerActivity extends AppCompatActivity {

    @BindViews({R.id.recyclerAnswer,
            R.id.RecyclerAttachTicketAnswer})
    List<RecyclerView> Rvs;

    @BindView(R.id.lblTitleActTicketAnswer)
    TextView Lbl;

    @BindView(R.id.mainLayoutActTicketAnswer)
    CoordinatorLayout layout;

    @BindView(R.id.txtMessageActTicketAnswer)
    EditText txt;

    @BindView(R.id.btnSubmitActTicketAnswer)
    Button btn;

    private ArrayList<TicketingAnswer> tickets = new ArrayList<>();
    private AdTicketAnswer adapter;
    private List<String> attaches = new ArrayList<>();
    private AdAttach AdAtach;
    private String linkFileIds = "";
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ticket_answer);
        ButterKnife.bind(this);
        init();
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
    public void EventBus(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        AdAtach.notifyDataSetChanged();
    }

    private void init() {
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("پاسخ تیکت  " + getIntent().getLongExtra("TicketId", 0));
        Rvs.get(0).setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        Rvs.get(0).setLayoutManager(manager);

        adapter = new AdTicketAnswer(this, tickets);
        Rvs.get(0).setAdapter(adapter);
        adapter.notifyDataSetChanged();

        HandelData(1);

        Rvs.get(1).setHasFixedSize(true);
        Rvs.get(1).setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        AdAtach = new AdAttach(this, attaches);
        Rvs.get(1).setAdapter(AdAtach);
        AdAtach.notifyDataSetChanged();
    }

    private void HandelData(int i) {
        if (AppUtill.isNetworkAvailable(this)) {
            RetrofitManager retro = new RetrofitManager(this);
            ITicket iTicket = retro.getCachedRetrofit().create(ITicket.class);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
            Observable<TicketingAnswerResponse> Call = iTicket.GetTicketAnswerActList(headers, new Gson().fromJson(getIntent().getExtras().getString("Request"), TicketingAnswerListRequest.class));
            Call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TicketingAnswerResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingAnswerResponse model) {
                            tickets.addAll(model.ListItems);
                            adapter.notifyDataSetChanged();
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

    @OnClick(R.id.imgBackActTicketAnswer)
    public void ClickBack() {
        finish();
    }

    @OnClick(R.id.btnSubmitActTicketAnswer)
    public void ClickSubmit() {
        if (txt.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(txt);
        } else {
            if (AppUtill.isNetworkAvailable(this)) {
                TicketingAnswerSubmitRequest request = new TicketingAnswerSubmitRequest();
                request.HtmlBody = txt.getText().toString();
                request.LinkTaskId = getIntent().getLongExtra("TicketId", 0);
                request.LinkFileIds = linkFileIds;
                RetrofitManager retro = new RetrofitManager(this);
                Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
                ITicket iTicket = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
                Observable<TicketingAnswerResponse> Call = iTicket.GetTicketAnswerActSubmit(headers, request);
                Call.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<TicketingAnswerResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(TicketingAnswerResponse model) {
                                Toasty.success(TicketAnswerActivity.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                                finish();
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
                Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
            }
        }
    }

    @OnClick(R.id.RippleAttachActTicketAnswer)
    public void ClickAttach() {
        if (CheckPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(TicketAnswerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    btn.setVisibility(View.GONE);
                    attaches.add(getPath(TicketAnswerActivity.this, uri));
                    AdAtach.notifyDataSetChanged();
                    UploadFileToServer(getPath(TicketAnswerActivity.this, uri));
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void UploadFileToServer(String url) {
        if (AppUtill.isNetworkAvailable(this)) {
            File file = new File(String.valueOf(Uri.parse(url)));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RetrofitManager retro = new RetrofitManager(this);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
            IFile iFile = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IFile.class);
            Observable<ResponseBody> Call = iFile.uploadFileWithPartMap(headers, new HashMap<>(), MultipartBody.Part.createFormData("File", file.getName(), requestFile));
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody model) {
                            try {

                                String uploadedString = new Gson().fromJson(model.string(), FileUploadModel.class).FileKey;
                                if (linkFileIds.equals("")) linkFileIds = uploadedString;
                                else linkFileIds = linkFileIds + "," + uploadedString;
                                adapter.notifyDataSetChanged();
                                btn.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                Toasty.warning(TicketAnswerActivity.this, "خطای خواندن اطلاعات", Toasty.LENGTH_LONG, true).show();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
                            btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }
}