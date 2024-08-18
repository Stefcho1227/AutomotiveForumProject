package com.example.forumproject.controllers.mvc;

import com.example.forumproject.models.User;
import com.example.forumproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class AdminMvcController {
    private final UserService userService;

    @Autowired
    public AdminMvcController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("users")
    public List<User> populateUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/admin")
    public String getAdminPanelView(Model model,
                                    @RequestParam(value = "firstName", defaultValue = "") String name,
                                    @RequestParam(value = "email", defaultValue = "") String email,
                                    @RequestParam(value = "username", defaultValue = "") String username) {
        if (model.getAttribute("isAdmin") != null) {
            if ((boolean) model.getAttribute("isAdmin")) {
                List<User> users = userService.getAllUsers(name, email, username);
                model.addAttribute("users", users);
                return "AdminPanelView";
            }
        }
        return "redirect:/";
    }


    @GetMapping("/admin/block/{id}")
    public String handeUserBlock(@PathVariable int id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("currentUser");
        User userBlockInfo = new User();
        userBlockInfo.setIsBlocked(true);
        userService.updateUserBlockStatus(loggedInUser, userBlockInfo, id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/unblock/{id}")
    public String handeUserUnblock(@PathVariable int id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("currentUser");
        User userBlockInfo = new User();
        userBlockInfo.setIsBlocked(false);
        userService.updateUserBlockStatus(loggedInUser, userBlockInfo, id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/addprivileges/{id}")
    public String handeUserGivePrivileges(@PathVariable int id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("currentUser");
        if (!loggedInUser.getRole().getRoleName().equals("admin")) {
            return "redirect:/";
        }
        userService.userAddPrivileges(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/removeprivileges/{id}")
    public String handeUserRemovePrivileges(@PathVariable int id, HttpSession session) {
        userService.userRemovePrivileges(id);
        return "redirect:/admin";
    }
}
