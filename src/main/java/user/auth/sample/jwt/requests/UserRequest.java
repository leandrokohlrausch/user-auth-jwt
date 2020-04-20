package user.auth.sample.jwt.requests;

import java.util.List;

public class UserRequest {

    private String username;
    private String password;
    private String name;
    private String email;
    private List<String> authorities;

    public UserRequest() {
    }

    public UserRequest(String username, String password, String name, String email, List<String> authorities) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
