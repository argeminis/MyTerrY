package com.urribarri.argenis.spotme;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLineString;
import com.google.maps.android.geojson.GeoJsonPolygon;

import java.util.List;

public class GeoJSONBuilder {
    /*
    GeoJSON format specifications: ***http://geojson.org/geojson-spec.html
    GeoJSON validator: ***http://geojsonlint.com/
    GeoJSON implmentation for Android: ***https://github.com/cocoahero/android-geojson
    http://www.macwright.org/2015/03/23/geojson-second-bite.html [Info on building GeoJSON]

    Polygon ring order is undefined in GeoJSON, but there’s a useful default to acquire:
    the right hand rule. Specifically this means that
       - The exterior ring should be counterclockwise.
       - Interior rings should be clockwise.
    Es bueno definir claves para los campos extra de intents usando el nombre del
    paquete de tu app como prefijo. Esto garantiza que las claves sean únicas, en
    caso de que tu app interactúe con otras apps.
    */
    public static GeoJsonGeometry makeGeoJsonPolygon(final List<List<LatLng>> latLngs){

        GeoJsonGeometry geoJsonGeometry= new GeoJsonGeometry() {
            @Override
            public String getType() {
                return "polygon";
            }
            GeoJsonPolygon geoJsonPolygon= new GeoJsonPolygon(latLngs);
        };
        return geoJsonGeometry;
    }

    public static GeoJsonLineString makeLineString(List<LatLng>latLngs){
        return new GeoJsonLineString(latLngs);
    }
}