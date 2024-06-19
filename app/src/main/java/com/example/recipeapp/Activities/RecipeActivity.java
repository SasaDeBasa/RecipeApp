package com.example.recipeapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipeapp.Models.Ingredient;
import com.example.recipeapp.R;
import com.example.recipeapp.RecyclerView.RecyclerViewAdapterRecipeIngredient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {

    private TextView title, ready_in, servings, vegetarian, instructions, likes, carbs, protein, fats, calories;
    private ImageView img;
    private DatabaseReference mRootRef;
    private JSONArray ingredientsArr;
    private List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
    private RecyclerView myrv;
    private boolean like = false;
    public String api_key = "11288130bd24478fbca94f418c0082e3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Objects.requireNonNull(getSupportActionBar()).hide();

        final Intent intent = getIntent();
        final String recipeId = Objects.requireNonNull(intent.getExtras()).getString("id");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String id = mAuth.getCurrentUser().getUid();
        System.out.println(recipeId);
        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Bookmarks").child(recipeId);
        Button fab = findViewById(R.id.fab);
        if(!like){
            fab.setText("REMOVE FROM BOOKMARKS");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = !like;
                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            Map favorites = new HashMap();
                            favorites.put("img", intent.getExtras().getString("img"));
                            favorites.put("title", intent.getExtras().getString("title"));
                            mRootRef.setValue(favorites);
                            Toast.makeText(RecipeActivity.this, "The recipe has been Bookmarked.", Toast.LENGTH_SHORT).show();
                            fab.setText("REMOVE FROM BOOKMARKS");
                        } else {
                            try {
                                mRootRef.setValue(null);
                                fab.setText("ADD TO BOOKMARKS");
                                Toast.makeText(RecipeActivity.this, "The recipe has been Removed.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String e = databaseError.toString();
                        Toast.makeText(RecipeActivity.this, e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        img = findViewById(R.id.recipe_ingredient_img);
        title = findViewById(R.id.recipe_name);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        likes = findViewById(R.id.recipe_likes);
        vegetarian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        getRecipeData(recipeId);
        carbs = findViewById(R.id.recipe_carbs_tv);
        calories = findViewById(R.id.recipe_calories_tv);
        protein = findViewById(R.id.recipe_protein_tv);
        fats = findViewById(R.id.recipe_fat_tv);
        getRecipeNutrition(recipeId);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mRootRef", String.valueOf(dataSnapshot));
                if (dataSnapshot.getValue() != null) {
                    like = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myrv = findViewById(R.id.recipe_ingredients_rv);
        myrv.setLayoutManager(new GridLayoutManager(this, 1));
    }


    private void getRecipeData(final String recipeId) {
        String URL = " https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey="+ api_key;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            try {
                                Picasso.get().load((String) response.get("image")).into(img);
                            }
                            catch (Exception e){
                                img.setImageResource(R.drawable.nopicture);
                            }
                            title.setText((String) response.get("title"));
                            ready_in.setText(Integer.toString((Integer) response.get("readyInMinutes"))+" Minutes");
                            servings.setText(Integer.toString((Integer) response.get("servings"))+" Servings");
                            likes.setText(Integer.toString((Integer) response.get("aggregateLikes")));
                            if ((boolean) response.get("vegetarian")) {
                                vegetarian.setText("Vegetarian");
                            }
                            else{
                                vegetarian.setText("Non-\nVegetarian");
                            }
                            try{
                                if(response.get("instructions").equals("")){
                                    throw new Exception("No Instructions");
                                }
                                else
                                    instructions.setText(Html.fromHtml((String) response.get("instructions")));
                            }
                            catch(Exception e){
                                String msg= "Unfortunately, the recipe you were looking for not found, " +
                                        "to view the original recipe click on the link below:" +
                                        "<a href="+response.get("spoonacularSourceUrl")+">"
                                        +response.get("spoonacularSourceUrl")+"</a>";
                                instructions.setMovementMethod(LinkMovementMethod.getInstance());
                                instructions.setText(Html.fromHtml(msg));
                            }
                            ingredientsArr = (JSONArray) response.get("extendedIngredients");
                            for (int i = 0; i < ingredientsArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = ingredientsArr.getJSONObject(i);
                                ingredientsList.add(new Ingredient(jsonObject1.optString("original"),
                                        jsonObject1.optString("image")));
                            }
                            RecyclerViewAdapterRecipeIngredient myAdapter =
                                    new RecyclerViewAdapterRecipeIngredient(getApplicationContext(), ingredientsList);
                            myrv.setAdapter(myAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getRecipeNutrition(final String recipeId){
        String URL = " https://api.spoonacular.com/recipes/" + recipeId +
                "/nutritionWidget.json?apiKey="+api_key;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            carbs.setText(String.format("%s\nCarbs", response.get("carbs")));
                            calories.setText((String) response.get("calories")+"\nCalories");
                            fats.setText((String) response.get("fat")+"\nFat");
                            protein.setText((String) response.get("protein")+"\nProtein");
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest2);
    }
}
