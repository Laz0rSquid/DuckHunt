/**
 *
 */
package DuckHunt;

/**
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main  extends JFrame{
    public static DuckHuntPanel game;

    public Main() {
        setTitle("Duck Hunt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents(this);

        pack();
        setVisible(true);
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
        game = new DuckHuntPanel();
        frame.add(game);
    }
}
