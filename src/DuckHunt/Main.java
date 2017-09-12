/**
 *
 */
package DuckHunt;

/**
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main extends JFrame implements LossInterface {

    public DuckHuntPanel game;

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
        game = new DuckHuntPanel(this);
        frame.add(game);
    }

    @Override
    public void onLossAction() {
        JOptionPane.showMessageDialog(this,
                "You reached "
                        + game.getScore()
                        + " points."
                        + System.lineSeparator()
                        + "Congratulation!",
                "END GAME",
                JOptionPane.INFORMATION_MESSAGE);
        game.resetAttributes(this);
    }
}
