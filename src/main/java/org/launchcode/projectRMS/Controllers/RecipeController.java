package org.launchcode.projectRMS.Controllers;


import org.launchcode.projectRMS.Comparators.CategoryComparator;
import org.launchcode.projectRMS.Comparators.CourseComparator;
import org.launchcode.projectRMS.Comparators.IngredientComparator;
import org.launchcode.projectRMS.Comparators.RecipeComparator;
import org.launchcode.projectRMS.models.*;
import org.launchcode.projectRMS.models.data.*;
import org.launchcode.projectRMS.models.forms.AddIngredientsToRecipe;
import org.launchcode.projectRMS.models.forms.AddQuantitiesToRecipe;
import org.launchcode.projectRMS.models.forms.AddRateCommentToRecipeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("recipe")
public class RecipeController {

    @Autowired
    RecipeDao recipeDao;

    @Autowired
    CourseDao courseDao;

    @Autowired
    IngredientDao ingredientDao;

    @Autowired
    QuantityDao quantityDao;

    @Autowired
    RateCommentDao rateCommentDao;

    @Autowired
    CategoryDao categoryDao;

    RecipeComparator recipeComparator = new RecipeComparator();
    CourseComparator courseComparator = new CourseComparator();
    CategoryComparator categoryComparator = new CategoryComparator();
    IngredientComparator ingredientComparator = new IngredientComparator();

    // Request path: /recipe
    @RequestMapping(value = "")
    public String index(Model model) {
        ArrayList<Recipe> lists = new ArrayList<>();
        for (Recipe recipe : recipeDao.findAll()){
            lists.add(recipe);
        }
        lists.sort(recipeComparator);
        model.addAttribute("recipes", lists);
        model.addAttribute("title", "Recipes");
        return "recipe/index";
    }

    // add/create a recipe
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddRecipeForm(Model model) {
        model.addAttribute("title", "Add a recipe");
        model.addAttribute(new Recipe());
        ArrayList<Course> courses = new ArrayList<>();
        for (Course course : courseDao.findAll()){
            courses.add(course);
        }
        courses.sort(courseComparator);

        ArrayList<Category> categories = new ArrayList<>();
        for (Category category : categoryDao.findAll()){
            categories.add(category);
        }
        categories.sort(categoryComparator);

        model.addAttribute("courses", courses);
        model.addAttribute("categories", categories);
        return "recipe/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddRecipeForm(Model model, @ModelAttribute @Valid Recipe newRecipe,
                                       Errors errors, @RequestParam int courseId, @RequestParam int categoryId) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add a recipe");
            ArrayList<Course> courses = new ArrayList<>();
            for (Course course : courseDao.findAll()){
                courses.add(course);
            }
            courses.sort(courseComparator);

            ArrayList<Category> categories = new ArrayList<>();
            for (Category category : categoryDao.findAll()){
                categories.add(category);
            }
            categories.sort(categoryComparator);

