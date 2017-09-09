/**
 *
 */
package DuckHunt;

/**
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public Main() {
        JFrame frame = new JFrame();
        frame.setTitle("Shape Clicker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        initComponents(frame);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        //create frame and components on EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

    private void initComponents(JFrame frame) {
        frame.add(new DuckHuntPanel());
    }
}
