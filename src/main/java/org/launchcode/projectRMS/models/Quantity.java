package org.launchcode.projectRMS.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Quantity {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @ManyToOne
    private Recipe recipe;

    @NotNull
    @ManyToOne
    private Ingredient ingredient;

    @NotNull
    @Size(min = 1, message = "Specify the quantity")
    private String amount;

    public Quantity(Recipe recipe, Ingredient ingredient, String amount) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Quantity() {
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
