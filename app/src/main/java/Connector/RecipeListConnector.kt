package Connector

import Model.Recipe
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.RecipeInfoActivity
import com.squareup.picasso.Picasso

class RecipeListConnector(private val mContext: Context, private val recipeList: ArrayList<Recipe>) :
    RecyclerView.Adapter<RecipeListConnector.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_page, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newList = recipeList[position] //pass data
        val recipeList = recipeList[position]
        holder.tvRecName.text = recipeList.recipeName.toString()
        holder.tvRecDescription.text = recipeList.recipeDescription.toString()
        Picasso.get().load(recipeList.recipeImage).into(holder.ivRecImage)

        //navigate to layout
        holder.itemView.rootView.setOnClickListener {
            //set Data
            val mIntent = Intent(mContext, RecipeInfoActivity::class.java)
            mIntent.putExtra("recipeModel", newList)  //pass
            mContext.startActivity(mIntent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRecName : TextView = itemView.findViewById(R.id.list_food_name)
        val tvRecDescription : TextView = itemView.findViewById(R.id.list_food_description)
        val ivRecImage : ImageView = itemView.findViewById(R.id.list_food_img)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
