package com.example.recipeapp

import Model.Recipe
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class RecipeInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var btnUpdateImage: ImageView
    private var myUrl = " "
    private var pictureUri: Uri? = null
    private var storagePostPicture: StorageReference? = null
    private val pickImage = 100
    private var addRecipeType = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*get Whole Model Data*/
        var recipeModel= intent.getParcelableExtra<Recipe>("recipeModel")
        val infoRecipeImage = recipeModel?.recipeImage
        val infoRecipeName = recipeModel?.recipeName
        val infoRecipeType = recipeModel?.recipeType
        val infoRecipeDescription = recipeModel?.recipeDescription
        val infoRecipeIngredient = recipeModel?.recipeIngredient
        val infoRecipeSteps = recipeModel?.recipeSteps

        /*call text*/
        val info_recipe_image = findViewById<ImageView>(R.id.btn_add_img)
        val info_recipe_name = findViewById<TextView>(R.id.info_recipe_name)
        val info_recipe_type = findViewById<TextView>(R.id.info_recipe_type)
        val info_recipe_description = findViewById<TextView>(R.id.info_recipe_description)
        val info_recipe_ingredient = findViewById<TextView>(R.id.info_recipe_ingredients)
        val info_recipe_steps = findViewById<TextView>(R.id.info_recipe_steps)

        val btnEdit = findViewById<Button>(R.id.btn_edit)
        val btnDelete = findViewById<Button>(R.id.btn_delete)

        Picasso.get().load(infoRecipeImage).into (info_recipe_image)
        info_recipe_name.text = infoRecipeName
        info_recipe_type.text = infoRecipeType
        info_recipe_description.text = infoRecipeDescription
        info_recipe_ingredient.text = infoRecipeIngredient
        info_recipe_steps.text = infoRecipeSteps

        btnEdit.setOnClickListener {
            editRecipe(
                recipeModel!!
            )
        }

        btnDelete.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle(R.string.diaDelete)
                .setPositiveButton(R.string.diaYes){
                        dialog, _->
                    deleteRecipe( recipeModel?.recipeID.toString())
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.diaNo){
                        dialog, _->
                    dialog.dismiss()
                }
                .create()
                .show()

        }

    }

    private fun updatePhoto() {
        storagePostPicture = FirebaseStorage.getInstance().reference.child("Recipe Image")

        val fileRef = storagePostPicture!!.child(System.currentTimeMillis().toString() + ".jpg")

        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(pictureUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
//                    progressDialog.dismiss()
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                myUrl = downloadUrl.toString()

            }
        })
    }

    private fun editRecipe(recipe: Recipe) {

        val builder = MaterialAlertDialogBuilder(this)

        val inflater = LayoutInflater.from(this)

        val mDialogView = inflater.inflate(R.layout.edit_dialog, null)

        btnUpdateImage = mDialogView.findViewById(R.id.btn_edit_img)
        val editRecName = mDialogView.findViewById<EditText>(R.id.edit_recipe_name)
        val editRecType = mDialogView.findViewById<Spinner>(R.id.edit_recipe_types)
        val editRecDescription = mDialogView.findViewById<EditText>(R.id.edit_recipe_description)
        val editRecIngredient = mDialogView.findViewById<EditText>(R.id.edit_recipe_ingredient)
        val editRecStep = mDialogView.findViewById<EditText>(R.id.edit_recipe_steps)

        /*Set the data (pass to dialog layout)*/
        Picasso.get().load(recipe.recipeImage).into (btnUpdateImage)
        editRecName.setText(recipe.recipeName)
        editRecDescription.setText(recipe.recipeDescription)
        editRecIngredient.setText(recipe.recipeIngredient)
        editRecStep.setText(recipe.recipeSteps)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            editRecType.adapter = adapter
            editRecType.onItemSelectedListener = this
        }

        val spinnerPosition: Int = adapter.getPosition(recipe?.recipeType)
        editRecType.setSelection(spinnerPosition)

        editRecType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                addRecipeType = parent?.getItemAtPosition(position).toString()
            }

        }

        btnUpdateImage.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)  //visit gallery
            startActivityForResult(gallery, pickImage)  // response to app  -> activity start
        }

        builder.setView(mDialogView)



        builder.setPositiveButton("Save") { p0, p1 ->
            val dbRecipe = FirebaseDatabase.getInstance().getReference("Recipes")

//            val image = Picasso.get().load(myUrl).into (btnUpdateImage)
//            val image = myUrl
            val name = editRecName.text.toString().trim()
            val des = editRecDescription.text.toString().trim()
            val ing = editRecIngredient.text.toString().trim()
            val step = editRecStep.text.toString().trim()
            val type = addRecipeType

            val recipe = Recipe(recipe.recipeID,name, des, ing, step, myUrl, type)

            dbRecipe.child(recipe.recipeID.toString()).setValue(recipe)

            Toast.makeText(applicationContext, R.string.updateSuccess, Toast.LENGTH_LONG).show()

            startActivity(Intent(this, MainActivity::class.java))
        }

        builder.setNegativeButton(R.string.cancel) { p0, p1 ->
        }

        val alert = builder.create()
        alert.show()



    }

    private fun deleteRecipe(recipeID: String){

        val dbRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeID)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, R.string.delSuccess, Toast.LENGTH_LONG).show()
            finish()

        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            pictureUri = data?.data
            btnUpdateImage.setImageURI(pictureUri)
            updatePhoto()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
        addRecipeType = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(this, R.string.reqType, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



}

