package view;

/**
 * aplicação RoomBookings.
 * responsável por configurar o ambiente inicial 
 *
 * @author Pedro 
 */
public class Main {
    public static void main(String[] args) {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}