package com.example.recipeapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var getRecipeType : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_create_new_recipe = findViewById<Button>(R.id.btn_create_new_recipe)
        getRecipeType = findViewById<Button>(R.id.getRecipeType)

        btn_create_new_recipe.setOnClickListener({
            startActivity(Intent(this, AddNewRecipe::class.java))
        })

        val spinner = findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
        var recipeType = parent?.getItemAtPosition(pos).toString()

        getRecipeType.setOnClickListener{
            when(pos){
                0->{

                }
                else->{
                    val intent = Intent(this, RecipeListActivity::class.java)
                    intent.putExtra("rType", recipeType)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
//        Toast.makeText(this, R.string.selectRecipeType, Toast.LENGTH_SHORT).show()
    }
}