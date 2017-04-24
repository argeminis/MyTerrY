package com.urribarri.argenis.spotme;

import android.util.Log;

import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

class GeoJSONBuilder {

    // GeoJSON format specifications: ***http://geojson.org/geojson-spec.html
    // GeoJSON validator: ***http://geojsonlint.com/
    // GeoJSON implmentation for Android: ***https://github.com/cocoahero/android-geojson
    // http://www.macwright.org/2015/03/23/geojson-second-bite.html [Info on building GeoJSON]
    /*
    * Polygon ring order is undefined in GeoJSON, but there’s a useful default to acquire:
    * the right hand rule. Specifically this means that
    *   - The exterior ring should be counterclockwise.
    *   - Interior rings should be clockwise.

    * */

    static Position toposition(LatLng latLng){
        return new Position(latLng.latitude,latLng.longitude,0.0);
    }

    static Object geometry (String type, ArrayList<Position> arrayList){
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