// [Module: Resident & Vehicle Management — Iman, Member 1]
public abstract class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public boolean authenticate(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }
}