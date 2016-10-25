package com.urribarri.argenis.spotme;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.maps.model.Polyline;

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

        // To save draft component
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjDraft.draftValidation(temp.size())== true){
                    v.setVisibility(View.INVISIBLE);
                    buildDraft();
                    temp.clear();

                } else {
                    Toast.makeText(MapsActivity.this,
                            "Please set at least 3 points",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // To save terr
        btn_terr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validDraft= ObjDraft.draftValidation(temp.size());

                // TODO: working with one draft terr; not working with > 1 draft terr
                //

                if (validDraft) {
                    buildDraft();// to add valid draft to arraylist
                    boolean validDraftList= ObjTerr.onedraftListValidation(pre_drafts.size());

                    if(validDraftList){
                        v.setVisibility(View.INVISIBLE);
                        btn.setVisibility(View.INVISIBLE);
                        buildTerr("onedraftTerr");// build one list with only one draft (1)
                        temp.clear();//TODO: RESET the working draft

                    } else {
                        v.setVisibility(View.INVISIBLE);
                        btn.setVisibility(View.INVISIBLE);
                        buildTerr("defaultTerr");// build one list to make a terr (0)
                        temp.clear();//TODO: RESET the working draft
                    }

                } else {
                    Toast.makeText(MapsActivity.this,
                            "Please set at least 3 points [other]",
                            Toast.LENGTH_SHORT).show();
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
            ObjDraft draft= new ObjDraft(temp);// feed with array
            ObjDraft.makePolygon(mMap,draft);// build polygon

            pre_drafts.add(draft);// add draft to pre_list(array)
            Log.i("SPOTME", "Added draft to pre_drafts ArrayList:"+ pre_drafts.size());

            //Empty for new draft
            //temp.clear();
        }

    private void buildTerr(String typeof){
        // TODO: confirmation msg "Theres only 1 component"
        switch (typeof) {
            case "onedraftTerr":
                // TODO: call the AlertDialog to confirm
                ObjTerr _terr= new ObjTerr(pre_drafts);
                pre_drafts.clear();
                pre_terr.add(_terr);

                break;

            case "defaultTerr":
                //TODO: create each draft ???
                ObjTerr terr = new ObjTerr(pre_drafts);// make draftLIST(terr)

                this.buildDraft();
                //Empty for new terr
                pre_drafts.clear();
                pre_terr.add(terr);// add terr to pre_list(array)

                Log.i("SPOTME", "Added terr to pre_terr ArrayList:" + pre_terr.size());

                break;
            }
        }


    }
