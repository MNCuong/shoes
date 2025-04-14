package com.example.shoes_store.Controller;

import com.example.shoes_store.Entity.Category;
import com.example.shoes_store.Entity.Product;
import com.example.shoes_store.Entity.User;
import com.example.shoes_store.Entity.Order;
import com.example.shoes_store.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/admin")
    public String getAllUsers(Model model) {
        List<User> Users = userService.getAllUsers();
        model.addAttribute("users", Users);
        return "admin/users";
    }

    @GetMapping("/admin/home")
    public String homeAdminPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            log.info("Không tìm thấy người dùng đăng nhập trong session. Chuyển hướng về trang đăng nhập.");
            return "redirect:/login";
        }

        if (!loggedInUser.getRole().equals("ADMIN")) {
            log.info("Người dùng không phải là ADMIN. Chuyển hướng về trang chính.");
            return "redirect:/home";
        }

        List<Category> categories = categoryService.getAll();
        List<Category> categoriesActive = categoryService.getAllWithActive();
        List<Product> products = productService.getAllProducts();
        List<Order> orders = orderService.getAllOrders();
        List<User> users = userService.getAllUsersByRole("CUSTOMER");

        int totalOrders = users.stream()
                .mapToInt(customer -> customer.getOrders().size())
                .sum();
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        List<Integer> years = new ArrayList<>();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            years.add(y);
        }

        model.addAttribute("years", years);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("categories", categories);
        model.addAttribute("categoriesActive", categoriesActive);
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        model.addAttribute("users", users);
        log.info("Người dùng ADMIN: " + loggedInUser.getUsername());
        model.addAttribute("user", loggedInUser);
        return "/admin/index";
    }


    @GetMapping("/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        User User = userService.getUserById(id);
        if (User != null) {
            List<Order> orders = userService.getOrdersByUserId(id);
            model.addAttribute("user", User);
            model.addAttribute("orders", orders);
        }
        return "admin/user-details";
    }

    @GetMapping("/about")
    public String aboutPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("user", loggedInUser);
        return "/user/about";
    }
    @GetMapping("/update-use")
    public String updateUserInfoPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", loggedInUser);
        return "/user/updateUserInfo";
    }

    @GetMapping("/contact")
    public String contactPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("user", loggedInUser);
        return "/user/contact";
    }

    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("cart",cartService.getCartByUserId(loggedInUser.getId()));
        model.addAttribute("user", loggedInUser);
        return "/user/cart";
    }
    @GetMapping("/order")
    public String orderPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("orders",orderService.getOrderByUser(loggedInUser));
        model.addAttribute("user", loggedInUser);
        return "/user/order";
    }
    @GetMapping("/shop")
    public String shopPage(HttpSession session, Model model) {
        List<Category> categoriesActive = categoryService.getAllWithActive();
        List<Product> products = productService.getAllProductsWithCateActive();
        model.addAttribute("categories", categoriesActive);
        model.addAttribute("products", products);

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", loggedInUser);
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);

        return "/user/shop";
    }

    @GetMapping("/shop-single")
    public String shopSinglePage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int cartItemQuantity=cartItemService.getQuantity(loggedInUser);
        log.info("cartItemQuantity {}", cartItemQuantity);
        model.addAttribute("cartItemQuantity", cartItemQuantity);
        model.addAttribute("user", loggedInUser);
        return "/user/shop-single";
    }

    @PostMapping("/user/update")
    public String updateUserInfo(@ModelAttribute User updatedUser,
                                 HttpSession session) {
        userService.updateUser(updatedUser);
        session.setAttribute("loggedInUser", updatedUser);

        return "redirect:/update-use";
    }

}
