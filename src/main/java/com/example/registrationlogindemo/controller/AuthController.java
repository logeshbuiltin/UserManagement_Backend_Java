package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.EmailDetails;
import com.example.registrationlogindemo.service.EmailService;
import com.example.registrationlogindemo.service.OtpGeneratorService;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    int otpLength = 4;

    private OtpGeneratorService otpGenerator = new OtpGeneratorService();

    @Autowired
    private EmailService emailService;

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * @param model
     * @return
     */
    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    /**
     * @param user
     * @param result
     * @param model
     * @return
     */
    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    /**
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    /**
     * @param userEmail
     * @return
     */
    @PutMapping("/generateOtp")
    public String generatePasswordOtp(@RequestParam("userEmail") String userEmail) {
        User existing = userService.findByEmail(userEmail);
        if(existing != null) {
            existing.setOtpNo(otpGenerator.OTP(otpLength));
            userService.updateUser(existing);
            EmailDetails details = new EmailDetails();
            details.setRecipient(existing.getEmail());
            details.setSubject("Password reset OTP");
            details.setMsgBody("OTP to change your password: " + existing.getOtpNo());
            String status = emailService.sendSimpleMail(details);
            return "Success: opt generated" + status;
        } else {
            return "Error: opt not generated";
        }
    }

    @GetMapping("/validateemailotp")
    public String validateOtp(@RequestParam(required = false) String userEmail, @RequestParam("userOtp") String userOtp) {
        User existing = userService.findByEmail(userEmail);
        if(existing != null) {
            if(userOtp.equals(existing.getOtpNo())) {
                return "Valid";
            } else {
                return "Invalid";
            }
        } else {
            return "Invalid user email";
        }
    }
}
