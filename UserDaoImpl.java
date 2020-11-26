package com.algonquincollege.cst8276.users.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        "java:app/jdbc/customers";
    private static final String INSERT_USER =
        "insert into user (ID, PASSWORD) values(?, ?)";
    
    private static final String READ_ALL =
            "SELECT * FROM USER";

    
    @Inject
    protected ExternalContext externalContext;
    private void logMsg(String msg) {
        ((ServletContext)externalContext.getContext()).log(msg);
    }
    
    @Resource(lookup = USER_DS_JNDI)
    protected DataSource userDS;
    
    protected Connection conn;    
    protected PreparedStatement readAllPstmt;
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
        
        try {
            readAllPstmt = conn.prepareStatement(READ_ALL);
        }
        catch (Exception e) {
            logMsg("something went wrong preparing read all: " + e.getLocalizedMessage());
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
        
        try {
            readAllPstmt = conn.prepareStatement(READ_ALL);
        }
        catch (Exception e) {
            logMsg("something went wrong preparing read all: " + e.getLocalizedMessage());
        }
        
        
        
    }
    
    @Override
    public List<UserPojo> readAllUsers() {
        logMsg("reading all users");
        List<UserPojo> users = new ArrayList<>();
        try {
            ResultSet rs = readAllPstmt.executeQuery();
            while (rs.next()) {
                UserPojo newUser = new UserPojo();
                newUser.setId(rs.getString("ID"));
                newUser.setPassword(rs.getString("PASSWORD"));
                //encrypt
             
                users.add(newUser);
            }
            try {
                rs.close();
            }
            catch (Exception e) {
                logMsg("something went wrong closing resultSet: " + e.getLocalizedMessage());
            }
        }
        catch (SQLException e) {
            logMsg("something went wrong accessing database: " + e.getLocalizedMessage());
        }
        return users;
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
    
    public List<UserPojo> decryptUser() {
        logMsg("reading all users");
        List<UserPojo> users = new ArrayList<>();
        EncryptionAES aes = new EncryptionAES();
        try {
            ResultSet rs = readAllPstmt.executeQuery();
            while (rs.next()) {
                UserPojo newUser = new UserPojo();
                newUser.setId(rs.getString("ID"));
                newUser.setPassword(rs.getString("PASSWORD"));
                //decrypt
                String decrpyted = aes.decrypt(newUser.getPassword(), newUser.getId());
                newUser.setPassword(decrpyted);
                users.add(newUser);
            }
            try {
                rs.close();
            }
            catch (Exception e) {
                logMsg("something went wrong closing resultSet: " + e.getLocalizedMessage());
            }
        }
        catch (SQLException e) {
            logMsg("something went wrong accessing database: " + e.getLocalizedMessage());
        }
        return users; 
        
       
        
        
    }
    
    
    
   
}