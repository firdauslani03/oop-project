// [Module: Resident & Vehicle Management — Iman, Member 1]
public class TestAuth {
    public static void main(String[] args) {
        ResidentManager manager = new ResidentManager();
        String id = "T001";
        String name = "Tester";
        String password = "pass123";
        String ic = "123456789012";
        String phone = "0123456789";

        try {
            manager.registerResident(id, name, password, ic, phone);
            System.out.println("Register succeeded for " + id);
        } catch (Exception e) {
            System.out.println("Register failed: " + e.getMessage());
        }

        try {
            Resident r = manager.login(id, password);
            System.out.println("Login succeeded for: " + r.getName());
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        try {
            manager.login(id, "wrongpass");
            System.out.println("Login with wrong password unexpectedly succeeded");
        } catch (Exception e) {
            System.out.println("Login with wrong password failed as expected: " + e.getMessage());
        }
    }
}
