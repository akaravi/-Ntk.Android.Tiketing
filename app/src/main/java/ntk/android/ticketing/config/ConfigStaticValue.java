package ntk.android.ticketing.config;

import android.content.Context;

import ntk.android.ticketing.utill.EasyPreference;


public class ConfigStaticValue {
    public ConfigStaticValue(Context context) {
        privateContext=context;
        ApiBaseAppId = 0;
        ApiBaseUrl = null;
       //ApiBaseUrl = "https://a7d336e81ee0.ngrok.io";


    }
    private Context privateContext;
    public void UrlPreferenceUseed() {
        if (privateContext != null) {
            int ApiBaseUrlPreferenceUseed = EasyPreference.with(privateContext).getInt("ApiBaseUrlUseed", 0);
            ApiBaseUrlPreferenceUseed++;
            EasyPreference.with(privateContext).addInt("ApiBaseUrlUseed", ApiBaseUrlPreferenceUseed);
        }
    }

    private String ApiBaseUrl;
    public String GetApiBaseUrl()
    {
        if (privateContext != null) {
            String ApiBaseUrlPreference = "";
            int ApiBaseUrlPreferenceUseed = 0;
            ApiBaseUrlPreference = EasyPreference.with(privateContext).getString("ApiBaseUrl", "");
            ApiBaseUrlPreferenceUseed = EasyPreference.with(privateContext).getInt("ApiBaseUrlUseed", 0);
            if (ApiBaseUrlPreference != null && !ApiBaseUrlPreference.isEmpty() && ApiBaseUrlPreferenceUseed < 10) {
                UrlPreferenceUseed();
                return ApiBaseUrlPreference;
            }
        }
        return ApiBaseUrl;
    }
    public int ApiBaseAppId;
}