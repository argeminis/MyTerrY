package com.urribarri.argenis.spotme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

        //Edition mode
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                btn.setVisibility(View.VISIBLE);
                btn_terr.setVisibility(View.VISIBLE);

                ObjDraft.startPoint(mMap,latLng);
                temp.add(latLng);

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        temp.add(latLng);
                        //TODO: check if is possible to replace current polilyne on map
                        ObjDraft.makePolyline(mMap,temp);
                    }
                });

            }
        });

        // Initial point; TODO: customize
        LatLng llissa = new LatLng(41.6, 2.24);
        mMap.setMinZoomPreference(13);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(llissa));

        // I/Choreographer: Skipped 30 frames!  The application may be doing too much work on its main thread.

        // To save draft component
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMap.setOnMapLongClickListener(omlcl);// set long listener ON
                if (ObjDraft.draftValidation(temp.size())== true){
                    v.setVisibility(View.INVISIBLE);
                    buildDraft();

                } else {
                    ObjDraft.errorOnPoints(getBaseContext());
                }
            }
        });

        // To save terr
        btn_terr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if temp has more than 3 points; if so buildDraft and then continue
                if (ObjDraft.draftValidation(temp.size())== true){
                    buildDraft();

                    //----------------------
                    if (ObjTerr.draftListValidation(pre_drafts.size())== true){
                        switch (pre_drafts.size()) {
                            case 0:
                                //Check temp size
                                if (ObjDraft.draftValidation(temp.size())== true) {
                                    btn.setVisibility(View.INVISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                    buildTerr("onedraftTerrQuick");// NOT HAVING a draft builded
                                } else {
                                    ObjDraft.errorOnPoints(getBaseContext());
                                }
                                break;

                            case 1:
                                btn.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);
                                buildTerr("onedraftTerr");// HAVING one draft builded

                                break;

                            default:
                                buildTerr("defaultTerr");// build one list to make a terr (0)
                                //TODO: RESET the working draft
                                btn.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);

                                break;
                        }
                    } else {
                        ObjDraft.errorOnPoints(getBaseContext());
                    }
                } else {
                    ObjDraft.errorOnPoints(getBaseContext());
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
        // TODO: confirmation msg "Theres only 1 component"
        switch (typeof) {
            case "onedraftTerrQuick":
                // TODO: call the AlertDialog to confirm
                buildDraft();

                ObjTerr _terr= new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());// feed with array
                pre_terr.add(_terr);
                pre_drafts.clear();

                break;

            case "onedraftTerr":
                // TODO: call the AlertDialog to confirm
                ObjTerr terr_ = new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());
                pre_terr.add(terr_);
                pre_drafts.clear();

                break;

            case "defaultTerr":
                ObjTerr terr = new ObjTerr((ArrayList<ObjDraft>) pre_drafts.clone());// make draftLIST(terr)
                pre_terr.add(terr);// add terr to pre_list(array)
                pre_drafts.clear();//Empty for new terr

                break;
            }
        }
    }
