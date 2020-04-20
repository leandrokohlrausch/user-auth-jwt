package user.auth.sample.jwt.responses;

import user.auth.sample.jwt.documents.UserDocument;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {

    private String id;
    private String username;
    private String name;
    private String email;
    private List<String> authorities;

    public UserResponse(UserDocument userDocument) {
        this.id = userDocument.getId();
        this.username = userDocument.getUsername();
        this.name = userDocument.getName();
        this.email = userDocument.getEmail();
        this.authorities = userDocument.getAuthorities().stream().map(roleDocument -> roleDocument.getAuthority()).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
