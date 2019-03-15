package org.launchcode.projectRMS.models.forms;

import org.launchcode.projectRMS.models.Ingredient;
import org.launchcode.projectRMS.models.Recipe;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AddIngredientsToRecipe {

    private Recipe recipe;
    private Iterable<Ingredient> ingredients;

    @NotNull
    private int recipeId;

    @NotNull
    private List<Integer> ingredientIds;

    public AddIngredientsToRecipe(Recipe recipe, Iterable<Ingredient> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public AddIngredientsToRecipe() {
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Iterable<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public List<Integer> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(List<Integer> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }
}
