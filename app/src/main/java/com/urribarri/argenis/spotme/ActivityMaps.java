package com.urribarri.argenis.spotme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.Geometry;
import com.cocoahero.android.geojson.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    private final static int INFO = 0;// request code - ActivityforResult
    private GoogleMap mMap;
    private ArrayList<LatLng> temp= new ArrayList<LatLng>();//vertex list
    private ArrayList<ObjDraft> pre_drafts = new ArrayList<ObjDraft>();//component list
    private ArrayList<ObjTerr> pre_terr= new ArrayList<ObjTerr>();// list of component_list
    private ArrayList<Position> pos= new ArrayList<>();

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

        /** TODO: read to see if there are terr to build */

        // Map Listeners on variables [ON/OFF]
        final GoogleMap.OnMapClickListener onMapClickListener=
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.i("SPOTME", "--------------------[onMapClick]");

                        mMap.setOnMapLongClickListener(null);// [OFF]
                        temp.add(latLng);
                        p(latLng);

                        if (ObjDraft.draftValidation(temp.size()) == true)
                            btn.setVisibility(View.VISIBLE);
                        ObjDraft.makePolyline(mMap,temp);
                    }
                };

        final GoogleMap.OnMapLongClickListener onMapLongClickListener=
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Log.i("SPOTME", "--------------------[onMapLongClick]");
                        mMap.setOnMapLongClickListener(null);
                        // if longclick not null
                        temp.add(latLng);
                        p(latLng);

                        btn_terr.setVisibility(View.VISIBLE);
                        mMap.setOnMapClickListener(onMapClickListener);

                    }
                };

        // Initial toposition; TODO: customize
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

                                /** TODO: ActivityForResult - SAVE (title...)*/
                                afr();
                                mMap.setOnMapLongClickListener(onMapLongClickListener);// [ON]

                                break;

                            default:
                                buildTerr("defaultTerr");// build one list to make a terr (0)
                                //TODO: RESET the working draft
                                btn.setVisibility(View.INVISIBLE);
                                v.setVisibility(View.INVISIBLE);

                                /** TODO: ActivityForResult - SAVE (title...)*/
                                afr();

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

                    /** TODO: ActivityForResult - SAVE (title...)*/
                    afr();
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
                Log.i("SPOTME", "onPolygonClick: "+polygon.getPoints().toString());
            }
        });

    }

    // -------------------
    /**Parse as position */
    private void p(LatLng latLng){
        pos.add(GeoJSONBuilder.toposition(latLng));
    }

    /**ActivityForResult */
    private void afr(){
        Intent i = new Intent(ActivityMaps.this, ActivityMapsSave.class);
        startActivityForResult(i, INFO);
    }

    // -------------------

    /**TODO: [unfinished]To retrieve further data when finishing a terr and make the persistence */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_CANCELED) {
            // Si es así mostramos mensaje de cancelado por pantalla.
            Log.i("SPOTME", "");
        } else {
            // De lo contrario, recogemos el resultado de la segunda actividad.
            String resultado = data.getExtras().getString("RESULTADO");
            // Y tratamos el resultado en función de si se lanzó para rellenar el
            // nombre o el apellido.
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

    /**To make draft */
    private void buildDraft(){
        ObjDraft objDraft= new ObjDraft((ArrayList<LatLng>) temp.clone());
        pre_drafts.add(objDraft);

        pos.add(pos.get(0));/**GeoJSON implicitly requires that the last coordinate is the same as the first*/

        //TODO: working implementation of the framework (GeoJSON)
        Object polygon= GeoJSONBuilder.geometry("Polygon",pos);

        Feature feature = new Feature((Geometry) polygon);
        feature.setIdentifier("MyIdentifier");
        feature.setProperties(new JSONObject());

        // Convert to formatted JSONObject
        try {
            JSONObject geoJSON = feature.toJSON();
            Log.i("SPOTME:", String.valueOf(geoJSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ObjDraft.makePolygon(mMap,objDraft);
        temp.clear();
        pos.clear();
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