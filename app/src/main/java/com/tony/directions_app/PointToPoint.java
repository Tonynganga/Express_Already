package com.tony.directions_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

public class PointToPoint extends Fragment implements  OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public PointToPoint() {
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        //Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();

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
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient1;

    private AutoCompleteTextView mSearchText;
    private AutoCompleteTextView mSearchText2;
    private DatabaseReference pointtopointRef;
    private DatabaseReference retriveDestinationRef, retriveRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ImageView mGps;
    private Button btnfind;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private TextView tvdistance;
    Marker mMyFirstMarker, mySecondMarker;
    String psource, pdestination;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.point_to_point_fragment, container, false);
        mSearchText = view.findViewById(R.id.edtsource);
        mSearchText2 = view.findViewById(R.id.edtdestination);

        mGps = view.findViewById(R.id.ic_gps);
        btnfind = view.findViewById(R.id.btnfind);
        tvdistance = view.findViewById(R.id.distance);

        pointtopointRef = FirebaseDatabase.getInstance().getReference().child("Location To Location");
        retriveRef = FirebaseDatabase.getInstance().getReference("Location To Location");


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getLocationPermission();


        MainActivity activity = (MainActivity) getActivity();
        Bundle results = activity.getMyData();
        psource = results.getString("psource");
        pdestination = results.getString("pdestination");
        if(psource!=null && pdestination!=null){
//        Toast.makeText(activity, ""+psource, Toast.LENGTH_SHORT).show();
        try {
        Geocoder geocodertwo = new Geocoder(getActivity());

        List<Address> listtwo = new ArrayList<>();


            listtwo = geocodertwo.getFromLocationName(psource, 1);

            if (listtwo.size() > 0) {
                Address address = listtwo.get(0);
                double startLatitude = address.getLatitude();
                double startLongitude = address.getLongitude();

                HashMap sourceHashMap = new HashMap();
                sourceHashMap.put("PSourceName", psource);
                sourceHashMap.put("PSourceLocality", address.getLocality());
                sourceHashMap.put("PSourceCountry", address.getCountryName());
                sourceHashMap.put("PSourcelat", address.getLatitude());
                sourceHashMap.put("PSourcelng", address.getLongitude());


                Geocoder geocoderthree = new Geocoder(getActivity());
                List<Address> listthree = new ArrayList<>();
                    listthree = geocoderthree.getFromLocationName(pdestination, 1);
                    if (listthree.size() > 0) {
                        Address address1 = listthree.get(0);
                        double endLatitude = address1.getLatitude();
                        double endLongitude = address1.getLongitude();


                        float[] distanceresults = new float[1];
                        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceresults);
                        float distance = distanceresults[0];

                        int kilometre = (int) (distance / 1000);
                        tvdistance.setText(kilometre + " km");

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String strDate = dateFormat.format(date).toString();

                        Log.d(TAG, "geoLocate: found a location: " + address1.toString());

                        sourceHashMap.put("PDestinationName", pdestination);
                        sourceHashMap.put("PDestinationLocality", address1.getLocality());
                        sourceHashMap.put("PDestinationCountry", address1.getCountryName());
                        sourceHashMap.put("PDestinationlat", address1.getLatitude());
                        sourceHashMap.put("PDestinationlng", address1.getLongitude());
                        sourceHashMap.put("PDistance", kilometre + " Km");
                        sourceHashMap.put("PDate", strDate);


                        pointtopointRef.child(mUser.getUid()).push().setValue(sourceHashMap).addOnCompleteListener(new OnCompleteListener() {

                            @Override
                            public void onComplete(@NonNull Task task) {

                                retriveRef.child(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {


                                        if (task.isSuccessful()) {
                                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                                Toast.makeText(getActivity(), "" + psource, Toast.LENGTH_SHORT).show();
                                                Double sourcelatitude = snapshot.child("PSourcelat").getValue(Double.class);
                                                Double sourcelongitude = snapshot.child("PSourcelng").getValue(Double.class);
                                                Double destlatitude = snapshot.child("PDestinationlat").getValue(Double.class);
                                                Double destlongitude = snapshot.child("PDestinationlng").getValue(Double.class);
                                                String sourcename = snapshot.child("PSourceName").getValue().toString();
                                                String destname = snapshot.child("PDestinationName").getValue().toString();

                                                LatLng sourcelocation = new LatLng(sourcelatitude, sourcelongitude);
                                                LatLng destinationlocation = new LatLng(destlatitude, destlongitude);
                                                if (mMyFirstMarker != null) {
                                                    mMyFirstMarker.remove();
                                                }
                                                if (mySecondMarker != null) {
                                                    mySecondMarker.remove();
                                                }
                                                MarkerOptions userMarker1 = new MarkerOptions().position(sourcelocation).title(sourcename);

                                                mMyFirstMarker = mMap.addMarker(userMarker1);
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));

                                                MarkerOptions userMarker2 = new MarkerOptions().position(destinationlocation).title(destname);

                                                mySecondMarker = mMap.addMarker(userMarker2);
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));

                                            }


                                        }
                                    }


                                });

                            }
                        });
                    }

            }
            //Toast.makeText(activity, "text" + psource, Toast.LENGTH_SHORT).show();



        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "location2: IOException: " + e.getMessage());
        }
        }
        return view;
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        //google api client object
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),0,this)
                .build();
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mSearchText2.setOnItemClickListener(mAutocompleteClickListener);

        //placeautocompleteadapter object
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        //place the autocompleteadapter in the edittext
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText2.setAdapter(mPlaceAutocompleteAdapter);


        btnfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
                //location2();
                //getDistance();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps location");
                getDeviceLocation();
            }
        });
        //hideSoftKeyBoard();
        /*
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONEu
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
        */

    }

    private  void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());

        List<Address> list = new ArrayList<>();

        try {
           list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "location2: IOException: " + e.getMessage() );
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            double startLatitude = address.getLatitude();
            double startLongitude = address.getLongitude();

            HashMap sourceHashMap = new HashMap();
            sourceHashMap.put("PSourceName", searchString);
            sourceHashMap.put("PSourceLocality", address.getLocality());
            sourceHashMap.put("PSourceCountry", address.getCountryName());
            sourceHashMap.put("PSourcelat", address.getLatitude());
            sourceHashMap.put("PSourcelng", address.getLongitude());

            String searchString2 = mSearchText2.getText().toString();
            Geocoder geocoder1 = new Geocoder(getActivity());
            List<Address> list1 = new ArrayList<>();

            try {
                list1 = geocoder1.getFromLocationName(searchString2, 1);

            } catch (IOException e) {
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
            }

            if (list1.size() > 0) {
                Address address1 = list1.get(0);
                double endLatitude = address1.getLatitude();
                double endLongitude = address1.getLongitude();


                float[] distanceresults = new float[1];
                Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceresults);
                float distance = distanceresults[0];

                int kilometre = (int) (distance / 1000);
                tvdistance.setText(kilometre + " km");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String strDate = dateFormat.format(date).toString();

                Log.d(TAG, "geoLocate: found a location: " + address1.toString());

                sourceHashMap.put("PDestinationName", searchString2);
                sourceHashMap.put("PDestinationLocality", address1.getLocality());
                sourceHashMap.put("PDestinationCountry", address1.getCountryName());
                sourceHashMap.put("PDestinationlat", address1.getLatitude());
                sourceHashMap.put("PDestinationlng", address1.getLongitude());
                sourceHashMap.put("PDistance", kilometre + " Km");
                sourceHashMap.put("PDate", strDate);


                pointtopointRef.child(mUser.getUid()).push().setValue(sourceHashMap).addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        retriveRef.child(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {


                                if (task.isSuccessful()){
                                    for(DataSnapshot snapshot: task.getResult().getChildren()){
                                        Double sourcelatitude = snapshot.child("PSourcelat").getValue(Double.class);
                                        Double sourcelongitude = snapshot.child("PSourcelng").getValue(Double.class);
                                        Double destlatitude = snapshot.child("PDestinationlat").getValue(Double.class);
                                        Double destlongitude = snapshot.child("PDestinationlng").getValue(Double.class);
                                        String sourcename = snapshot.child("PSourceName").getValue().toString();
                                        String destname = snapshot.child("PDestinationName").getValue().toString();

                                        LatLng sourcelocation = new LatLng(sourcelatitude, sourcelongitude);
                                        LatLng destinationlocation = new LatLng(destlatitude, destlongitude);
                                        if (mMyFirstMarker != null) {
                                            mMyFirstMarker.remove();
                                        }
                                        if (mySecondMarker != null) {
                                            mySecondMarker.remove();
                                        }
                                        MarkerOptions userMarker1 = new MarkerOptions().position(sourcelocation).title(sourcename);

                                        mMyFirstMarker = mMap.addMarker(userMarker1);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));

                                        MarkerOptions userMarker2 = new MarkerOptions().position(destinationlocation).title(destname);

                                        mySecondMarker = mMap.addMarker(userMarker2);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));

                                    }


                                    }
                                }


                        });

                    }
                });
            }


        }


        }

