package com.example.monapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Itineraire extends AppCompatActivity {

    private MapView mapView;
    private  List<GeoPoint> geoPoints = new ArrayList<>();
    private Button btnRetourItineraire;
    private Button btnTerminee;
    private TextView textViewTimeDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itineraire);

        mapView = findViewById(R.id.mapView2);
        btnTerminee = findViewById(R.id.btnTerminee);


        Context ctx = getApplicationContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // Initialisation OSMDroid
        org.osmdroid.config.Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));

        // Définissez le TileSource de la carte
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Définissez les coordonnées de l'emplacement que vous souhaitez pointer
        double latitude = 37.7749; // Exemple : latitude de San Francisco
        double longitude = -122.4194; // Exemple : longitude de San Francisco

        IMapController mapController = mapView.getController();
        mapController.setZoom(20.0);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        List<GeoPoint> testGeoPoints = getSampleGeoPoints();
        mapController.setCenter(testGeoPoints.get(0));
        //BoundingBox boundingBox = BoundingBox.fromGeoPoints(testGeoPoints);
         //mapView.zoomToBoundingBox(boundingBox, true);

        btnRetourItineraire = findViewById(R.id.btnRetourItineraire);
        textViewTimeDistance = findViewById(R.id.textViewTimeDistance);



        //bouton retour
        btnRetourItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermer l'activité actuelle
                finish();
            }
        });

        // Add markers
        addMarkers(testGeoPoints);

        // Add polyline
        //addPolyline(testGeoPoints);
        new  HttpPostRequest().execute("https://api.openrouteservice.org/v2/directions/driving-car/geojson","https://api.openrouteservice.org/v2/matrix/driving-car");

    }
    private void addMarkers(List<GeoPoint> geoPoints) {
        List<OverlayItem> items = new ArrayList<>();
        for (GeoPoint geoPoint : geoPoints) {
            OverlayItem overlayItem = new OverlayItem("Marker Title", "Marker Description", geoPoint);
            items.add(overlayItem);
        }

        ItemizedIconOverlay<OverlayItem> markerOverlay = new ItemizedIconOverlay<>(items, null, this);
        mapView.getOverlayManager().add(markerOverlay);
    }

    private void addPolyline(List<GeoPoint> geoPoints) {
        Polyline polyline = new Polyline();
        polyline.setPoints(geoPoints);
        polyline.setColor(0x550000FF); // Red color
        polyline.setWidth(5); // Stroke width of 5 pixels

        mapView.getOverlayManager().add(polyline);
    }

    private List<GeoPoint> getSampleGeoPoints() {
        // Receive the list of GeoPoints
        List<GeoPoint> receivedGeoPoints = new ArrayList<>() ;
                //getIntent().getParcelableArrayListExtra("geoPoints");
        ArrayList<String> geoPoint = getIntent().getStringArrayListExtra("geoPoints");

        /*geoPoints.add(new GeoPoint(49.41461,8.681495)); // San Francisco, CA geoPoints.add(new GeoPoint(34.0522, -118.2437)); // Los Angeles, CA
        geoPoints.add(new GeoPoint(49.41943,8.686507));  // New York, NY
        geoPoints.add(new GeoPoint(49.420318,8.687872));  // New York, NY
        geoPoints.add(new GeoPoint(49.422318,8.707872));  // New York, NY
        geoPoints.add(new GeoPoint(49.424318,8.787872));  // New York, NY
        geoPoints.add(new GeoPoint(49.425318,8.887872));  // New York, NY
        return geoPoints;*/
        for (String points : geoPoint){
           String[] p ;
           p = points.split(",");
          receivedGeoPoints.add(new GeoPoint(Float.valueOf(p[0]), Float.valueOf(p[1])));


        }
        geoPoints=receivedGeoPoints;
        return receivedGeoPoints;
    }

    private class HttpPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                List<List<Double>> distanceList = new ArrayList<>();
