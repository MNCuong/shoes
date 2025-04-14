package com.example.shoes_store.Controller;

import com.example.shoes_store.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.shoes_store.Entity.Category;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String getAllCategories(Model model) {
        List<Category> categories = categoryService.getAll();
        log.info("categories: {}", categories.get(0).getName());
        model.addAttribute("categories", categories);
        return "redirect:/admin/home";
    }

    @GetMapping("/active")
    public String getAllActiveCategories(Model model) {
        List<Category> activeCategories = categoryService.getAllWithActive();
        model.addAttribute("categories", activeCategories);
        return "/category/list";
    }

    @GetMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "/category/add";
    }

    @PostMapping("/add")
    public String addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        log.info("category: {}", category.getName());
        return "redirect:/admin/home#about";
    }


    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return "redirect:/admin/home#about";
    }


    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/home#about";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleCategoryStatus(@PathVariable Long id) {
        categoryService.toggleCategoryStatus(id);
        return "redirect:/admin/home";
    }
}
