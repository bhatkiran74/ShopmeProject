package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listAll(Model model) {

		List<User> listUser = userService.listAll();

		model.addAttribute("listUsers", listUser);

		return "users";
	}

	// New user route
	@GetMapping("/users/new")
	public String newUser(Model model) {

		User user = new User();
		user.setEnabled(true);
		List<Role> listRoles = userService.listRoles();

//		System.out.println(listRoles);

		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");

		return "user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

			user.setPhotos(fileName);
			User savedUser = userService.save(user);

			String uploadDir = "user-photos/" + savedUser.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			userService.save(user);

		}
		redirectAttributes.addFlashAttribute("message", "The user has been successfully saved...");

		return "redirect:/users";
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

		try {
			User user = userService.get(id);
			List<Role> listRoles = userService.listRoles();
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User (Id)" + id + ")");
			return "user_form";

		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {

			userService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user has been successfully deleted..." + id);

		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/users";

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes) {

		userService.updateEnabledstatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The User Id " + id + " has been " + status;

		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/users";

	}

}
