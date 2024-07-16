package com.main.vlbi.services;

import com.main.vlbi.forms.UserCreateForm;
import com.main.vlbi.models.Role;
import com.main.vlbi.models.User;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Optional;


public interface UserService {
    Optional<User> findUser(String email);
    Optional<User> getUserById(long id);
    Optional<User> getUserByEmail(String email);
    Iterable<User> getAllUsers();
    void create(@Valid UserCreateForm form);
    void Update(long id, String Email, String Password, String Name, String LastName, Role role);
    void Delete(long id);
    void ChangePassword(String email, String newPassword);
    void resetPassword(String email);
    ArrayList<String> getOnlyAdminEmails();
}

