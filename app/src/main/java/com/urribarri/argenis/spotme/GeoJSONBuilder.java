package com.urribarri.argenis.spotme;

import android.util.Log;

import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by argenis on 11/01/2017.
 */

public class GeoJSONBuilder {

    // GeoJSON format specifications: ***http://geojson.org/geojson-spec.html
    // GeoJSON validator: ***http://geojsonlint.com/
    // GeoJSON implmentation for Android: ***https://github.com/cocoahero/android-geojson

    public static Position toposition(LatLng latLng){
        Position p= new Position(latLng.latitude,latLng.longitude,0.0);
        return p;
    }

    public static Object geometry (String type, ArrayList<Position> arrayList){
        Object object = null;

        switch (type) {
            case "Point":
                object= new Point();

                break;

            case "Polygon":
                Ring ring= new Ring();
                ring.addPositions(arrayList);
                object= new Polygon(ring);

                break;
            default:
                // code here;
                Log.i("SPOTME:", "No object created as geometry");

                break;
        }
        return object;
    }
}