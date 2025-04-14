package com.example.shoes_store.Controller;

import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Service.CartItemService;
import com.example.shoes_store.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    CartItemService cartItemService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);

        if (user != null) {
            log.info("Đăng nhập thành công với người dùng: " + user.getUsername());
            if (user.getRole().equals("ADMIN")) {
                log.info("Vai trò ADMIN được xác nhận.");
                session.setAttribute("loggedInUser", user);
                return "redirect:/admin/home";
            } else {
                log.info("Vai trò người dùng: " + user.getRole());
                session.setAttribute("loggedInUser", user);
                return "redirect:/home";
            }
        }

        log.info("Đăng nhập thất bại");
        model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        return "/login";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", loggedInUser);
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        return "/user/home";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "/user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           @RequestParam String fullname,
                           @RequestParam String address,
                           Model model) {

        if (userService.userExists(username)) {
            model.addAttribute("error", "Tên người dùng đã tồn tại!");
            return "register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setFullname(fullname);
        newUser.setAddress(address);

        userService.registerUser(newUser);

        model.addAttribute("message", "Đăng ký thành công!");
        return "redirect:login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}


