package com.urribarri.argenis.spotme;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by argenis on 08/09/2016.
 Transparency for ARGB
 100% - FF
 95% - F2
 90% - E6
 85% - D9
 80% - CC
 75% - BF
 70% - B3
 65% - A6
 60% - 99
 55% - 8C
 50% - 80
 45% - 73
 40% - 66
 35% - 59
 30% - 4D
 25% - 40
 20% - 33
 15% - 26
 10% - 1A
 5% - 0D
 0% - 00
 */
public class ObjDraft{

    ArrayList<LatLng> userInput= new ArrayList<LatLng>();

    public ObjDraft(ArrayList<LatLng> userInput) {
        this.userInput = userInput;
    }

    public ArrayList<LatLng> getUserInput() {
        return userInput;
    }

    public static Polyline makePolyline (GoogleMap map, ArrayList<LatLng> input){
        Polyline polyline = map.addPolyline(new PolylineOptions()
                .addAll(input)
                .width(5)
                .color(0xA6000000));
        return polyline;
    }

    public static Polygon makePolygon(GoogleMap map, ObjDraft draft){
        Polygon polygon = map.addPolygon(new PolygonOptions()
                .addAll(draft.getUserInput())//property of draft
                .strokeWidth(6)
                .strokeColor(0x80000000) // black 50%
                .fillColor(0x66ffff00)) // yellow, 40%
                ;

        polygon.setClickable(true);// explicitly set
        return polygon;
    }

    public static Boolean draftValidation(int points){
        if (points > 2) {return true;} else {return false;}
    }
}