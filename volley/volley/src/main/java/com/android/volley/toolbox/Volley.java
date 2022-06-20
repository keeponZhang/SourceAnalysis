/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import java.io.File;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack A {@link BaseHttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, BaseHttpStack stack) {
        BasicNetwork network;
//        判断如果stack是等于null的，则去创建一个HttpStack对象
        if (stack == null) {
            //在Android 2.2版本之前，HttpURLConnection一直存在着一些令人厌烦的bug
            if (Build.VERSION.SDK_INT >= 9) {
//                如果手机系统版本号是大于9的，则创建一个HurlStack的实例
                network = new BasicNetwork(new HurlStack());
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                // At some point in the future we'll move our minSdkVersion past Froyo and can
                // delete this fallback (along with all Apache HTTP code).
                String userAgent = "volley/0";
                try {
                    String packageName = context.getPackageName();
                    PackageInfo info =
                            context.getPackageManager().getPackageInfo(packageName, /* flags= */ 0);
                    userAgent = packageName + "/" + info.versionCode;
                } catch (NameNotFoundException e) {
                }
            //  否则就创建一个HttpClientStack的实例
                network =
                        new BasicNetwork(
                                new HttpClientStack(AndroidHttpClient.newInstance(userAgent)));
            }
        } else {
            //创建好了HttpStack之后，接下来又创建了一个Network对象，它是用于根据传入的HttpStack对象来处理网络请求的
            network = new BasicNetwork(stack);
        }

        return newRequestQueue(context, network);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     * @deprecated Use {@link #newRequestQueue(Context, BaseHttpStack)} instead to avoid depending
     *     on Apache HTTP. This method may be removed in a future release of Volley.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        if (stack == null) {
            return newRequestQueue(context, (BaseHttpStack) null);
        }
        return newRequestQueue(context, new BasicNetwork(stack));
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        //DiskBasedCache是缓存用的
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (BaseHttpStack) null);
    }
}
