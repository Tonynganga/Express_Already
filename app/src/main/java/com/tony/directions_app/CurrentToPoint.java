package com.tony.directions_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tony.directions_app.Models.PlaceInfo;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CurrentToPoint extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public CurrentToPoint(){}
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation2();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();

        }
    }

    private static final String TAG = "MainActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static  final float DEFAULT_ZOOM = 15f;
    private static  final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71,136));

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private AutoCompleteTextView mSearchText;
    private AutoCompleteTextView mSearchTextdest;

    private ImageView mGps;
    private Button btnfind;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient1;
    private PlaceInfo mPlace, mName;
    private TextView currentDistance, tvcurrentLocation;
    LocationManager locationManager;
    private DatabaseReference currenttopointRef, retriveCurrentRef, currenttopointdesRef, retrieveDestRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Marker mMyCurrentLocMarker, myDestinationMarker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_to_point, container, false);
//        mSearchText = view.findViewById(R.id.edtsource);
        mSearchTextdest = view.findViewById(R.id.edtdestinationcurrent);

        mGps = view.findViewById(R.id.ic_gps);
        btnfind = view.findViewById(R.id.btnfind);
        currentDistance = view.findViewById(R.id.currentDistance);
        tvcurrentLocation = view.findViewById(R.id.currentlocation);

        retriveCurrentRef = FirebaseDatabase.getInstance().getReference("Current To Point");
        currenttopointRef = FirebaseDatabase.getInstance().getReference().child("Current To Point");
        currenttopointdesRef = FirebaseDatabase.getInstance().getReference().child("Current To Point");
        retrieveDestRef = FirebaseDatabase.getInstance().getReference().child("Current To Point");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getLocationPermission();
        return  view;
    }
    private void init() {
        Log.d(TAG, "init: initializing");

        //google api client object
        mGoogleApiClient1 = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), 1, this)
                .build();
//        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mSearchTextdest.setOnItemClickListener(mAutocompleteClickListener);

        //placeautocompleteadapter object
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient1,
                LAT_LNG_BOUNDS, null);

        //place the autocompleteadapter in the edittext
