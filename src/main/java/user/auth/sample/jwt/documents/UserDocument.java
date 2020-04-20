package user.auth.sample.jwt.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;
import user.auth.sample.jwt.requests.UserRequest;

import java.util.List;

@Document(collection = "users")
public class UserDocument implements UserDetails, CredentialsContainer  {

    @Id
    private String id;
    private String username;
    private String password;
    private String name;
    private String email;
    @DBRef
    private List<? extends RoleDocument> authorities;

    public UserDocument() {

    }

    public UserDocument(String username, String password, List<? extends RoleDocument> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDocument(UserRequest userRequest) {
        this.setRequestProperties(userRequest);
    }

    public void setRequestProperties(UserRequest userRequest) {
        if (userRequest.getUsername() != null) this.username = userRequest.getUsername();
        if (userRequest.getPassword() != null) this.password = userRequest.getPassword();
        if (userRequest.getEmail() != null) this.email = userRequest.getEmail();
        if (userRequest.getName() != null) this.name = userRequest.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
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

    @Override
    public List<? extends RoleDocument> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<? extends RoleDocument> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = "";
    }
}
