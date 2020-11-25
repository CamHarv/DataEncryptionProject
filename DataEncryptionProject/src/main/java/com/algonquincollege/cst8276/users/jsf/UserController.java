package com.algonquincollege.cst8276.users.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.SessionMap;
import javax.faces.context.ExternalContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import com.algonquincollege.cst8276.users.dao.UserDao;
import com.algonquincollege.cst8276.users.model.UserPojo;

@Named
@SessionScoped
public class UserController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    @SessionMap
    protected Map<String, Object> sessionMap;
    
    @Inject
    protected UserDao userDao;
    
    protected List<UserPojo> users;

    @Inject
    protected ExternalContext externalContext;
    
    private void logMsg(String msg) {
        ((ServletContext)externalContext.getContext()).log(msg);
    }
    
    public List<UserPojo> getUsers() {
        return users;
    }
    
    public void setCustomers(List<UserPojo> users) {
        this.users = users;
    }
    
    public String navigateToAddForm() {
        sessionMap.put("newUser", new UserPojo());
        return "add-user?faces-redirect=true";
    }
    
    public String navigateToLoginForm() {
        sessionMap.put("newUser", new UserPojo());
        return "login-user?faces-redirect=true";
    }

    public String submitUser() {
        UserPojo user = (UserPojo) sessionMap.get("newUser");
        userDao.createUser(user);
        return "success-page?faces-redirect=true";
    }
    
    
}