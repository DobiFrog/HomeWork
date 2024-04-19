package needToTest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authorization {

    @JsonProperty("password")
    private String password;

    @JsonProperty("username")
    private String username;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}