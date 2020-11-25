package com.algonquincollege.cst8276.users.model;

import java.io.Serializable;
import javax.faces.view.ViewScoped;


@ViewScoped
public class UserPojo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String password;    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }   

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserPojo)) {
            return false;
        }
        UserPojo other = (UserPojo) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("User [id=")
            .append(id)
            .append(", ");
        if (id != null) {
            builder
                .append("id=")
                .append(id)
                .append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

}