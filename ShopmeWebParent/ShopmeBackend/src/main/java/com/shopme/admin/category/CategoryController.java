package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;


@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;

	@GetMapping("/categories")
	public String listAll(@Param("sortDir")String sortDir,Model model) {

		List<Category> listCategories = service.listAll(sortDir);
		
		
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		String reverseSortDir = sortDir.equals("asc")? "desc": "asc";
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		
		
		List<Category> listCategories = service.listCategoriesUsedInForm();
		
		
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		
		return "categories/category_form";
	}

	
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,@RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
		
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
			category.setImage(fileName);

			
			Category savedCategory = service.save(category);
			String uploadDir = "../category-images/"+savedCategory.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
			
		} else {
service.save(category);
		}
		
		
		
		
		
//		Category savedCatefory = service.save(category);
//		String uploadDir = "../category-image/"+ savedCatefory.getId();
//		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		
		redirectAttributes.addFlashAttribute("message", "The category has been successfully saved...");
		return "redirect:/categories";
	}
	
	
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id")Integer id,Model model, RedirectAttributes ra) {
		
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
		
			model.addAttribute("pageTitle", "Edit Category (Id: "+id+")");
			
			
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
