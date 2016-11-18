package com.urribarri.argenis.spotme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {
    // Comment

    private GoogleMap mMap;
    private ArrayList<LatLng> temp= new ArrayList<LatLng>();//vertex list
    private ArrayList<ObjDraft> pre_drafts = new ArrayList<ObjDraft>();//component list
    private ArrayList<ObjTerr> pre_terr= new ArrayList<ObjTerr>();// list of component_list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        // Confirm button - HIDDEN
        final Button btn = (Button) findViewById(R.id.btn);
        btn.setVisibility(View.INVISIBLE);
        final Button btn_terr = (Button) findViewById(R.id.btn_terr);
        btn_terr.setVisibility(View.INVISIBLE);

        // Map Listeners on variables [ON/OFF]
        final GoogleMap.OnMapClickListener onMapClickListener=
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.setOnMapLongClickListener(null);// [OFF]
                        temp.add(latLng);
                        ObjDraft.makePolyline(mMap,temp);

                    }
                };

        final GoogleMap.OnMapLongClickListener onMapLongClickListener=
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        ObjDraft.startPoint(mMap,latLng);
                        temp.add(latLng);
                        btn.setVisibility(View.VISIBLE);
                        btn_terr.setVisibility(View.VISIBLE);

                        mMap.setOnMapClickListener(onMapClickListener);
                    }
                };

        // Initial point; TODO: customize
        LatLng llissa = new LatLng(41.6, 2.24);
        mMap.setMinZoomPreference(13);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(llissa));

        // Activating EditMode
        mMap.setOnMapLongClickListener(onMapLongClickListener); // [ON]
        // I/Choreographer: Skipped 30 frames!  The application may be doing too much work on its main thread.

        // To save draft component
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjDraft.draftValidation(temp.size())== true){
                    v.setVisibility(View.INVISIBLE);
                    buildDraft();
                    mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                } else {
                    ManagerError.errorOnDraftPoints(getBaseContext());
                }
            }
        });

        // To save terr
        btn_terr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If there's an incomplete valid draft...
                if (ObjDraft.draftValidation(temp.size())== true) {
                    buildDraft();

                    //... then continue to the corresponding buildTerr
                    if (ObjTerr.draftListValidation(pre_drafts.size()) == true) {
                        switch (pre_drafts.size()) {
                            case 0: // ...NOT HAVING a draft builded
                                if (ObjDraft.draftValidation(temp.size()) == true) {
                                    btn.setVisibility(View.INVISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                    buildTerr("onedraftTerrQuick");
                                    mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                } else {
                                    ManagerError.errorOnDraftPoints(getBaseContext());
                                }
                                break;

                            case 1: // ...HAVING one draft builded
                                btn.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);
                                buildTerr("onedraftTerr");
                                mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                break;

                            default:
                                buildTerr("defaultTerr");// build one list to make a terr (0)
                                //TODO: RESET the working draft
                                btn.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);
                                mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                break;
                        }
                    } else {
                        ManagerError.errorOnDraftQuantity(getBaseContext());
                    }

                //...when builded drafts are to compound a terr
                } else if ((temp.size()==0)&(pre_drafts.size()>0)){
                    buildTerr("defaultTerr");// build one list to make a terr (0)
                    btn.setVisibility(View.INVISIBLE);
                    v.setVisibility(View.INVISIBLE);
                    mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                } else {
                    ManagerError.errorOnDraftPoints(getBaseContext());
                }
            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Log.i("SPOTME", "onPolygonClick: "+polygon.getId());
            }
        });

    }

    private void buildDraft(){
        ObjDraft objDraft= new ObjDraft((ArrayList<LatLng>) temp.clone());
        pre_drafts.add(objDraft);
        temp.clear();
    }

    private void buildTerr(String typeof){
        switch (typeof) {
            case "onedraftTerrQuick":
                buildDraft();

                ObjTerr _terr= new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());
                pre_terr.add(_terr);
                pre_drafts.clear();

                String _output= ManagerParser.JSONType(_terr);
                ManagerError.log("onedraftTerrQuick",_output);

                break;

            case "onedraftTerr":
                ObjTerr terr_ = new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());
                pre_terr.add(terr_);
                pre_drafts.clear();

                String output_= ManagerParser.JSONType(terr_);
                ManagerError.log("onedraftTerr",output_);

                break;

            case "defaultTerr":
                ObjTerr terr = new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());
                pre_terr.add(terr);
                pre_drafts.clear();

                String output= ManagerParser.JSONType(terr);
                ManagerError.log("onedraftTerr",output);

                break;
            }
        }
}