            model.addAttribute("courses", courses);
            model.addAttribute("categories", categories);
            return "recipe/add";
        }
        Course cor = courseDao.findOne(courseId);
        Category cat = categoryDao.findOne(categoryId);
        newRecipe.setCourse(cor);
        newRecipe.setCategory(cat);
        recipeDao.save(newRecipe);

        model.addAttribute("message", "Recipe added successfully!");
        model.addAttribute("title", "Add ingredients to " + newRecipe.getRecipeName());

        return "redirect:view/" + newRecipe.getId();
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable int id, Model model){
        Recipe recipe = recipeDao.findOne(id);
        List<Quantity> quantities = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()){
            for (Quantity quantity : ingredient.getQuantities()){
                quantities.add(quantity);
            }
        }
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("recipe", recipe);
        model.addAttribute("quantities", quantities);
        model.addAttribute("message", "Added successfully!");
        return "recipe/view";
    }

    @RequestMapping(value="add-ingredients/{recipeId}", method = RequestMethod.GET)
    public String displayAddIngredients(@PathVariable int recipeId, Model model){

        Recipe recipe = recipeDao.findOne(recipeId);
        AddIngredientsToRecipe form = new AddIngredientsToRecipe(recipe, ingredientDao.findAll());
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (Ingredient ingredient : ingredientDao.findAll()){
            ingredients.add(ingredient);
        }
        ingredients.sort(ingredientComparator);
        model.addAttribute("ingredients", ingredients);
        model.addAttribute("title", "Add Ingredients to " + recipe.getRecipeName());
        model.addAttribute("form", form);
        return "recipe/add-ingredients";
    }

    @RequestMapping(value="add-ingredients", method = RequestMethod.POST)
    public String processAddIngredients(@ModelAttribute @Valid AddIngredientsToRecipe form, Errors errors,
                                        Model model){
        if (errors.hasErrors()){
            model.addAttribute("form", form);
            return "recipe/add-ingredients";
        }
        List<Ingredient> recipeIngredients = new ArrayList<>();
        Recipe theRecipe = recipeDao.findOne(form.getRecipeId());
        for (int id : form.getIngredientIds()){
            Ingredient theIngredient = ingredientDao.findOne(id);
            recipeIngredients.add(theIngredient);
        }
        theRecipe.addIngredients(recipeIngredients);
        recipeDao.save(theRecipe);
        return "redirect:view/" + theRecipe.getId();
    }

    @RequestMapping(value = "specify-quantity/{recipeId}/{ingredientId}", method = RequestMethod.GET)
    public String displaySpecifyQuantityForm(@PathVariable int recipeId, @PathVariable int ingredientId, Model model){
        String quantity = "";
        Ingredient ingredient = ingredientDao.findOne(ingredientId);
        Recipe recipe = recipeDao.findOne(recipeId);
        AddQuantitiesToRecipe form = new AddQuantitiesToRecipe(recipe, ingredient, quantity);
        model.addAttribute("title", "Specify quantity for " + ingredient.getIngredientName());
        model.addAttribute("form", form);
        return "recipe/specify-quantity";
    }

    @RequestMapping(value="specify-quantity", method = RequestMethod.POST)
    public String processSpecifyQuantityForm(@ModelAttribute @Valid AddQuantitiesToRecipe form, Errors errors,
                                             Model model){
        if (errors.hasErrors()){
            model.addAttribute("form", form);
            return "recipe/specify-quantity";
        }
        Ingredient ingredient = ingredientDao.findOne(form.getIngredientId());
        Recipe recipe = recipeDao.findOne(form.getRecipeId());
        Quantity quantity = new Quantity(recipe, ingredient, form.getAmount());
        ingredient.setQuantities(quantity);
        quantityDao.save(quantity);
        recipe.addQuantities(quantity);
        recipeDao.save(recipe);
        ingredientDao.save(ingredient);
        return "redirect:view/" + recipe.getId();
    }

    //delete the ingredient and quantity from the recipe
    @RequestMapping(value = "remove/{quantityId}")
    public String removeIngredientAndQuantity(@PathVariable int quantityId, Model model){
        Quantity quantity = quantityDao.findOne(quantityId);
        quantityDao.delete(quantity);
        Recipe recipe = recipeDao.findOne(quantity.getRecipe().getId());
        recipe.removeIngredient(quantity.getIngredient());
        recipeDao.save(recipe);
        model.addAttribute("message", "Ingredient and Quantity removed successfully");
        return "recipe/message";
    }

    //view single recipe
    @RequestMapping(value="single/{id}", method = RequestMethod.GET)
    public String singleRecipe(@PathVariable int id, Model model){
        Recipe recipe = recipeDao.findOne(id);
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("course", recipe.getCourse());
        model.addAttribute("category", recipe.getCategory());
        model.addAttribute("recipe", recipe);
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("ingredients", recipe.getIngredients());
        model.addAttribute("quantities", recipe.getQuantities());
        return "recipe/single";
    }

    //recipes in a course
    @RequestMapping(value = "course", method = RequestMethod.GET)
    public String course(Model model, @RequestParam int id){
        Course cor = courseDao.findOne(id);
        List<Recipe> recipes = cor.getRecipes();
        model.addAttribute("recipes", recipes);
        model.addAttribute("title", cor.getCourseName() + " recipes");
        return "recipe/list-under";
    }
    //recipes in a category
    @RequestMapping(value = "category", method = RequestMethod.GET)
    public String category(Model model, @RequestParam int id){
        Category cat = categoryDao.findOne(id);
        List<Recipe> recipes = cat.getRecipes();
        model.addAttribute("recipes", recipes);
        model.addAttribute("title", cat.getCategoryName() + " recipes");
        return "recipe/list-under";
    }

    // delete each recipe
    @RequestMapping(value = "delete/{recipeId}", method = RequestMethod.POST)
    public String delete(@PathVariable int recipeId, Model model){
        Recipe recipe = recipeDao.findOne(recipeId);
        recipe.deleteIngredients(recipe.getIngredients());
        recipe.removeQuantities(recipe.getQuantities());
        recipeDao.delete(recipeId);
        model.addAttribute("message", "Recipe deleted successfully!");
        return "recipe/message";
    }

    //Edit a recipe
    @RequestMapping(value="edit/{recipeId}", method = RequestMethod.GET)
    public String displayEditRecipeForm(Model model, @PathVariable int recipeId){
        model.addAttribute(recipeDao.findOne(recipeId));
        model.addAttribute("title", "Edit " + recipeDao.findOne(recipeId).getRecipeName());
        ArrayList<Course> courses = new ArrayList<>();
        for (Course course : courseDao.findAll()){
            courses.add(course);
        }
        courses.sort(courseComparator);

        ArrayList<Category> categories = new ArrayList<>();
        for (Category category : categoryDao.findAll()){
            categories.add(category);
        }
        categories.sort(categoryComparator);

        model.addAttribute("courses", courses);
        model.addAttribute("categories", categories);
        return "recipe/edit";
    }

    @RequestMapping(value = "edit/{recipeId}", method = RequestMethod.POST)
    public String processEditForm(@PathVariable int recipeId, @RequestParam String recipeName,
                                  @RequestParam int courseId, @RequestParam int categoryId,
                                  @RequestParam int servingSize, @RequestParam String prepTime,
                                  @RequestParam String cookTime, Model model,
                                  @RequestParam String direction){
        Recipe edited = recipeDao.findOne(recipeId);
        edited.setRecipeName(recipeName);
        edited.setServingSize(servingSize);
        edited.setPrepTime(prepTime);
        edited.setCookTime(cookTime);
        edited.setDirection(direction);


        Course cor = courseDao.findOne(courseId);
        edited.setCourse(cor);

        Category cat = categoryDao.findOne(categoryId);
        edited.setCategory(cat);

        recipeDao.save(edited);

        model.addAttribute("message", "Successfully edited!");
        model.addAttribute("recipe", edited);
        model.addAttribute("title", "Ingredients needed for " + edited.getRecipeName());
        return "redirect:/recipe/view/" + edited.getId();
    }

    @RequestMapping(value = "add-rating/{recipeId}", method = RequestMethod.GET)
    public String displayAddRatingForm(@PathVariable int recipeId, Model model){
        String rating = "";
        String comment = "";
        Recipe recipe = recipeDao.findOne(recipeId);
        AddRateCommentToRecipeForm rateForm = new AddRateCommentToRecipeForm(recipe, rating, comment);
        model.addAttribute("title", "Rate and Comment "+recipe.getRecipeName());
        model.addAttribute("form", rateForm);
        return "recipe/add-rating";
    }

    @RequestMapping(value = "add-rating", method = RequestMethod.POST)
    public String processAddRatingForm(Model model, @ModelAttribute @Valid AddRateCommentToRecipeForm rateForm,
                                       Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("form", rateForm);
            return "recipe/add-rating";
        }
        Recipe recipe = recipeDao.findOne(rateForm.getRecipeId());
        RateComment rateComment = new RateComment(recipe, rateForm.getRating(), rateForm.getComment());
        recipe.setRateCommentList(rateComment);
        rateCommentDao.save(rateComment);
        recipeDao.save(recipe);
        return "redirect:view-ratingWithMsg/"+ recipe.getId();
    }

    @RequestMapping(value="view-ratingWithMsg/{id}", method = RequestMethod.GET)
    public String viewRatingWithSuccessMsg(@PathVariable int id, Model model){
        Recipe recipe = recipeDao.findOne(id);
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("recipe", recipe);
        model.addAttribute("message", "Added successfully!");
        return "recipe/view-ratingWithMsg";
    }

    @RequestMapping(value="view-rating/{id}", method = RequestMethod.GET)
    public String viewRating(@PathVariable int id, Model model){
        Recipe recipe = recipeDao.findOne(id);
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("recipe", recipe);
        return "recipe/view-rating";
    }
}



