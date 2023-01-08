package Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Recipe(
    var recipeID: String? = null,
    var recipeName: String? = null,
    var recipeDescription: String? = null,
    var recipeIngredient: String? = null,
    var recipeSteps: String? = null,
    var recipeImage: String? = null,
    var recipeType: String? = null,
): Parcelable