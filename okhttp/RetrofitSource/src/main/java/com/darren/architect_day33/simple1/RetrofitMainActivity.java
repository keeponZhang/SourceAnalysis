package com.darren.architect_day33.simple1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.darren.architect_day33.R;
import com.darren.architect_day33.simple1.api.GitHubService;
import com.darren.architect_day33.simple1.bean.Repo;
import com.darren.architect_day33.simple1.bean.UserInfo;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void simpleResponseBody(View view) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://www.baidu.com/")
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        service.listReposResponseBody("keeponzhang");
        Call<ResponseBody> call = service.listReposResponseBody("keeponzhang");
        try {
            call.enqueue(new Callback<ResponseBody>() {
                //BuiltInConverters
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.e(TAG, "onResponse: "+response.body().string() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void simpleCall(View view) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://api.github.com/")
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<Repo>> octocat = service.listRepos("octocat");
        octocat.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                Log.e(TAG, "onResponse List<Repo> size : "+response.body().size() );
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {

            }
        });
    }
    public void simpleObservable(View view) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://api.github.com/")
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        Observable<List<Repo>> octocat = service.listReposObservable("octocat");

        octocat.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //Observable<List<Repo>>??????????????????????????????Observer??????????????????
                .subscribe(new Observer<List<Repo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                    }

                    @Override
                    public void onNext(List<Repo> value) {
                        Log.e("TAG", "MainActivity onNext:" + value.size());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
    public void getData(View view) {
        RetrofitClient.getServiceApi().userLogin("Darren","940223")
                //??????????????????OkHttpCall???OkHttpCall??????????????????Callback????????????????????????Result<UserInfo>?????????Callback<Result<UserInfo>>???Result?????????????????????????????????????????????????????????????????????success?????????UserInfo???????????????????????????????????????error
                //?????????????????????????????????
                .enqueue(new HttpCallback<UserInfo>(){
                    @Override
                    public void onSucceed(UserInfo result) {
                        Log.e(TAG, "onSucceed: "+result );
                        // ??????
//                        Toast.makeText(MainActivity.this,"??????"+result.toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String code, String msg) {
                        // ??????
                        Log.e(TAG, "onError: "+msg );
//                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                });
    }


}
