package org.launchcode.projectRMS.Controllers;

import org.launchcode.projectRMS.Comparators.CourseComparator;
import org.launchcode.projectRMS.models.Course;
import org.launchcode.projectRMS.models.data.CourseDao;
import org.launchcode.projectRMS.models.data.RecipeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("course")
public class CourseController {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private RecipeDao recipeDao;

    CourseComparator courseComparator = new CourseComparator();

    @RequestMapping(value="")
    public String index(Model model){
        ArrayList<Course> lists = new ArrayList<>();
        for (Course course : courseDao.findAll()){
            lists.add(course);
        }
        lists.sort(courseComparator);
        model.addAttribute("title", "Courses");
        model.addAttribute("courses", lists);
        return "course/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String displayAddForm(Model model){
        model.addAttribute("title", "Add Course");
        model.addAttribute("course", new Course());
        return "course/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String processAddForm(Model model, @ModelAttribute @Valid Course course, Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("title", "Add Course");
            return "course/add";
        }
        courseDao.save(course);

        model.addAttribute("message", "Successfully added!");
        return "course/message";
        //return "redirect:";
    }

    // delete each course
    @RequestMapping(value = "delete/{courseId}")
    public String delete(@PathVariable int courseId, Model model){
        courseDao.delete(courseId);
        model.addAttribute("message", "Successfully deleted!");
        return "course/message";
        //return "redirect:/course";
    }
}