//        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchTextdest.setAdapter(mPlaceAutocompleteAdapter);

        btnfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                geoLocate();
                Getlocation();
               //getDistance();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps location");
                getDeviceLocation2();
            }
        });
        //hideSoftKeyBoard();
        /*
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
        */
    }

    private void Getlocation(){
        String searchStringdestination = mSearchTextdest.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchStringdestination, 1);
        }catch (IOException e){
            Log.e(TAG, "Getlocation: IOException: " + e.getMessage() );
        }

        if (list.size() > 0) {
            Address addressdestination = list.get(0);

            endLatitude = addressdestination.getLatitude();
            endLongitude = addressdestination.getLongitude();
            Log.d(TAG, "geoLocate: found a location: " + addressdestination.toString());

            HashMap CurrentToPointHashMap = new HashMap();
            CurrentToPointHashMap.put("CDestinationlat", addressdestination.getLatitude());
            CurrentToPointHashMap.put("CDestinationlng", addressdestination.getLongitude());
            CurrentToPointHashMap.put("CDestinationName", searchStringdestination);
            CurrentToPointHashMap.put("CDestinationCountry", addressdestination.getCountryName());
            CurrentToPointHashMap.put("CDestinationLocality", addressdestination.getLocality());

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try {
                if (mLocationPermissionGranted){
                    double finalEndLatitude = endLatitude;
                    mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location != null) {
                                try {
                                    // moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

                                    Geocoder geocodercurrentLoc = new Geocoder(getActivity(), Locale.getDefault());

                                    List<Address> addresses = geocodercurrentLoc.getFromLocation(
                                            location.getLatitude(), location.getLongitude(), 1
                                    );

                                    tvcurrentLocation.setText(Html.fromHtml(
                                            "<font color='#6200EE'><b><b></font>"
                                                    + addresses.get(0).getCountryName()
                                                    +","
                                                    + addresses.get(0).getCountryCode()
                                                    +","
                                                    + addresses.get(0).getLocality()
                                                    +","
                                                    + addresses.get(0).getSubLocality()
                                                    +","
                                                    + addresses.get(0).getAddressLine(0)

                                    ));

                                    startLatitude = addresses.get(0).getLatitude();
                                    startLongitude = addresses.get(0).getLongitude();

                                    float[] distanceresults = new float[1];

                                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceresults);
                                    float distance = distanceresults[0];

                                    int currentKilometre = (int) (distance / 1000);
                                    currentDistance.setText(currentKilometre + " km");


                                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    Date date2 = new Date();
                                    String strDate2 = dateFormat2.format(date2).toString();

                                    CurrentToPointHashMap.put("CCurrentLocation Name", addresses.get(0).getLocality());
                                    CurrentToPointHashMap.put("CCurrentCountry", addresses.get(0).getCountryName());
                                    CurrentToPointHashMap.put("CCurrentLocality", addresses.get(0).getAdminArea());
                                    CurrentToPointHashMap.put("CCurrentlat", addresses.get(0).getLatitude());
                                    CurrentToPointHashMap.put("CCurrentlng", addresses.get(0).getLongitude());
                                    CurrentToPointHashMap.put("CDistance", currentKilometre + " Km");
                                    CurrentToPointHashMap.put("CDate", strDate2);

                                    currenttopointRef.child(mUser.getUid()).push().setValue(CurrentToPointHashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            retriveCurrentRef.child(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for(DataSnapshot snapshot: task.getResult().getChildren()){
                                                            Double currentLatitude = snapshot.child("CCurrentlat").getValue(Double.class);
                                                            Double currentLongitude = snapshot.child("CCurrentlng").getValue(Double.class);
                                                            String currentName = snapshot.child("CCurrentLocality").getValue().toString();
                                                            Double destinationLatitude = snapshot.child("CDestinationlat").getValue(Double.class);
                                                            Double destinationLongitude = snapshot.child("CDestinationlng").getValue(Double.class);
                                                            String destinationName = snapshot.child("CDestinationName").getValue().toString();

                                                            LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                                                            LatLng destinationLocation = new LatLng(destinationLatitude, destinationLongitude);

                                                            if (myDestinationMarker != null) {
                                                                myDestinationMarker.remove();
                                                            }
                                                            if (mMyCurrentLocMarker != null) {
                                                                mMyCurrentLocMarker.remove();
                                                            }

                                                            MarkerOptions userMarker3 = new MarkerOptions().position(currentLocation).title("Current Location " + currentName);
                                                            mMyCurrentLocMarker = mMap.addMarker(userMarker3);
                                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

                                                            MarkerOptions userMarker4 = new MarkerOptions().position(destinationLocation).title(destinationName);
                                                            myDestinationMarker = mMap.addMarker(userMarker4);
                                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, DEFAULT_ZOOM));

                                                        }
                                                    }
                                                }

                                            });

                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }catch (SecurityException e){
                Log.d(TAG, "getDeviceLocation: SecurityExcpetion" + e.getMessage());
            }
        }





        }




    @SuppressLint("MissingPermission")
    private void getDeviceLocation2(){

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionGranted){
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if (location != null) {

                            try {
                               moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );

                                tvcurrentLocation.setText(Html.fromHtml(
                                        "<font color='#6200EE'><b><b></font>"
                                                + addresses.get(0).getCountryName()
                                                +","
                                                + addresses.get(0).getCountryCode()
                                                +","
                                                + addresses.get(0).getLocality()
                                                +","
                                                + addresses.get(0).getSubLocality()
                                                +","
                                                + addresses.get(0).getAddressLine(0)

                                ));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityExcpetion" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera to lat");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        //hideSoftKeyBoard();

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: gettting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission granted");
                            mLocationPermissionGranted = true;
                            initMap();
                        }
                    }
                    mLocationPermissionGranted = false;
                    Log.d(TAG, "onRequestPermissionsResult: permissions failed");
                    return;

                }
            }
        }

    }

    /*private void hideSoftKeyBoard(){
        this.getParentFragment().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

     */

    /*
            -------------------------------------------google places API autocomplte suggestions-------------------------
    */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

            //hideSoftKeyBoard();

            final AutocompletePrediction item =  mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient1, placeId);

            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully" + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();

                String result = mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());

            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException " +e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient1.stopAutoManage(getActivity());
        mGoogleApiClient1.disconnect();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient1 != null && mGoogleApiClient1.isConnected()) {
            mGoogleApiClient1.stopAutoManage(getActivity());
            mGoogleApiClient1.disconnect();
        }
    }

}

