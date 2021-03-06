package com.example.lastommg.SecondTab.Upload;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastommg.MyPage.AlbumAdapter;
import com.example.lastommg.SecondTab.GpsTracker;
import com.example.lastommg.SecondTab.Item;
import com.example.lastommg.SecondTab.ItemAdapter;
import com.example.lastommg.Login.App;
import com.example.lastommg.MainActivity;
import com.example.lastommg.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {
    Uri imageUri, photoURI;
    String mCurrentPhotoPath, address;
    int flag;

    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private AlbumAdapter mAlbumAdapter;

    double latitude, longitude, latitude2, longitude2;
    private GpsTracker gpsTracker;
    GeoPoint u_GeoPoint, s_GeoPoint;

    ImageView close, image_added;
    TextView post;
    EditText description, locate, phoneNumber, name;
    ImageButton gps;
    RecyclerView p_recyclerView;
    ItemAdapter itemAdapter;

    ProgressDialog progressDialog;

    public static Context p_context;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1111;
    private static final int FROM_CAMERA = 2222;
    private static final int FROM_ALBUM = 3333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        p_recyclerView = ((MainActivity) MainActivity.context_main).recyclerView;

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);
        locate = findViewById(R.id.locate);
        phoneNumber = findViewById(R.id.phoneNumber);
        gps = findViewById(R.id.btn_locate);

        gpsTracker = new GpsTracker(PostActivity.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        u_GeoPoint = new GeoPoint(latitude, longitude);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mAlbumAdapter = new AlbumAdapter();
        itemAdapter = new ItemAdapter();
        p_recyclerView.setAdapter(itemAdapter);

        p_context = this;
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyD19jDZHkiTXzRHVXpM66GK6m38IOfaFQ0");

        post.setEnabled(false);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent map = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(map);
                List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(PostActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private void makeDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle("?????? ?????????").setCancelable(false).setPositiveButton("????????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.v("??????", "??????????????? > ???????????? ??????");
                flag = 0;
                takePhoto();
                post.setEnabled(true);
            }

        }).setNeutralButton("????????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                Log.v("??????", "??????????????? > ???????????? ??????");
                flag = 1;
                getAlbum();
                post.setEnabled(true);
            }
        }).setNegativeButton("??????   ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.v("??????", "??????????????? > ?????? ??????");
                dialog.cancel();
            }
        });

        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void takePhoto() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                photoFile = createImageFile();

                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        } else {
            Log.v("??????", "??????????????? ?????? ?????????");
            return;
        }
    }

    private File createImageFile() {
        File imageFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ommg");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic() {
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // ?????? ????????? ?????? ????????? ?????????(?????? ????????? ???????????? ????????? ???????????? ??? ???)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void getAlbum() {
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, FROM_ALBUM);
    }

    private void uploadImage() {
        Uri file = null;
        App nickName = (App) getApplicationContext();

        progressDialog = new ProgressDialog(PostActivity.this);
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Timestamp timestamp = new Timestamp(new Date());
        String imageFileName = "JPEG" + timeStamp + "jpg";
        StorageReference filereference = storageReference.child("image/" + imageFileName);
        if (flag == 0) {
            //????????????
            file = Uri.fromFile(new File(mCurrentPhotoPath));
        } else if (flag == 1) {
            //????????????
            file = photoURI;
        }

        uploadTask = filereference.putFile(file);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostActivity.this, "????????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.v("??????", "?????? ????????? ??????");
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pathReference = storageReference.child("image");
                if (pathReference == null) {
                    Toast.makeText(PostActivity.this, "???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    StorageReference submitProfile = storageReference.child("image/" + imageFileName);
                    submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            address = getCurrentAddress(s_GeoPoint.getLatitude(), s_GeoPoint.getLongitude());
                            Item item = new Item(0,0,0, nickName.getNickname(), name.getText().toString(), description.getText().toString(), uri.toString(), phoneNumber.getText().toString(), s_GeoPoint, address, 0.0, timestamp);
                            itemAdapter.addItem(item);
                            if (item.getNickname().equals(nickName.getNickname())) {
                                mAlbumAdapter.addItem(item);
                                Log.i("aaaaaa", String.valueOf(mAlbumAdapter.getItemCount()));
                            }
                            Log.i("??????", item.getGeoPoint().toString());
                            db.collection("items").document(name.getText().toString()).set(item);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(PostActivity.this, "???????????? ????????????????????????.", Toast.LENGTH_SHORT).show();
                p_recyclerView.startLayoutAnimation();

                //postActivity ?????????
                Intent goBack2Main = new Intent(PostActivity.this, MainActivity.class);
                startActivity(goBack2Main);
            }
        });
    }

    public String getCurrentAddress(double latitude, double longitude) {
        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_CAMERA:
                    try {
                        Log.v("??????", "FROM_CAMERA ??????");
                        galleryAddPic();
                        image_added.setImageURI(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case FROM_ALBUM:
                    //???????????? ????????????
                    if (data.getData() != null) {
                        try {
                            photoURI = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                            image_added.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case AUTOCOMPLETE_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getLatLng());
                        String add = place.getAddress();
                        Geocoder geocoder = new Geocoder(this);
                        try {
                            List<Address> mResultLocation = geocoder.getFromLocationName(add, 1);
                            latitude2 = mResultLocation.get(0).getLatitude();
                            longitude2 = mResultLocation.get(0).getLongitude();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        locate.setText(place.getAddress());
                        name.setText(place.getName());
                        s_GeoPoint = new GeoPoint(latitude2, longitude2);
                        Log.i(TAG, "???????????????" + s_GeoPoint.getLatitude() + "," + s_GeoPoint.getLongitude());

                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i(TAG, status.getStatusMessage());
                    } else if (resultCode == RESULT_CANCELED) {
                        // The user canceled the operation.
                    }

                    break;
            }
        }
    }
}