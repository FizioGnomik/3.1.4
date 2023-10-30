package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/")
    public String getAdminPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/admin_page";
    }

    @GetMapping("/add")
    public String newUserPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/new_user";
    }
    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user, BindingResult bindingResult,
                             @RequestParam(value = "roles") String[] selectResult) {
        if (bindingResult.hasErrors()) {
            return "admin/new_user";
        } else {
            for (String s : selectResult) {
                user.setRoles(Collections.singleton(roleService.getRole(s)));
            }
            userService.saveUser(user);
            return "redirect:/admin/";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/";
    }

    @PatchMapping("/edit/{id}")
    public String updateUser(@ModelAttribute("user") User user, BindingResult bindingResult,
                             @RequestParam(value = "roles") String[] selectResult) {
        if (!bindingResult.hasErrors()) {
            for (String s : selectResult) {
                user.setRoles(Collections.singleton(roleService.getRole(s)));
            }
            userService.updateUser(user);
        }
        return "redirect:/admin/";
    }
}