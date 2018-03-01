package com.android.assets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ASSETS = "assets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        readFile("customize_theme.properties");

        readFile("preview");

        unZipFile("320.amr");
    }

    /**
     * 直接读取文件/文件夹
     */
    private void readFile(String targetPath) {
        try {
            String[] assetsList = ApkAssetsController.list(MainActivity.this, targetPath);
            if (assetsList.length > 0) {
                /**
                 * 创建preview文件夹,位于/storage/emulated/0/Android/data/com.android.assets/files/assets/preview
                 */
                File file = getExternalFilesDir(ASSETS + File.separator + targetPath);
                // 是一个目录
                for (String assetPath : assetsList) {
                    InputStream is;
                    try {
                        is = getAssets().open(targetPath + File.separator + assetPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    File picFile = new File(file.getAbsolutePath() + File.separator + assetPath);
                    FileOutputStream fos = new FileOutputStream(picFile);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    fos.flush();//刷新缓冲区
                    is.close();
                    fos.close();
                    Log.d(TAG, "当前文件：" + picFile.getPath());
                }
            } else {
                // 是一个空目录或者文件
                /**
                 * 创建preview文件夹,位于/storage/emulated/0/Android/data/com.android.assets/files/assets/customize_theme.properties
                 */
                File file = getExternalFilesDir(ASSETS);
                InputStream is = null;
                try {
                    is = getAssets().open(targetPath);
                } catch (IOException e) {
                    Log.d(TAG, targetPath + "是一个空目录或者不存在");
                }
                if (is != null) {
                    File picFile = new File(file.getAbsolutePath() + File.separator + targetPath);
                    FileOutputStream fos = new FileOutputStream(picFile);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    fos.flush();//刷新缓冲区
                    is.close();
                    fos.close();
                    Log.d(TAG, "当前文件：" + picFile.getPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取压缩文件
     *
     * @param targetPath
     * @return
     */
    public boolean unZipFile(String targetPath) {
        InputStream is = null;
        try {
            is = getAssets().open(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry zipEntry = null;
        try {
            zipEntry = zipIs.getNextEntry();
            while (zipEntry != null) {
                String szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    File folder = new File(targetPath + File.separator + szName);
                    folder.mkdirs();
                } else {
                    //先获取目录
                    File file = new File(getExternalFilesDir(ASSETS + File.separator + targetPath).getAbsolutePath() + File.separator + szName);
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    try {
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zipIs.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    } finally {
                        out.flush();
                        try {
                            out.getFD().sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Error e) {
                            e.printStackTrace();
                        }
                        out.close();
                    }
//                    initIcon(szName, file);
                }
                zipEntry = zipIs.getNextEntry();
            }
            return true;
        } catch (Exception e) {
            //FIXME what exception should notify user? we should think of it
            e.printStackTrace();
            return false;
        } catch (Error e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                zipIs.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            try {
                zipIs.close();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
    }
}
