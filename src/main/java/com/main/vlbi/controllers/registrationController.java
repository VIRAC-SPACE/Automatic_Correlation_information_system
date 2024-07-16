package com.main.vlbi.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.vlbi.forms.UserCreateForm;
import com.main.vlbi.services.implementations.EmailServiceImpl;
import com.main.vlbi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

@RestController
@Controller
@RequestMapping(path = "/registration")
public class registrationController {

    @Autowired
    EmailServiceImpl emailServiceImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @RequestMapping(method = RequestMethod.POST, path = "/senduserdatatoadmis")
    public @ResponseBody
    void getRegistrationData(@RequestBody String requestString) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserCreateForm>() {
        }.getType();
        UserCreateForm form = gson.fromJson(requestString, type);
        String name = form.getName();
        String lastName = form.getLast_name();
        String passWord = form.getPassword();
        String role = form.getRole_string();
        String email = form.getEmail();
        ArrayList<String> adminsEmails = userServiceImpl.getOnlyAdminEmails();

        String Subject = "User registration";
        String Text = "Dear Admin! \n\n" + "Please register this user with these data \n" + "Name\t" + name + "\n" + "Last Name\t" + lastName + "\n" + "Password\t" + passWord + "\n" + "Role\t" + role + "\nEmail\t" + email + "\n";
        emailServiceImpl.send("janis.steinbergs@venta.lv", Subject, Text);
        adminsEmails
                .parallelStream()
                .forEach(u -> emailServiceImpl.send(u, Subject, Text));
    }
}