//    private  void geoLocate2(){
//        Log.d(TAG, "geoLocate: geolocating");
//
//        Geocoder geocodertwo = new Geocoder(getActivity());
//
//        List<Address> listtwo = new ArrayList<>();
//
//        try {
//            listtwo = geocodertwo.getFromLocationName(psource, 1);
//        }catch (IOException e){
//            Log.e(TAG, "location2: IOException: " + e.getMessage() );
//        }
//        if (listtwo.size() > 0) {
//            Address address = listtwo.get(0);
//            double startLatitude = address.getLatitude();
//            double startLongitude = address.getLongitude();
//
//            HashMap sourceHashMap = new HashMap();
//            sourceHashMap.put("PSourceName", psource);
//            sourceHashMap.put("PSourceLocality", address.getLocality());
//            sourceHashMap.put("PSourceCountry", address.getCountryName());
//            sourceHashMap.put("PSourcelat", address.getLatitude());
//            sourceHashMap.put("PSourcelng", address.getLongitude());
//
//
//            Geocoder geocoderthree = new Geocoder(getActivity());
//            List<Address> listthree = new ArrayList<>();
//
//            try {
//                listthree = geocoderthree.getFromLocationName(pdestination, 1);
//
//            } catch (IOException e) {
//                Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
//            }
//
//            if (listthree.size() > 0) {
//                Address address1 = listthree.get(0);
//                double endLatitude = address1.getLatitude();
//                double endLongitude = address1.getLongitude();
//
//
//                float[] distanceresults = new float[1];
//                Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceresults);
//                float distance = distanceresults[0];
//
//                int kilometre = (int) (distance / 1000);
//                tvdistance.setText(kilometre + " km");
//
//                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                Date date = new Date();
//                String strDate = dateFormat.format(date).toString();
//
//                Log.d(TAG, "geoLocate: found a location: " + address1.toString());
//
//                sourceHashMap.put("PDestinationName", pdestination);
//                sourceHashMap.put("PDestinationLocality", address1.getLocality());
//                sourceHashMap.put("PDestinationCountry", address1.getCountryName());
//                sourceHashMap.put("PDestinationlat", address1.getLatitude());
//                sourceHashMap.put("PDestinationlng", address1.getLongitude());
//                sourceHashMap.put("PDistance", kilometre + " Km");
//                sourceHashMap.put("PDate", strDate);
//
//
//                pointtopointRef.child(mUser.getUid()).push().setValue(sourceHashMap).addOnCompleteListener(new OnCompleteListener() {
//
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//
//                        retriveRef.child(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//
//
//                                if (task.isSuccessful()){
//                                    for(DataSnapshot snapshot: task.getResult().getChildren()){
//                                        Double sourcelatitude = snapshot.child("PSourcelat").getValue(Double.class);
//                                        Double sourcelongitude = snapshot.child("PSourcelng").getValue(Double.class);
//                                        Double destlatitude = snapshot.child("PDestinationlat").getValue(Double.class);
//                                        Double destlongitude = snapshot.child("PDestinationlng").getValue(Double.class);
//                                        String sourcename = snapshot.child("PSourceName").getValue().toString();
//                                        String destname = snapshot.child("PDestinationName").getValue().toString();
//
//                                        LatLng sourcelocation = new LatLng(sourcelatitude, sourcelongitude);
//                                        LatLng destinationlocation = new LatLng(destlatitude, destlongitude);
//                                        if (mMyFirstMarker != null) {
//                                            mMyFirstMarker.remove();
//                                        }
//                                        if (mySecondMarker != null) {
//                                            mySecondMarker.remove();
//                                        }
//                                        MarkerOptions userMarker1 = new MarkerOptions().position(sourcelocation).title(sourcename);
//
//                                        mMyFirstMarker = mMap.addMarker(userMarker1);
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));
//
//                                        MarkerOptions userMarker2 = new MarkerOptions().position(destinationlocation).title(destname);
//
//                                        mySecondMarker = mMap.addMarker(userMarker2);
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourcelocation, DEFAULT_ZOOM));
//
//                                    }
//
//
//                                }
//                            }
//
//
//                        });
//
//                    }
//                });
//            }
//
//
//        }
//
//
//    }
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting devices currecnt location");

        mFusedLocationProviderClient1 = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient1.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

                        if (location != null) {
                            try {

                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
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
                    .getPlaceById(mGoogleApiClient, placeId);

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

                mPlace.setName(place.getName().toString());
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
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

}
