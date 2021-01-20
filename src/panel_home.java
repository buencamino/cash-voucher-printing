import javax.swing.*;
import java.awt.*;

public class panel_home extends JPanel {
    JLabel lbl_background;

    public panel_home()
    {
        setLayout(new BorderLayout());

        lbl_background = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("voucherbg.jpg")));

        add(lbl_background, BorderLayout.CENTER);
    }
}
