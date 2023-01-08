package com.example.recipeapp

import Connector.RecipeListConnector
import Model.Recipe
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class RecipeListActivity : AppCompatActivity(){

    private lateinit var recipeListRecyclerView: RecyclerView
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var recipeModel: Recipe

    private var adapter: RecyclerView.Adapter<RecipeListConnector.ViewHolder>? = null

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeListRecyclerView = findViewById(R.id.recycler_view_recipe_list)

        recipeList = arrayListOf<Recipe>()

        val recipeType = intent.getStringExtra("rType")

        recipeListRecyclerView.visibility = View.GONE

        val mAdapter = RecipeListConnector(this, recipeList)
        recipeListRecyclerView.adapter = mAdapter

        dbRef = FirebaseDatabase.getInstance().getReference("Recipes")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                recipeList.clear()

                if (snapshot.exists()){
                    for (data in snapshot.children) {
                        val recipe = data.getValue(Recipe::class.java)
                        recipeModel =  recipe!!

                        if (recipe.recipeType == recipeType) {
                            recipeList.add(recipe)
                        }
                    }

                    recipeListRecyclerView.layoutManager = LinearLayoutManager(this@RecipeListActivity)
                    recipeListRecyclerView.setHasFixedSize(true)
                    adapter = RecipeListConnector(this@RecipeListActivity, recipeList)

                    recipeListRecyclerView.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
