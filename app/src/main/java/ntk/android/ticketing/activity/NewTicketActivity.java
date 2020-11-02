package ntk.android.ticketing.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.ConfigStaticValue;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.Regex;
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdAttach;
import ntk.android.ticketing.adapter.AdSpinner;
import ntk.android.ticketing.event.RemoveAttachEvent;
import ntk.android.base.api.baseModel.FilterModel;
import ntk.android.base.api.file.entity.FileUploadModel;
import ntk.android.base.api.file.interfase.IFile;
import ntk.android.base.api.member.model.MemberUserActAddRequest;
import ntk.android.base.api.ticket.entity.TicketingDepartemen;
import ntk.android.base.api.ticket.entity.TicketingTask;
import ntk.android.base.api.ticket.interfase.ITicket;
import ntk.android.base.api.ticket.model.TicketingDepartemenResponse;
import ntk.android.base.api.ticket.model.TicketingTaskResponse;
import ntk.android.base.config.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class NewTicketActivity extends BaseActivity {

    @BindViews({R.id.SpinnerService,
            R.id.SpinnerState})
    List<Spinner> spinners;

    @BindViews({R.id.lblTitleActSendTicket,
            R.id.lblImportantActSendTicket,
            R.id.lblServiceActSendTicket})
    List<TextView> Lbls;

    @BindViews({R.id.txtSubjectActSendTicket,
            R.id.txtMessageActSendTicket,
            R.id.txtNameFamilyActSendTicket,
            R.id.txtPhoneNumberActSendTicket,
            R.id.txtEmailActSendTicket})
    List<EditText> Txts;

    @BindViews({R.id.inputSubjectActSendTicket,
            R.id.inputMessageActSendTicket,
            R.id.inputNameFamilytActSendTicket,
            R.id.inputPhoneNumberActSendTicket,
            R.id.inputEmailtActSendTicket})
    List<TextInputLayout> Inputs;

    @BindView(R.id.btnSubmitActSendTicket)
    Button Btn;

    @BindView(R.id.RecyclerAttach)
    RecyclerView Rv;

    @BindView(R.id.mainLayoutActSendTicket)
    CoordinatorLayout layout;

    private TicketingTask request = new TicketingTask();
    private MemberUserActAddRequest requestMember = new MemberUserActAddRequest();
    private List<String> attaches = new ArrayList<>();
    private List<String> fileId = new ArrayList<>();
    private AdAttach adapter;

    private static final int READ_REQUEST_CODE = 1520;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_send_ticket);
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

    private void init() {
        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Inputs.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(3).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Inputs.get(4).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Txts.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(2).setText(EasyPreference.with(this).getString("NameFamily", ""));
        Txts.get(3).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(3).setText(EasyPreference.with(this).getString("PhoneNumber", ""));
        Txts.get(4).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Txts.get(4).setText(EasyPreference.with(this).getString("Email", ""));

        Btn.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        adapter = new AdAttach(this, attaches);
        Rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        AdSpinner<String> adapter_state = new AdSpinner<>(this, R.layout.spinner_item, new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.StateTicket))));
        spinners.get(1).setAdapter(adapter_state);
        spinners.get(1).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request.Priority = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (request.Priority == 0) {
            spinners.get(1).setSelection(0);
        }
        FilterModel request = new FilterModel();
        RetrofitManager retro = new RetrofitManager(this);
        ITicket iTicket = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<TicketingDepartemenResponse> Call = iTicket.GetTicketDepartmanActList(headers, request);
        Call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TicketingDepartemenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TicketingDepartemenResponse model) {
                        List<String> list = new ArrayList<>();
                        for (TicketingDepartemen td : model.ListItems) {
                            list.add(td.Title);
                            AdSpinner<String> adapter_dpartman = new AdSpinner<>(NewTicketActivity.this, R.layout.spinner_item, list);
                            spinners.get(0).setAdapter(adapter_dpartman);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //todo show error dialog
                        Toasty.warning(NewTicketActivity.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.btnSubmitActSendTicket)
    public void ClickSubmit() {

        if (Txts.get(0).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(0));
            Toasty.warning(NewTicketActivity.this, "موضوع درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (Txts.get(1).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(1));
            Toasty.warning(NewTicketActivity.this, "متن درخواست خود را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (Txts.get(2).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(2));
            Toasty.warning(NewTicketActivity.this, "نام و نام خانوادگی را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("NameFamily", Txts.get(2).getText().toString());
        if (Txts.get(3).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(3));
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Txts.get(3).getText().toString().startsWith("09")) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(3));
            Toasty.warning(NewTicketActivity.this, "شماره تلفن همراه را به صورت صحیح وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("PhoneNumber", Txts.get(3).getText().toString());
        if (Txts.get(4).getText().toString().isEmpty()) {
            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(4));
            Toasty.warning(NewTicketActivity.this, "پست الکترونیک را وارد کنید", Toasty.LENGTH_LONG, true).show();
            return;
        }
        if (!Regex.ValidateEmail(Txts.get(4).getText().toString())) {
            Toasty.warning(this, "آدرس پست الکترونیکی صحیح نمیباشد", Toasty.LENGTH_LONG, true).show();
            return;
        }
        EasyPreference.with(this).addString("Email", Txts.get(4).getText().toString());
        if (AppUtill.isNetworkAvailable(this)) {
            //show dialog loading
            switcher.showLoadDialog(this, false);
            request.Email = Txts.get(4).getText().toString();
            request.PhoneNo = Txts.get(3).getText().toString();
            request.FullName = Txts.get(2).getText().toString();
            request.HtmlBody = Txts.get(1).getText().toString();
            request.Title = Txts.get(0).getText().toString();

            String ids = "";
            for (int i = 0; i < fileId.size(); i++) {
                if (ids.equals(""))
                    ids = fileId.get(i);
                else
                    ids += "," + fileId.get(i);
            }
            request.LinkFileIds = ids;

            requestMember.FirstName = Txts.get(2).getText().toString();
            requestMember.LastName = Txts.get(2).getText().toString();
            requestMember.PhoneNo = Txts.get(3).getText().toString();
            requestMember.Email = Txts.get(4).getText().toString();


            RetrofitManager retro = new RetrofitManager(this);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);

//                IMember iMember = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IMember.class);
//                Observable<MemberUserResponse> CallMember = iMember.SetUserActAdd(headers, requestMember);
//                CallMember.observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(new Observer<MemberUserResponse>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(MemberUserResponse model) {
//                                //Toasty.success(ActSendTicket.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
//                                //finish();
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                //Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
//                                //    @Override
//                                //    public void onClick(View v) {
//                                //        init();
//                                //    }
//                                ///}).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            findViewById(R.id.btnSubmitActSendTicket).setClickable(false);

            ITicket iTicket = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
            Observable<TicketingTaskResponse> Call = iTicket.SetTicketTaskActSubmit(headers, request);
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<TicketingTaskResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TicketingTaskResponse model) {
                            switcher.hideLoadDialog();
                            Toasty.success(NewTicketActivity.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            switcher.hideLoadDialog();
                            Snackbar.make(layout, "خطای سامانه مجددا تلاش کنید", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    init();
                                }
                            }).show();
                            findViewById(R.id.btnSubmitActSendTicket).setClickable(true);
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


    @OnClick(R.id.imgBackActSendTicket)
    public void Clickback() {
        finish();
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.RippleAttachActSendTicket)
    public void ClickAttach() {
        if (CheckPermission()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(NewTicketActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Btn.setVisibility(View.GONE);
                    attaches.add(getPath(NewTicketActivity.this, uri));
                    adapter.notifyDataSetChanged();
                    UploadFileToServer(getPath(NewTicketActivity.this, uri));
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
                                adapter.notifyDataSetChanged();
                                fileId.add(uploadedString);
                                Btn.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                Toasty.warning(NewTicketActivity.this, "خطای خواندن اطلاعات", Toasty.LENGTH_LONG, true).show();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Btn.setVisibility(View.VISIBLE);
                            attaches.remove(attaches.size() - 1);
                            adapter.notifyDataSetChanged();
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
            Btn.setVisibility(View.VISIBLE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }

    @Subscribe
    public void EventRemove(RemoveAttachEvent event) {
        attaches.remove(event.GetPosition());
        fileId.remove(event.GetPosition());
        adapter.notifyDataSetChanged();
    }
}
