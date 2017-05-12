package com.urribarri.argenis.spotme;

import android.content.Intent;
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
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    private final static int INFO = 0;// request code - ActivityforResult
    private GoogleMap mMap;
    private GeoJsonLayer geoJsonLayer;
    private JSONObject jsonObject= new JSONObject();
    private List<List<LatLng>> latLngs = new ArrayList<List<LatLng>>();
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
        addGeoJSONLayer();

        // Confirm button - HIDDEN
        final Button btn_draft = (Button) findViewById(R.id.btn);
        btn_draft.setVisibility(View.INVISIBLE);
        final Button btn_terr = (Button) findViewById(R.id.btn_terr);
        btn_terr.setVisibility(View.INVISIBLE);

        /** TODO: read to see if there are terr to build */

        // Map Listeners on variables [ON/OFF]
        final GoogleMap.OnMapClickListener onMapClickListener=
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.i("SPOTME", "--------------------[onMapClick]");

                        mMap.setOnMapLongClickListener(null);// [OFF]
                        List<LatLng> point= new ArrayList<LatLng>();
                        point.add(latLng);
                        latLngs.add(point);

                        if (ObjDraft.draftValidation(latLngs.size())) {
                            btn_draft.setVisibility(View.VISIBLE);
                            //@makeline
                        }
                    }
                };

        final GoogleMap.OnMapLongClickListener onMapLongClickListener=
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Log.i("SPOTME", "--------------------[onMapLongClick]");
                        mMap.setOnMapLongClickListener(null);
                        // if longclick not null
                        List<LatLng> starting_point= new ArrayList<LatLng>();
                        starting_point.add(latLng);
                        latLngs.add(starting_point);

                        btn_terr.setVisibility(View.VISIBLE);
                        mMap.setOnMapClickListener(onMapClickListener);
                    }
                };

        // Initial position; TODO: customizable
        LatLng boliviamrkt = new LatLng(-16.494553, -68.174245);
        mMap.setMinZoomPreference(13);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(boliviamrkt));

        // Activating EditMode
        mMap.setOnMapLongClickListener(onMapLongClickListener); // [ON]

        // To save draft component
        btn_draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjDraft.draftValidation(latLngs.size())){
                    v.setVisibility(View.INVISIBLE);
                    try {
                        buildDraft();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                if (ObjDraft.draftValidation(latLngs.size())) {
                    try {
                        buildDraft();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //... then continue to the corresponding buildTerr
                    if (ObjTerr.draftListValidation(pre_drafts.size())) {
                        switch (pre_drafts.size()) {
                            case 0: // ...NOT HAVING a draft builded
                                if (ObjDraft.draftValidation(latLngs.size())) {
                                    btn_draft.setVisibility(View.INVISIBLE);
                                    v.setVisibility(View.INVISIBLE);
                                    try {
                                        buildTerr("onedraftTerrQuick");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                } else {
                                    ManagerError.errorOnDraftPoints(getBaseContext());
                                }
                                break;

                            case 1: // ...HAVING one draft builded
                                btn_draft.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);
                                try {
                                    buildTerr("onedraftTerr");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                /** TODO: ActivityForResult - SAVE (title...)*/
                                startActivityForResult();
                                mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                break;

                            default:
                                try {
                                    buildTerr("defaultTerr");// build one list to make a terr (0)
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //TODO: RESET the working draft
                                btn_draft.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);

                                /** TODO: ActivityForResult - SAVE (title...)*/
                                startActivityForResult();

                                mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                break;
                        }
                    } else {
                        ManagerError.errorOnDraftQuantity(getBaseContext());
                    }

                    //...when builded drafts are to compound a terr
                } else if ((latLngs.size()==0)&(pre_drafts.size()>0)){
                    try {
                        buildTerr("defaultTerr");// build one list to make a terr (0)
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    btn_draft.setVisibility(View.INVISIBLE);
                    v.setVisibility(View.INVISIBLE);

                    /** TODO: ActivityForResult - SAVE (title...)*/
                    startActivityForResult();
                    mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                } else {
                    ManagerError.errorOnDraftPoints(getBaseContext());
                }
            }
        });
    }

    /** ActivityForResult **/
    private void startActivityForResult(){
        Intent i = new Intent(ActivityMaps.this, ActivityMapsSave.class);
        startActivityForResult(i, INFO);
    }

    // -------------------

    /**TODO: [unfinished]To retrieve further data when finishing a terr and make the persistence */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Log.i("SPOTME", "");
        } else {
            String resultado = data.getExtras().getString("RESULTADO");
            switch (requestCode) {
                case 0:
                    Toast.makeText(ActivityMaps.this, resultado,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(ActivityMaps.this, "NO SE HA ENVIADO NADA",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void _DEV_(){

        GeoJsonPoint point = new GeoJsonPoint(new LatLng(-16.494553, -68.174245));
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("Ocean", "South Atlantic");
        GeoJsonFeature pointFeature = new GeoJsonFeature(point, "Origin", properties, null);
        geoJsonLayer.addFeature(pointFeature);

        /*GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(latLngs);
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("Ocean", "South Atlantic");
        GeoJsonFeature polygonfeature = new GeoJsonFeature(geoJsonPolygon, "Origin", properties, null);
        geoJsonLayer.addFeature(polygonfeature);
        */
    }

    /* Add GeoJSONlayer */
    private void addGeoJSONLayer(){
        geoJsonLayer= new GeoJsonLayer(mMap,jsonObject);
        geoJsonLayer.addLayerToMap();
    }

    /* Remove GeoJSONlayer */
    private void removeGeoJSONLayer(){
        geoJsonLayer.removeLayerFromMap();
    }

    /**To make draft */
    private void buildDraft() throws JSONException {
        //TODO: GeoJSON implicitly requires that the last coordinate is the same as the first(ring)
        latLngs.add(latLngs.get(0));
        _DEV_();
        ObjDraft objDraft= new ObjDraft(latLngs);
        ManagerJSON.objectToJsonObject(objDraft);
        GeoJSONBuilder.makeGeoJsonPolygon(latLngs);
        pre_drafts.add(objDraft);
        latLngs.clear();
    }

    private void buildTerr(String typeof) throws JSONException {
        switch (typeof) {
            case "onedraftTerrQuick":
                buildDraft();
                pre_terr.add(new ObjTerr(pre_drafts));
                pre_drafts.clear();
                break;

            case "onedraftTerr": case "defaultTerr":
                pre_terr.add(new ObjTerr(pre_drafts));
                pre_drafts.clear();
                break;
            }
        }
}