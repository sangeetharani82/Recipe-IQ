package org.launchcode.projectRMS.Controllers;


import org.launchcode.projectRMS.models.*;
import org.launchcode.projectRMS.models.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    CategoryDao categoryDao;

    @Autowired
    UserDao userDao;

    // Request path: /recipe
    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("recipes", recipeDao.findAll());
        model.addAttribute("title", "All recipes");
        return "recipe/index";
    }

    // add/create a recipe
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddRecipeForm(Model model) {
        model.addAttribute("title", "Add recipes");
        model.addAttribute(new Recipe());
        model.addAttribute("courses", courseDao.findAll());
        model.addAttribute("categories", categoryDao.findAll());
        return "recipe/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddRecipeForm(Model model, @ModelAttribute @Valid Recipe newRecipe,
                                       Errors errors, @RequestParam int courseId, @RequestParam int categoryId) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add a recipe");
            model.addAttribute("courses", courseDao.findAll());
            model.addAttribute("categories", categoryDao.findAll());
            return "recipe/add";
        }
//        int id = loggedInUser.getId();
//        newRecipe.setUser(userDao.findOne(id));
        Course cor = courseDao.findOne(courseId);
        Category cat = categoryDao.findOne(categoryId);
        newRecipe.setCourse(cor);
        newRecipe.setCategory(cat);
        recipeDao.save(newRecipe);

        model.addAttribute("message", "Recipe added successfully!");
        return "addIngredients/view";
        // return "redirect:single/"+newRecipe.getId();
    }

    //view single recipe
    @RequestMapping(value="single/{id}", method = RequestMethod.GET)
    public String singleRecipe(@PathVariable int id, Model model){
        Recipe recipe = recipeDao.findOne(id);
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("course", recipe.getCourse());
        model.addAttribute("category", recipe.getCategory());
        model.addAttribute("recipe", recipe);
        List<AddIngredientsToRecipe> lists = recipe.getAddIngredientsToRecipes();
        model.addAttribute("title", recipe.getRecipeName());
        model.addAttribute("ingredientLists", lists);
        return "recipe/single";
    }

    // delete each recipe
    @RequestMapping(value = "delete/{recipeId}")
    public String delete(@PathVariable int recipeId, Model model){
        recipeDao.delete(recipeId);
        model.addAttribute("message", "Recipe deleted successfully!");
        //return "redirect:/recipe";
        return "recipe/message";
    }

    //Edit a recipe
    @RequestMapping(value="edit/{recipeId}", method = RequestMethod.GET)
    public String displayEditRecipeForm(Model model, @PathVariable int recipeId){
        model.addAttribute(recipeDao.findOne(recipeId));
        model.addAttribute("title", "Edit " + recipeDao.findOne(recipeId).getRecipeName());
        model.addAttribute("courses", courseDao.findAll());
        model.addAttribute("categories", categoryDao.findAll());
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

        model.addAttribute("message", "Recipe edited and saved successfully!");

        List<AddIngredientsToRecipe> lists = edited.getAddIngredientsToRecipes();
        model.addAttribute("title", "Ingredients needed for " + edited.getRecipeName());
        model.addAttribute("ingredientLists", lists);

        return "addIngredients/view";
    }

    //recipes in a course
    @RequestMapping(value = "course", method = RequestMethod.GET)
    public String course(Model model, @RequestParam int id){
        Course cor = courseDao.findOne(id);
        List<Recipe> recipes = cor.getRecipes();
        model.addAttribute("recipes", recipes);
        model.addAttribute("title", "Recipes in Course "+ cor.getCourseName());
        return "recipe/index";
    }
    //recipes in a category
    @RequestMapping(value = "category", method = RequestMethod.GET)
    public String category(Model model, @RequestParam int id){
        Category cat = categoryDao.findOne(id);
        List<Recipe> recipes = cat.getRecipes();
        model.addAttribute("recipes", recipes);
        model.addAttribute("title", "Recipes in Category" + cat.getCategoryName());
        return "recipe/index";
    }

//    @RequestMapping(value = "overAll", method = RequestMethod.GET)
//    public String overAllRating(Model model){
//        ArrayList<Integer> ratings = new ArrayList<>();
//        int running_total = 0;
//        double averageRating;
//        for (Recipe recipe : recipeDao.findAll()){
//            int id = recipe.getId();
//            for (RateComment rate : recipeDao.findOne(id).getRateCommentList()){
//                ratings.add(rate.getRating());
//            }
//        }
//        for (int i : ratings){
//            running_total = running_total + i;
//        }
//        averageRating = running_total / ratings.size();
//        model.addAttribute("average", averageRating);
//        return "recipe/index";
//    }

//    //delete a recipe
//    @RequestMapping(value="remove", method = RequestMethod.GET)
//    public String displayRemoveRecipeForm(Model model){
//        model.addAttribute("recipes", recipeDao.findAll());
//        model.addAttribute("title", "Delete recipe(s)");
//        return "recipe/remove";
//    }
//
//    @RequestMapping(value="remove", method = RequestMethod.POST)
//    public String processRemoveRecipeForm(@RequestParam(defaultValue = "-1") int[] recipeIds, Model model){
//        for (int recipeId : recipeIds){
//            if (recipeId != -1) {
//                recipeDao.delete(recipeId);
//                return "redirect:";
//            }
//        }
//        model.addAttribute("recipes", recipeDao.findAll());
//        model.addAttribute("title", "Delete recipe(s)");
//        return "recipe/remove";
//    }
}


