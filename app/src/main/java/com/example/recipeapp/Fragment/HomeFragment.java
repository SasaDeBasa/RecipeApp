package com.example.recipeapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.recipeapp.Activities.RecipeActivity;
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

        View fingerButton = RootView.findViewById(R.id.hfingerfood);
        fingerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "655400");
                intent.putExtra("title", "Pear and Pesto Crostini");
                intent.putExtra("img", "https://spoonacular.com/recipeImages/Pear-and-Pesto-Crostini-655400.jpg");
                startActivity(intent);
            }
        });

        View dessertButton = RootView.findViewById(R.id.hdesserts);
        dessertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "90629");
                intent.putExtra("title", "Baked Apples in White Wine");
                intent.putExtra("img", "https://img.spoonacular.com/recipes/90629-312x231.jpg");
                startActivity(intent);
            }
        });

        View apperButton = RootView.findViewById(R.id.happertizer);
        apperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "649944");
                intent.putExtra("title", "Lentil Mango Salad");
                intent.putExtra("img", "https://spoonacular.com/recipeImages/Lentill-Mango-Salad-649944.jpg");
                startActivity(intent);
            }
        });

        View beveragButton = RootView.findViewById(R.id.hbeverage);
        beveragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "644044");
                intent.putExtra("title", "Protein Strawberry Smoothie");
                intent.putExtra("img", "https://spoonacular.com/recipeImages/Fruity-Milk-Spin-644044.jpg");
                startActivity(intent);
            }
        });

        View saladBtn = RootView.findViewById(R.id.hsalad);
        saladBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "632269");
                intent.putExtra("title", "Amaranth and Roast Veggie Salad");
                intent.putExtra("img", "https://spoonacular.com/recipeImages/Amaranth-and-Roast-Veggie-Salad-632269.jpg");
                startActivity(intent);
            }
        });

        View soupButton = RootView.findViewById(R.id.hsoup);
        soupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0; // or any other logic to get the correct position
                Recipe selectedRecipe = listRecipe.get(position);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("id", "716381");
                intent.putExtra("title", "Nigerian Snail Stew");
                intent.putExtra("img", "https://spoonacular.com/recipeImages/nigerian-snail-stew-716381.jpg");
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
        // Inflate the layout for this fragment
        return RootView;

    }


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