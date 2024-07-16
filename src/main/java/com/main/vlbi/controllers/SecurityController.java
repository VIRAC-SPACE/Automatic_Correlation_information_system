package com.main.vlbi.controllers;


import com.main.vlbi.config.security.SecurityUtils;
import com.main.vlbi.models.User;
import com.main.vlbi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SecurityController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/security/account", method = RequestMethod.POST)
    public @ResponseBody
    User getUserAccount() {
        Optional<User> user_tmp = userService.findUser(SecurityUtils.getCurrentLogin());
        User user = user_tmp.get();
        user.setPassword(null);
        return user;
    }
}
