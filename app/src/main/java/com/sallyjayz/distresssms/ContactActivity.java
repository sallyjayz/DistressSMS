package com.sallyjayz.distresssms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sallyjayz.distresssms.adapter.ContactAdapter;
import com.sallyjayz.distresssms.model.DistressContact;
import com.sallyjayz.distresssms.sms.DistressSMS;
import com.sallyjayz.distresssms.viewmodel.ContactViewModel;

public class ContactActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int REQUEST_CODE = 99;
    private ContactViewModel mContactViewModel;
    private RecyclerView mRecyclerView;
    private ContactAdapter mContactAdapter;
    private boolean addButtonClicked = false;
    private boolean deleteButtonClicked = false;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static boolean permissionGranted;
    public static final int PERMISSION_ALL = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private Location lastKnownLocation;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 900000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 900000;
    private LocationCallback locationCallback;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkPermission();
        createLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mContactAdapter = new ContactAdapter(this);

        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        mContactViewModel.getAllDistressContacts().observe(this, distressContacts -> {
            if (distressContacts.size() > 0) {
                mContactAdapter.setDistressContact(distressContacts);
                mRecyclerView.setAdapter(mContactAdapter);
            }
        });

        deleteContact();
    }

    private void getContact() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        DistressContact contact = new DistressContact();

        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query
                            (contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                        String[] projection = new String[] {
                                ContactsContract.Contacts._ID,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                        };

                        Cursor cursorSelection = getContentResolver().query
                                (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                        String phone = "";
                        String emptyName = " ";
                        String phoneName = " ";
                        if (cursor.getCount() > 0) {
                            while (cursorSelection.moveToNext()) {
                                phone = cursorSelection.getString(cursorSelection.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneName = cursorSelection.getString(cursorSelection.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                            }
                        }

                        if (!phoneName.equalsIgnoreCase(emptyName)) {
                            contact.setContactName(name);
                            contact.setContactNumber(phone.trim());
                            contact.setContactImage(photoUri);
                            mContactViewModel.insert(contact);
                            Log.d("getContactsList", contact.getContactName() + "---" + contact.getContactNumber() + " -- " + contactId + " -- " + photoUri);
                        } else {
                            Toast.makeText(ContactActivity.this, "Select contact with a phone number", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        cursorSelection.close();
                    }
                    cursor.close();
                }
                break;
        }
    }

    private void checkPermission() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = false;
                    return false;
                } else {
                    permissionGranted = true;
                    return true;
                }
            }
        }
        return true;
    }


    public void addContact(View view) {
        addButtonClicked = false;
        mContactViewModel.getAllCount().observe(this, integer -> {
            if (!addButtonClicked) {
                addButtonClicked = true;
                if (integer < 5){
                    getContact();
                } else {
                    Toast.makeText(ContactActivity.this, "Contact cannot be more than five",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    public void getDeviceLocation() {
        try {
            if (permissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            onLocationChanged(lastKnownLocation);
                        } else {
                            Toast.makeText(ContactActivity.this, "Location Unknown", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    private void onLocationChanged(Location lastLocation) {
        mLatitude = lastLocation.getLatitude();
        mLongitude = lastLocation.getLongitude();
    }

    private void startLocationUpdates() {
        if (permissionGranted) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            }catch (SecurityException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        mContactViewModel.getAllPhoneNumbers().observe(this, strings -> {
                            for (int i = 0; i < strings.size(); i++) {
                                DistressSMS.smsSendMessage(Double.toString(mLatitude),
                                        Double.toString(mLongitude), strings.get(i));
                                Log.d("longlag", "Current location" + mLatitude + " " + mLongitude + " " + strings.get(i));
                            }
                        });
                    }

                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (permissionGranted) {
            getDeviceLocation();
        } else {
            finish();
        }
    }

    public void deleteAllContact(View view) {

        deleteButtonClicked = false;
        mContactViewModel.getAllCount().observe(this, integer -> {
            if (!deleteButtonClicked) {
                deleteButtonClicked = true;
                if (integer > 0) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ContactActivity.this);
                    builder.setMessage("Are you sure you want to delete all contacts?");
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        mContactViewModel.deleteAll();
                        mContactAdapter.deleteAll();
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                    builder.create();
                    builder.show();
                } else {
                    addButtonClicked = true;
                    Toast.makeText(ContactActivity.this, "You cannot delete an empty contact list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteContact() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final DistressContact distressContact = mContactAdapter.getContactAtPosition(position);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ContactActivity.this);
                builder.setMessage("Are you sure you want to delete " + distressContact.getContactName());
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                    mContactViewModel.deleteContact(distressContact);
                    mContactAdapter.removeContact(position);
                });
                builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                builder.create();
                builder.show();
                mContactAdapter.notifyDataSetChanged();

            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}