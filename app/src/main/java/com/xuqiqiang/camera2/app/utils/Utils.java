package com.xuqiqiang.camera2.app.utils;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xuqiqiang.camera2.app.R;

import java.io.File;

public final class Utils {
    private static final String TAG = "Utils";

    public static void goToGallery(Context context, Uri uri) {
        if (uri == null) {
            Log.e(TAG, "uri is null");
            return;
        }

        if (tryGallery(context, uri)) return;

        File file = toFile(context, uri);
        if (file == null) {
            Log.e(TAG, "file is null");
            Toast.makeText(context, R.string.open_file_error, Toast.LENGTH_LONG).show();
            return;
        }

        Uri imageUri = getImageContentUri(context, file);
        if (imageUri != null && tryGallery(context, imageUri)) return;

        openFile(context, file);
    }

    private static boolean tryGallery(Context context, Uri uri) {

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(
                    "com.android.gallery3d", "com.android.gallery3d.app.GalleryActivity");
            intent.setAction(Intent.ACTION_VIEW);
            //intent.setDataAndType(uri,"image/*");
            intent.setData(uri);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if (componentName != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            fixIntent(context, intent, file);
            context.startActivity(intent);
            Log.d(TAG, "openFile");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.open_file_error, Toast.LENGTH_LONG).show();
//            ToastMaster.showToast(context, "无法打开文件");
        }
    }

    public static File toFile(Context context, Uri uri) {
        Log.d(TAG, "toFile " + uri.getPath());
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists() && file.isFile()) return file;
        }
        String[] arr = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, arr, null, null, null);
        if (cursor == null) return null;
        try {
            int imgIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(imgIndex);
            return new File(imgPath);
        } finally {
            cursor.close();
        }
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        try {
            String filePath = imageFile.getAbsolutePath();
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Intent fixIntent(Context context, Intent intent, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "openFile:" + context.getPackageName());
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, MimeUtils.getMIMEType(file));
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), MimeUtils.getMIMEType(file));
        }
        return intent;
    }
}
