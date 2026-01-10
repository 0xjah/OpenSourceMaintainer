package models;

public class User {

    public enum Role {
        ADMIN, MAINTAINER
    }

    private int id;
    private String name;
    private Role role;
    private String pwd;
    private String usrName;
    private String email;

    public User(String name, Role role, String pwd, String usrName) {
        this.name = name;
        this.role = role;
        this.pwd = pwd;
        this.usrName = usrName;
    }

    public User(int id, String name, Role role, String pwd, String usrName) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.pwd = pwd;
        this.usrName = usrName;
    }

    public User(int id, String name, Role role, String pwd, String usrName, String email) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.pwd = pwd;
        this.usrName = usrName;
        this.email = email;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return pwd;
    }

    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    public String getUsername() {
        return usrName;
    }

    public void setUsername(String usrName) {
        this.usrName = usrName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}
