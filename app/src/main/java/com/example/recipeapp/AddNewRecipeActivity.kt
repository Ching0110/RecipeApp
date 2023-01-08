package com.example.recipeapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class AddNewRecipe : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var myUrl = ""
    private var pictureUri: Uri? = null
    private var storagePostPicture: StorageReference? = null
    private var addRecipeType = ""
    private val pickImage = 100
    private lateinit var btn_add_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

//        val database = Firebase.database

        btn_add_image = findViewById<ImageView>(R.id.btn_add_img)
        val add_recipe_types = findViewById<Spinner>(R.id.add_recipe_types)
        val btn_add_new_food = findViewById<Button>(R.id.btn_add_new_food)
        val btn_cancel = findViewById<Button>(R.id.btn_cancel)

        btn_add_image.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)  //visit gallery
            startActivityForResult(gallery, pickImage)  // response to app  -> activity start
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            add_recipe_types.adapter = adapter
            add_recipe_types.onItemSelectedListener = this
        }

        val spinnerPosition: Int = adapter.getPosition(addRecipeType)
        add_recipe_types.setSelection(spinnerPosition)

        btn_add_new_food.setOnClickListener {uploadNewRecipe()}

        btn_cancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun uploadPhoto() {
        storagePostPicture = FirebaseStorage.getInstance().reference.child("Recipe Image")

        val fileRef = storagePostPicture!!.child(System.currentTimeMillis().toString() + ".jpg")

        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(pictureUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it

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

    private fun uploadNewRecipe(){
        val recipe_name = findViewById<EditText>(R.id.recipe_name)
        val recipe_description = findViewById<EditText>(R.id.recipe_description)
        val recipe_ingredient = findViewById<EditText>(R.id.recipe_ingredient)
        val recipe_steps = findViewById<EditText>(R.id.recipe_steps)

        when{
            pictureUri == null -> {
                Toast.makeText(this, R.string.reqImg, Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(recipe_name.text.toString()) -> {
                Toast.makeText(this, R.string.reqName, Toast.LENGTH_LONG).show()
            }
            addRecipeType == "Choose Recipe Type" -> {
                Toast.makeText(this, R.string.reqType, Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(recipe_description.text.toString()) -> {
                Toast.makeText(this, R.string.reqDes, Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(recipe_ingredient.text.toString()) -> {
                Toast.makeText(this, R.string.reqIng, Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(recipe_steps.text.toString()) -> {
                Toast.makeText(this, R.string.reqStep, Toast.LENGTH_LONG).show()
            }
            else -> {
                val ref = FirebaseDatabase.getInstance().reference.child("Recipes")
                val recipeId = ref.push().key

                val recipeMap = HashMap<String, Any>()
                recipeMap["recipeID"] = recipeId!!
                recipeMap["recipeImage"] = myUrl
                recipeMap["recipeName"] = recipe_name.text.toString()
                recipeMap["recipeType"] = addRecipeType
                recipeMap["recipeDescription"] = recipe_description.text.toString()
                recipeMap["recipeIngredient"] = recipe_ingredient.text.toString()
                recipeMap["recipeSteps"] = recipe_steps.text.toString()

                ref.child(recipeId).updateChildren(recipeMap)

                Toast.makeText(this, R.string.addSucess, Toast.LENGTH_LONG).show()

                finish()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            pictureUri = data?.data
            btn_add_image.setImageURI(pictureUri)
            uploadPhoto()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
        addRecipeType = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(this, R.string.reqType, Toast.LENGTH_LONG).show()
    }

}