//data = json.loads(received_json)
                JSONObject json = new JSONObject(makeHttpRequest1(urls[1]));

                JSONObject json2 = new JSONObject(makeHttpRequest1(urls[1]));
                JSONArray distances = json.getJSONArray("distances");
                JSONArray time = json.getJSONArray("durations");
                StringBuilder distancesAndDurationsText = new StringBuilder("Distances and Durations: ");
                for (int j = 0; j < distances.length(); j++) {
                    JSONArray dist = distances.getJSONArray(j);
                    JSONArray timer = time.getJSONArray(j);
                    double dista = dist.getDouble(0);
                    double timerr = timer.getDouble(0);


                    List<Double> distanceTimeArray = new ArrayList<>();
                    distanceTimeArray.add(dista);
                    distanceTimeArray.add(timerr);

                    distanceList.add(distanceTimeArray);

                    // Append distances and durations to the StringBuilder
                    distancesAndDurationsText
                            .append("étape "+(j+1)+" Distance: ").append(dista).append(", ")
                            .append("Duration: ").append(timerr)
                            .append(" | ");
                }
// Display distances and durations
                textViewTimeDistance.setText(distancesAndDurationsText.toString());
                return makeHttpRequest(urls[0]);
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result on the main thread
            Log.d("HTTP_RESULT", result);
            // Process the result as needed
            parseGeoJsonAndUpdateMap(result);
        }

        private String makeHttpRequest(String url) throws IOException {
            OkHttpClient client = new OkHttpClient();

            // Example JSON payload
            // String jsonPayload = "{\"coordinates\":[[8.681495,49.41461],[8.686507,49.41943],[8.687872,49.420318]]}";
            // String jsonPayload = "{\"coordinates\":["+getSampleGeoPoints()+"]}";
            String jsonPayload = convertGeoPointsToJsonArray(geoPoints);
            jsonPayload = "{\"coordinates\":"+jsonPayload+"}";
            Log.d("HTTP_RESULT", jsonPayload);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonPayload);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "5b3ce3597851110001cf62484ae25bddd1094c6795c62d0695409071")
                    .addHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }
    }

    private String makeHttpRequest1(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Example JSON payload
        String jsonPayload = convertGeoPointsToJsonArray(geoPoints);
        jsonPayload = "{\"locations\":" + jsonPayload + ",\"metrics\":[\"distance\",\"duration\"]}";
        Log.d("HTTP_RESULT", jsonPayload);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonPayload);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "5b3ce3597851110001cf62484ae25bddd1094c6795c62d0695409071")
                .addHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    private String convertGeoPointsToJsonArray(List<GeoPoint> geoPoints) {
        StringBuilder jsonStringBuilder = new StringBuilder("[");
        for (GeoPoint geoPoint : geoPoints) {
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();
            jsonStringBuilder.append("[")
                    .append(longitude)
                    .append(",")
                    .append(latitude)
                    .append("],");
        }
        // Remove the trailing comma
        if (jsonStringBuilder.length() > 1) {
            jsonStringBuilder.setLength(jsonStringBuilder.length() - 1);
        }
        jsonStringBuilder.append("]");
        return jsonStringBuilder.toString();
    }
    private void parseGeoJsonAndUpdateMap(String geoJson) {
        try {
            JSONObject json = new JSONObject(geoJson);

            // Parse geometry coordinates from GeoJSON
            List<GeoPoint> parsedGeoPoints = parseGeoJsonCoordinates(json);

            // Update the map with the parsed coordinates by adding a polyline
            addPolylineToMap(parsedGeoPoints);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<GeoPoint> parseGeoJsonCoordinates(JSONObject json) throws JSONException {
        List<GeoPoint> geoPoints = new ArrayList<>();

        // Assuming your GeoJSON structure has a "coordinates" array
        if (json.has("features")) {
            JSONArray features = json.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);

                if (feature.has("geometry")) {
                    JSONObject geometry = feature.getJSONObject("geometry");

                    if (geometry.has("coordinates")) {
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        // Parse coordinates and create GeoPoint objects
                        for (int j = 0; j < coordinates.length(); j++) {
                            JSONArray coord = coordinates.getJSONArray(j);
                            double latitude = coord.getDouble(1);
                            double longitude = coord.getDouble(0);
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            geoPoints.add(geoPoint);
                        }
                    }
                }
            }
        }

        return geoPoints;
    }

    private void addPolylineToMap(List<GeoPoint> geoPoints) {
        // Add a new polyline with the parsed coordinates
        Polyline newPolyline = new Polyline();
        newPolyline.setPoints(geoPoints);
        newPolyline.setColor(0x55FF0000); // Semi-transparent red
        newPolyline.setWidth(5);

        mapView.getOverlayManager().add(newPolyline);
    }

}



