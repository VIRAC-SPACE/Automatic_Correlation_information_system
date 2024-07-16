package com.main.vlbi.forms;

import com.main.vlbi.models.Role;

import javax.validation.constraints.NotNull;

public class UserCreateForm {
    @NotNull
    private String email = "";

    @NotNull
    private String password = "";

    @NotNull
    private String name = "";

    @NotNull
    private String last_name = "";

    @NotNull
    private Role user_role = new Role();

    @NotNull
    private String role_string = "";

    public String getRole_string() {
        return role_string;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public Role getRole() {
        return user_role;
    }

    public void setRole(Role role) {
        this.user_role = role;
    }

}
