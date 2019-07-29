package ntk.android.ticketing.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tedpark.tedpermission.rx2.TedRx2Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
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
import ntk.android.ticketing.R;
import ntk.android.ticketing.adapter.AdAttach;
import ntk.android.ticketing.adapter.AdSpinner;
import ntk.android.ticketing.config.ConfigRestHeader;
import ntk.android.ticketing.config.ConfigStaticValue;
import ntk.android.ticketing.event.EvRemoveAttach;
import ntk.android.ticketing.utill.AppUtill;
import ntk.android.ticketing.utill.EasyPreference;
import ntk.android.ticketing.utill.FontManager;
import ntk.android.ticketing.utill.Regex;
import ntk.base.api.file.interfase.IFile;
import ntk.base.api.ticket.interfase.ITicket;
import ntk.base.api.ticket.model.TicketingDepartemen;
import ntk.base.api.ticket.model.TicketingDepartemenList;
import ntk.base.api.ticket.model.TicketingSubmitRequest;
import ntk.base.api.ticket.model.TicketingSubmitResponse;
import ntk.base.api.utill.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ActSendTicket extends AppCompatActivity {

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

    @BindView(R.id.progressAttachActSendTicket)
    ProgressBar progressBar;

    private TicketingSubmitRequest request = new TicketingSubmitRequest();
    private List<String> attaches = new ArrayList<>();
    private AdAttach adapter;
    private String linkFileIds = "";
    private static final int READ_REQUEST_CODE = 42;

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


        RetrofitManager retro = new RetrofitManager(this);
        ITicket iTicket = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<TicketingDepartemenList> Call = iTicket.GetTicketDepartman(headers);
        Call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TicketingDepartemenList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TicketingDepartemenList model) {
                        List<String> list = new ArrayList<>();
                        for (TicketingDepartemen td : model.ListItems) {
                            list.add(td.Title);
                            AdSpinner<String> adapter_dpartman = new AdSpinner<>(ActSendTicket.this, R.layout.spinner_item, list);
                            spinners.get(0).setAdapter(adapter_dpartman);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toasty.warning(ActSendTicket.this, "خطای سامانه", Toasty.LENGTH_LONG, true).show();
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
        } else {
            if (Txts.get(1).getText().toString().isEmpty()) {
                YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(1));
            } else {
                if (Txts.get(2).getText().toString().isEmpty()) {
                    YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(2));
                } else {
                    EasyPreference.with(this).addString("NameFamily", Txts.get(2).getText().toString());
                    if (Txts.get(3).getText().toString().isEmpty()) {
                        YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(3));
                    } else {
                        EasyPreference.with(this).addString("PhoneNumber", Txts.get(3).getText().toString());
                        if (Txts.get(4).getText().toString().isEmpty()) {
                            YoYo.with(Techniques.Tada).duration(700).playOn(Txts.get(4));
                        } else {
                            EasyPreference.with(this).addString("Email", Txts.get(4).getText().toString());
                            if (Regex.ValidateEmail(Txts.get(4).getText().toString())) {
                                if (AppUtill.isNetworkAvailable(this)) {
                                    request.Email = Txts.get(4).getText().toString();
                                    request.PhoneNo = Txts.get(3).getText().toString();
                                    request.Name = Txts.get(2).getText().toString();
                                    request.HtmlBody = Txts.get(1).getText().toString();
                                    request.Title = Txts.get(0).getText().toString();
                                    request.uploadName = attaches;
                                    request.LinkFileIds = linkFileIds;

                                    RetrofitManager retro = new RetrofitManager(this);
                                    Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
                                    ITicket iTicket = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(ITicket.class);
                                    Observable<TicketingSubmitResponse> Call = iTicket.SetTicketSubmit(headers, request);
                                    Call.observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new Observer<TicketingSubmitResponse>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onNext(TicketingSubmitResponse model) {
                                                    Toasty.success(ActSendTicket.this, "با موفقیت ثبت شد", Toasty.LENGTH_LONG, true).show();
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
                                    Snackbar.make(layout, "عدم دسترسی به اینترنت", Snackbar.LENGTH_INDEFINITE).setAction("تلاش مجددا", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            init();
                                        }
                                    }).show();
                                }
                            } else {
                                Toasty.warning(this, "آدرس پست الکترونیکی صحیح نمیباشد", Toasty.LENGTH_LONG, true).show();
                            }
                        }
                    }
                }
            }
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
            ActivityCompat.requestPermissions(ActSendTicket.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    public static String getFilePathFromUri(Context context, Uri _uri) {
        String filePath = "";
        if (_uri != null && "content".equals(_uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null)) {
                if (cursor == null) return _uri.getPath();
                cursor.moveToFirst();
                filePath = cursor.getString(0);
            } catch (SecurityException e) {
                filePath = "/storage/emulated/0" + _uri.getPath().replace("/storage/emulated/0", "");
            }

        } else {
            filePath = _uri.getPath();
        }
        return filePath;
    }

    public static String getFilePathFromUriForServer(Context context, Uri _uri) {
        String filePath = "";
        if (_uri != null && "content".equals(_uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(_uri,
                    new String[]
                            {
                                    MediaStore.Images.ImageColumns.DATA,
                                    MediaStore.Images.Media.DATA,
                                    MediaStore.Images.Media.MIME_TYPE,
                                    MediaStore.Video.VideoColumns.DATA,
                                    MediaStore.Video.Media.DATA,
                                    MediaStore.Video.Media.MIME_TYPE,
                                    MediaStore.Audio.AudioColumns.DATA,
                                    MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.MIME_TYPE,
                            }, null, null, null)) {
                cursor.moveToFirst();
                filePath = cursor.getString(0);
            } catch (SecurityException e) {
                filePath = "/storage/emulated/0" + _uri.getPath().replace("/storage/emulated/0", "");
            }

        } else {
            filePath = _uri.getPath();
        }
        return filePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Btn.setVisibility(View.GONE);
                    attaches.add(getFilePathFromUri(ActSendTicket.this, uri));
                    adapter.notifyDataSetChanged();
                    UploadFileToServer(getFilePathFromUriForServer(ActSendTicket.this, uri));
                }
            }
        }
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
            Observable<String> Call = iFile.uploadFileWithPartMap(headers, new HashMap<>(), MultipartBody.Part.createFormData("File", file.getName(), requestFile));
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(String model) {
                            adapter.notifyDataSetChanged();
                            if (linkFileIds.equals("")) linkFileIds = model;
                            else linkFileIds = linkFileIds + "," + model;
                            Btn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.GONE);
                            Btn.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }

    @Subscribe
    public void EventRemove(EvRemoveAttach event) {
        attaches.remove(event.GetPosition());
        adapter.notifyDataSetChanged();
    }
}
