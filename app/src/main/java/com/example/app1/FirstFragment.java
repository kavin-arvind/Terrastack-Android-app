package com.example.app1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.app1.databinding.FragmentFirstBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment implements OnMapReadyCallback {

    private FragmentFirstBinding binding;
    private GoogleMap googleMap;
    TextView tv;
    TextView village_name;
    Button submit_village;
    AutoCompleteTextView autoComplete;
    ArrayAdapter<String> adapterVillages;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // Drop down menu
        autoComplete = view.findViewById(R.id.auto_complete_txt);
        adapterVillages = new ArrayAdapter<>(view.getContext(), R.layout.list_village, Constants.village_list);
        autoComplete.setAdapter(adapterVillages);
        autoComplete.setOnItemClickListener((parent, view1, position, id) -> village_name.setText(adapterVillages.getItem(position)));

        // Obtain a reference to the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        tv = view.findViewById(R.id.tv);
        village_name = view.findViewById(R.id.village_name);
        submit_village = view.findViewById(R.id.submit_village);

        submit_village.setOnClickListener(v -> {

            // Clear the map before adding new polygons
            if (googleMap != null) {
                googleMap.clear();
            }
            tv.setText("");
            String village_name_str = village_name.getText().toString();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            my_api api = retrofit.create(my_api.class);

            Call<List<Plot>> call = api.getVillage(village_name_str);

            call.enqueue(new Callback<List<Plot>>() {
                @Override
                public void onResponse(@NonNull Call<List<Plot>> call, @NonNull Response<List<Plot>> response) {
                    if (response.isSuccessful()) {
                        // Response is successful
                        List<Plot> data = response.body();
                        village_name.setText(village_name_str);
                        if (data != null) {
                            for (int i = 0; i < data.size(); i++) {
                                Plot plot = data.get(i);
                                plot.setGeometry();
                                // tv.append("gid - " + plot.getGid() + " survey-tag - " + plot.getSurvey_tag() + "\n");
                                addPolygonToMap(plot);
                            }
                            moveCameraToPlots(data);
                        }
                    } else {
                        // Handle unsuccessful response
                        Log.e("Retrofit", "Response not successful: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Plot>> call, @NonNull Throwable t) {
                    // Handle failure
                    Log.e("Retrofit", "Failed to fetch data: " + t.getMessage());
                }
            });

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        map.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in the center of the map and move the camera
//        LatLng markerPosition = new LatLng(0, 0); // Change the coordinates as needed
//        googleMap.addMarker(new MarkerOptions().position(markerPosition).title("Marker"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
    }

    // Add polygon to map
    private void addPolygonToMap(Plot plot) {
        try {
            Geometry geometry = plot.getGeometry();
            // Convert JTS Geometry to Google Maps PolygonOptions
            PolygonOptions polygonOptions = convertGeometryToPolygonOptions(geometry);

            // checking for filled sub_division_no
            String sub_div_no = plot.getSub_division_no();
            if(sub_div_no == null){
                polygonOptions.fillColor(Constants.red);
            }
            else{
                polygonOptions.fillColor(Constants.green);
            }
            polygonOptions.strokeColor(Constants.stroke_color);
            polygonOptions.strokeWidth(Constants.stroke_width);

            // Add Polygon to the map
            googleMap.addPolygon(polygonOptions);

            // Calculate the centroid of the polygon
            Coordinate centroid = geometry.getCentroid().getCoordinate();
            LatLng centroidLatLng = new LatLng(centroid.y, centroid.x);

            // Add a marker at the centroid with an info window
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(centroidLatLng)
                    .title("Survey Tag: " + plot.getSurvey_tag())
                    .snippet("GID: " + plot.getGid());
            Marker marker = googleMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(plot);
            }

            // Set an info window click listener to display additional info
            googleMap.setOnInfoWindowClickListener(marker1 -> {
                // Handle the click event if needed
                Plot plot1 = (Plot) marker1.getTag();
                if (plot1 != null) {
                    try{
                        String plot_info = "";
                        if(plot1.getSurvey_tag() == null){
                            plot_info = getString(R.string.plot_info_null,
                                    plot1.getGid(),
                                    plot1.getDescription(),
                                    plot1.getVarp(),
                                    plot1.getSub_division_no());
                        }
                        else{
                            plot_info = getString(R.string.plot_info_not_null,
                                    plot1.getGid(),
                                    plot1.getSurvey_tag(),
                                    plot1.getSurvey_tag_gid(),
                                    plot1.getDescription(),
                                    plot1.getVarp(),
                                    plot1.getSub_division_no());
                        }
                        tv.setText(plot_info);
                    }
                    catch (Error e){
                        e.printStackTrace();
                    }
                }
                else{
                    tv.setText("Try Again.. Returned null value..\n");
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Convert JTS Geometry to Google Maps PolygonOptions
    private PolygonOptions convertGeometryToPolygonOptions(Geometry geometry) throws ParseException {
        PolygonOptions polygonOptions = new PolygonOptions();

        // Extract coordinates from JTS Geometry and add them to PolygonOptions
        for (int i = 0; i < geometry.getCoordinates().length; i++) {
            LatLng latLng = new LatLng(
                    geometry.getCoordinates()[i].y,
                    geometry.getCoordinates()[i].x
            );
            polygonOptions.add(latLng);
        }

        return polygonOptions;
    }

    private void moveCameraToPlots(List<Plot> plotsList) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (Plot plot : plotsList) {
            if (plot.getGeometry() != null) {
                // Iterate through the coordinates of the plot and include them in the bounds
                for (Coordinate coordinate : plot.getGeometry().getCoordinates()) {
                    boundsBuilder.include(new LatLng(coordinate.y, coordinate.x));
                }
            }
        }

        // Build the bounds
        LatLngBounds bounds = boundsBuilder.build();

        // Set padding around the bounds (optional)
        int padding = 100; // Padding in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // Move the camera to focus on the bounds
        googleMap.animateCamera(cameraUpdate);
    }
}
