package com.example.app1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.app1.databinding.FragmentFirstBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

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
    String url = "https://www.jsonkeeper.com/";
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

        // Obtain a reference to the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv = (TextView) view.findViewById(R.id.tv);
        tv.setText("initial text\n");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        my_api api = retrofit.create(my_api.class);

        Call<List<Plot>> call = api.getVillage();

        call.enqueue(new Callback<List<Plot>>() {
            @Override
            public void onResponse(Call<List<Plot>> call, Response<List<Plot>> response) {
                if (response.isSuccessful()) {
                    // Response is successful
                    List<Plot> data = response.body();
                    for(int i = 0; i < data.size() ; i++)
                        tv.append("gid - "+data.get(i).getGid() + " survey-no - " + data.get(i).getSurvey_no()+" \n\n\n");
                    addPolygonsToMap(data);
                } else {
                    // Handle unsuccessful response
                    Log.e("Retrofit", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Plot>> call, Throwable t) {
                // Handle failure
                Log.e("Retrofit", "Failed to fetch data: " + t.getMessage());
            }
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

        // Add a marker in the center of the map and move the camera
        LatLng markerPosition = new LatLng(0, 0); // Change the coordinates as needed
        googleMap.addMarker(new MarkerOptions().position(markerPosition).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
    }

    private void addPolygonsToMap(List<Plot> data) {
        for (Plot plot : data) {
            List<LatLng> coordinates = plot.getCoordinates();
            if (coordinates != null && !coordinates.isEmpty()) {
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(coordinates)
                        .strokeColor(0xFF0000FF) // Outline color
                        .fillColor(0x7F00FF00); // Fill color with transparency

                googleMap.addPolygon(polygonOptions);
            } else {
                // Handle case where coordinates are null or empty if necessary
                tv.append("Plot with gid " + plot.getCoordinates() + " has no coordinates\n");
            }
        }
    }
}
