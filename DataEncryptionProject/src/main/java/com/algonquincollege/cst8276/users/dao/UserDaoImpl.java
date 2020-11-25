package com.algonquincollege.cst8276.users.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.algonquincollege.cst8276.users.model.UserPojo;

/**
* Description: Implements the C-R-U-D API for the database
*/
@Named
@ApplicationScoped
public class UserDaoImpl implements UserDao, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USER_DS_JNDI =
        "java:app/jdbc/users";
    private static final String INSERT_USER =
        "insert into users (ID, PASSWORD) values(?, ?)";

    
    @Inject
    protected ExternalContext externalContext;
    private void logMsg(String msg) {
        ((ServletContext)externalContext.getContext()).log(msg);
    }
    
    @Resource(lookup = USER_DS_JNDI)
    protected DataSource userDS;
    
    protected Connection conn;    
    protected PreparedStatement createPstmt;
    
    @PostConstruct
    protected void buildConnectionAndStatements() {
        try {
            logMsg("building connection and stmts");
                        
            if (conn == null) {
                conn = userDS.getConnection();
            }
        }
        catch (Exception e) {
            logMsg("something went wrong getting connection from database: " + e.getLocalizedMessage());
        }
        if (conn != null) {
            logMsg("connection success");
            try {
                createPstmt = conn.prepareStatement(INSERT_USER, RETURN_GENERATED_KEYS);
            }catch (Exception e) {
                logMsg("something went wrong preparing createPstmt: " + e.getLocalizedMessage());
            }            
        }
    }
    
    @PreDestroy
    protected void closeConnectionAndStatements() {
        try {
            logMsg("closing stmts and connection");
            createPstmt.close();
            conn.close();
        }
        catch (Exception e) {
            logMsg("something went wrong closing stmts or connection: " + e.getLocalizedMessage());
        }
    }
    
       
    @Override
    public UserPojo createUser(UserPojo user) {
        try {
            System.out.println("user "+user);
            createPstmt.setString(1, user.getId());
            
            //TODO - Implement Encryption Part
            EncryptionAES aes = new EncryptionAES();
            String encrpyted = aes.encrypt(user.getPassword(), user.getId());
            System.out.print("encrpyted: "+encrpyted);
            
            createPstmt.setString(2, encrpyted);
            createPstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return user;
    }
    
   
}