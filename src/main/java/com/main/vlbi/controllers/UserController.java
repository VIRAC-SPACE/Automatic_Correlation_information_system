package com.main.vlbi.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.vlbi.forms.UserCreateForm;
import com.main.vlbi.models.Role;
import com.main.vlbi.models.User;
import com.main.vlbi.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Optional;

@RestController
@Controller
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @RequestMapping(method = RequestMethod.GET, path = "/all")
    public @ResponseBody
    Iterable<User> getAllUser() {
        return userServiceImpl.getAllUsers();
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete")
    public @ResponseBody
    void deleteUser(@RequestParam Long id) {
        userServiceImpl.Delete(id);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/usersemail")
    public @ResponseBody
    Optional<User> getUserByEmail(@RequestParam String email) {
        return userServiceImpl.getUserByEmail(email);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/usersid")
    public @ResponseBody
    Optional<User> getUserById(@RequestParam Long id) {
        return userServiceImpl.getUserById(id);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/create")
    public @ResponseBody
    void createUser(@RequestBody String requestString) {
        Role role;
        Gson gson = new Gson();
        Type type = new TypeToken<UserCreateForm>() {
        }.getType();
        UserCreateForm form = gson.fromJson(requestString, type);
        role = new Role(form.getRole_string());
        form.setRole(role);
        userServiceImpl.create(form);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/update")
    public @ResponseBody
    void updateUser(@RequestBody String requestString) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserCreateForm>() {
        }.getType();
        UserCreateForm form = gson.fromJson(requestString, type);
        Role role = new Role(form.getRole_string());
        form.setRole(role);
        Optional<User> user_tmp = getUserByEmail(form.getEmail());
        User user = user_tmp.get();
        Long id = user.getId();
        userServiceImpl.Update(id, form.getEmail(), form.getPassword(), form.getName(), form.getLast_name(), role);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/changepassword")
    public @ResponseBody
    void ChangePassword(@RequestBody String requestString) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserCreateForm>() {
        }.getType();
        UserCreateForm form = gson.fromJson(requestString, type);
        userServiceImpl.ChangePassword(form.getEmail(), form.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/forgetyourpassword")
    public @ResponseBody
    void resetPassword(@RequestBody String requestString) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserCreateForm>() {
        }.getType();
        UserCreateForm form = gson.fromJson(requestString, type);
        userServiceImpl.resetPassword(form.getEmail());
    }
}
