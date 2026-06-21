// [Module: Resident & Vehicle Management — Iman, Member 1]
public abstract class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = PasswordUtil.hash(password);
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordUtil.hash(password);
    }

    public String getRole() {
        return role;
    }

    public boolean authenticate(String inputPassword) {
        String hashed = PasswordUtil.hash(inputPassword);
        return this.password != null && this.password.equals(hashed);
    }
}