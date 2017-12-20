package com.think.bitmap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by think on 2017/12/16.
 */

public interface NetServerInterface {

    @GET
    Call<ResponseBody> getBitmap(@Url String url);
}
