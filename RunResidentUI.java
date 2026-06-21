import Exception.*;

public class RunResidentUI {
    public static void main(String[] args) {
        ResidentManager manager = new ResidentManager();
        ResidentMenuUI ui = new ResidentMenuUI(manager);
        ui.start();
    }
}
