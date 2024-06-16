package com.example.recipeapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipeapp.Activities.LoginActivity;
import com.example.recipeapp.Activities.ProfileActivity;
import com.example.recipeapp.Models.Recipe;
import com.example.recipeapp.R;
import com.example.recipeapp.RecyclerView.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    private List<Recipe> listRecipe = new ArrayList<>();
    private JSONArray testArr;
    private RecyclerView myrv,horizontalRecyclerView;
    private ImageButton profile;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView emptyView, customApp;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    public String api_key = "11288130bd24478fbca94f418c0082e3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final View RootView = inflater.inflate(R.layout.fragment_home, container, false);
        profile = RootView.findViewById(R.id.profilePic);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        progressBar = RootView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        emptyView= RootView.findViewById(R.id.empty_view2);
        customApp = RootView.findViewById(R.id.appNameCustom);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/uber_medium.otf");
        customApp.setTypeface(customFont);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendToLogin();
        }
        myrv = RootView.findViewById(R.id.recyclerview);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        getRandomRecipes();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        ImageButton button1 = view.findViewById(R.id.hfingerfood);
//        ImageButton button2 = view.findViewById(R.id.hbeverage);
//        ImageButton button3 = view.findViewById(R.id.happertizer);
//        ImageButton button4 = view.findViewById(R.id.hdesserts);
//        ImageButton button5 = view.findViewById(R.id.hsalad);
//        ImageButton button6 = view.findViewById(R.id.hsoup);
//
//        button1.setOnClickListener(createOnClickListener("fingerfood"));
//        button2.setOnClickListener(createOnClickListener("beverage"));
//        button3.setOnClickListener(createOnClickListener("appertizer"));
//        button4.setOnClickListener(createOnClickListener("dessert"));
//        button5.setOnClickListener(createOnClickListener("salad"));
//        button6.setOnClickListener(createOnClickListener("soup"));
        // Inflate the layout for this fragment
        return RootView;

    }

//    private View.OnClickListener createOnClickListener(final String recipeName) {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("food_name", recipeName);
//                NavController navController = Navigation.findNavController(v);
//                navController.navigate(R.id.fragment_container, bundle);
//            }
//        };
//    }


    private void sendToLogin() {
        Intent sendToLogin = new Intent(getActivity(), LoginActivity.class);
        startActivity(sendToLogin);
        requireActivity().finish();
    }

    private void getRandomRecipes() {
        String URL = " https://api.spoonacular.com/recipes/random?number=40&apiKey="+api_key;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            testArr = (JSONArray) response.get("recipes");
                            Log.i("the res is:", String.valueOf(testArr));
                            for (int i = 0; i < testArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = testArr.getJSONObject(i);
                                listRecipe.add(new Recipe(jsonObject1.optString("id"),
                                        jsonObject1.optString("title"), jsonObject1.optString("image"),
                                        Integer.parseInt(jsonObject1.optString("servings")),
                                        Integer.parseInt(jsonObject1.optString("readyInMinutes" ))));
                            }
                            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(), listRecipe);
                            myrv.setAdapter(myAdapter);
                            progressBar.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        //progressBar.setVisibility(View.GONE);
                        myrv.setAlpha(0);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}