package com.dila.apprawat.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dila.apprawat.R;
import com.dila.apprawat.network.api.URLServer;
import com.dila.apprawat.network.helper.VolleyMultipartRequest;
import com.dila.apprawat.network.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UploadImage extends AppCompatActivity {

    private SharedPreferences preferences;
    private VolleyMultipartRequest postPoto;
    private Uri imageUri;
    private final int kodeGallery = 1, kodeKamera = 0;
    private Bitmap bitmap = null;
    private ImageView image, btn_kembali;
    private LinearLayout btn_upload, btn_gambarlain, btn_kirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        setInit();
        setButton();
    }

    private void setButton() {
        btn_gambarlain.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, kodeGallery);
        });

        btn_upload.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, kodeKamera);
            }
        });
        btn_kirim.setOnClickListener(v -> {
            uploadImage();
        });
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == kodeKamera && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(bitmap);
            Log.d("Respon", "Img Url: " + bitmap);
        } else if (requestCode == kodeGallery && resultCode == RESULT_OK && data != null) {
            try {
                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageURI(imageUri);

                Log.d("Respon", "Img Url: " + bitmap);
            } catch (Exception e) {
                Log.d("Respon", "Error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void uploadImage() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Loading...");
        dialog.show();
        String id = String.valueOf(preferences.getInt("id_m", 0));
        postPoto = new VolleyMultipartRequest(Request.Method.POST, URLServer.POSTGAMBARPFORILE, response -> {
            try {
                JSONObject object = new JSONObject(new String(response.data));
                if (object.getBoolean("status")) {
                    showSuccess("Gambar berhasil diupload!");
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            showError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_m", id);
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long namaGambar = System.currentTimeMillis();
                params.put("image", new DataPart(namaGambar + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        postPoto.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    dialog.dismiss();
                    Looper.prepare();
                    showError("Koneksi gagal!");
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postPoto);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void showSuccess(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .setContentText(string)
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setInit() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        image = findViewById(R.id.image);
        btn_upload = findViewById(R.id.btn_upload);
        btn_gambarlain = findViewById(R.id.btn_gambarlain);
        btn_kirim = findViewById(R.id.btn_kirim);
        btn_kembali = findViewById(R.id.btn_kembali);
    }
}