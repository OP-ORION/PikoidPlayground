import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class BoidFrame extends JFrame {
    public BoidFrame(int w, int h, int c) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(w, h));
        //setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        setVisible(true);

        addBoidPanel(c);
        addSettingsPanel();
    }

    private void addSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setPreferredSize(new Dimension(250, getHeight()));
        settingsPanel.setBackground(new Color(70, 70, 75));

        /*
        //WHY IS NUMBERS ONLY INPUT SO HARDDD
        JSpinner viewRangeSpinner = new JSpinner();
        viewRangeSpinner.setValue(BoidSettings.VIEW_RANGE);
        viewRangeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner source = (JSpinner)e.getSource();
                BoidSettings.VIEW_RANGE = (int) source.getValue();
            }
        });
        viewRangeSpinner.setPreferredSize(new Dimension(100,25));
        settingsPanel.add(viewRangeSpinner);
         */
        JTextPane wIPBanner = new JTextPane();
        wIPBanner.setText("STILL ADDING SETTINGS");
        wIPBanner.setEditable(false);
        settingsPanel.add(wIPBanner);

        add(settingsPanel, BorderLayout.EAST);
    }

    private void addBoidPanel(int boidCount) {
        BoidPanel boidPanel = new BoidPanel(boidCount);
        add(boidPanel);
    }
}