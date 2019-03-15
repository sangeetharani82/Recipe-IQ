package org.launchcode.projectRMS.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, max=30)
    private String ingredientName;

    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes;

    @OneToMany
    @JoinColumn(name = "ingredient_id")
    private List<Quantity> quantities;

    public Ingredient(){}

    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getId() {
        return id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public List<Quantity> getQuantities() {
        return quantities;
    }

    public void setQuantities(Quantity quantity){
        quantities.add(quantity);
    }
}
