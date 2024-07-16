package com.main.vlbi.services.implementations;

import com.main.vlbi.forms.UserCreateForm;
import com.main.vlbi.models.Role;
import com.main.vlbi.models.User;
import com.main.vlbi.repositorys.UserRepository;
import com.main.vlbi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service("UserService")
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailServiceImpl emailServiceImpl;

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void create(@Valid UserCreateForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.addRole(form.getRole());
        user.setName(form.getName());
        user.setLast_name(form.getLast_name());
        user.setIsNew(true);
        emailServiceImpl.send(form.getEmail(), "Your account is created",
                "Your new account has been created.\nPlease log in and change your password.");
        userRepository.save(user);
    }

    @Override
    public void Update(long id, String Email, String Password, String Name, String LastName, Role role) {
        Optional<User> user_tmp = getUserById(id);
        User user = user_tmp.get();
        user.setEmail(Email);
        user.setName(Name);
        user.setLast_name(LastName);
        user.setPassword(new BCryptPasswordEncoder().encode(Password));
        user.addRole(role);
        emailServiceImpl.send(Email, "Your account has been updated",
                "Your account has been updated by admin.");
        userRepository.save(user);
        //return userRepository.save(user);
    }

    @Override
    public void Delete(long id) {
        Optional<User> User = getUserById(id);
        User user = User.get();
        emailServiceImpl.send(user.getEmail(), "Your account has been deleted",
                "Your account has been deleted");
        userRepository.delete(user);
    }

    @Override
    public void ChangePassword(String Email, String newPassword) {
        Optional<User> user_tmp = getUserByEmail(Email);
        User user = user_tmp.get();
        user.setIsNew(false);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        emailServiceImpl.send(Email, "Your password has been changed",
                "You have successfully changed your password.");
        userRepository.save(user);
    }


    @Override
    public void resetPassword(String Email) {
        Optional<User> User = getUserByEmail(Email);
        User user = User.get();

        String newPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        user.setIsNew(true);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        emailServiceImpl.send(Email, "Your password has been changed",
                "You have successfully changed your password." + "\nYour new password is:\t" + newPassword);
        userRepository.save(user);
    }

	@Override
	public ArrayList<String> getOnlyAdminEmails() {
		return userRepository.findByRolesName("admin");
	}

}
