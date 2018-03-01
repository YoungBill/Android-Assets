package com.android.assets;

import android.content.Context;

import java.io.IOException;

/**
 * Created by chentao on 2018/2/28.
 * APK资源控制器
 */

public class ApkAssetsController {

    public static String[] list(Context context, String path) throws IOException {
        return context.getAssets().list(path);
    }
}
