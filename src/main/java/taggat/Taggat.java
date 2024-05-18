package taggat;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Taggat {

    private static final Logger logger = Logger.getLogger(Taggat.class.getName());

    public static void main(String[] args) {
        // Set the FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        // Create and configure the JFrame
        JFrame frame = new JFrame("Taggat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set layout for the main frame
        frame.setLayout(new BorderLayout());

        // Left panel (30% of the width)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.3),
                Toolkit.getDefaultToolkit().getScreenSize().height));
        leftPanel.setLayout(new BorderLayout());

        // Logo at the top
        JLabel logoLabel = new JLabel(new ImageIcon("logo.png"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(logoLabel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Right panel (70% of the width)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY); // Placeholder for actual content
        rightPanel.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7),
                Toolkit.getDefaultToolkit().getScreenSize().height));

        // Locations tab
        JPanel locationsPanel = new JPanel();
        locationsPanel.setLayout(new BorderLayout());
        JPanel locationsTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel locationsLabel = new JLabel("Locations");
        JButton addLocationButton = new JButton("+");
        locationsTopPanel.add(locationsLabel);
        locationsTopPanel.add(addLocationButton);
        locationsPanel.add(locationsTopPanel, BorderLayout.NORTH);
        JPanel locationsContentPanel = new JPanel();
        locationsContentPanel.setLayout(new BoxLayout(locationsContentPanel, BoxLayout.Y_AXIS));
        JScrollPane locationsScrollPane = new JScrollPane(locationsContentPanel);
        locationsPanel.add(locationsScrollPane, BorderLayout.CENTER);

        addLocationButton.addActionListener(e -> {
            // Create a panel to hold the text field and checkbox
            JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // Create the text field
            JTextField locationField = new JTextField("C:\\Users\\ashton\\Desktop\\Taggat\\thispersondoesnotexist");
            locationField.setPreferredSize(new Dimension(200, locationField.getPreferredSize().height));

            // Create the checkbox
            JCheckBox checkBox = new JCheckBox();

            // Add ActionListener to the checkbox
            checkBox.addActionListener(evt -> {
                if (checkBox.isSelected()) {
                    // Load the image
                    ImageIcon icon = new ImageIcon(locationField.getText());

                    // Generate a thumbnail
                    Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon thumbnailIcon = new ImageIcon(image);

                    // Create a label to display the thumbnail
                    JLabel thumbnailLabel = new JLabel(thumbnailIcon);

                    // Add the thumbnail label to the right panel
                    rightPanel.add(thumbnailLabel);

                    // Refresh the layout
                    rightPanel.revalidate();
                    rightPanel.repaint();
                } else {
                    // Remove the thumbnail label from the right panel
                    Component[] components = rightPanel.getComponents();
                    for (Component component : components) {
                        if (component instanceof JLabel) {
                            rightPanel.remove(component);
                        }
                    }

                    // Refresh the layout
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            });

            // Add components to the panel
            locationPanel.add(checkBox);
            locationPanel.add(locationField);

            // Set the maximum width of the text field
            locationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, locationField.getPreferredSize().height));

            // Add the panel to the content panel
            locationsContentPanel.add(locationPanel);

            // Refresh the layout
            locationsContentPanel.revalidate();
            locationsContentPanel.repaint();
        });

        // Tags tab
        JPanel tagsPanel = new JPanel();
        tagsPanel.setLayout(new BorderLayout());
        JPanel tagsTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tagsLabel = new JLabel("Tags");
        JButton addTagButton = new JButton("+");
        tagsTopPanel.add(tagsLabel);
        tagsTopPanel.add(addTagButton);
        tagsPanel.add(tagsTopPanel, BorderLayout.NORTH);
        JPanel tagsContentPanel = new JPanel();
        tagsContentPanel.setLayout(new BoxLayout(tagsContentPanel, BoxLayout.Y_AXIS));
        JScrollPane tagsScrollPane = new JScrollPane(tagsContentPanel);
        tagsPanel.add(tagsScrollPane, BorderLayout.CENTER);
        addTagButton.addActionListener(e -> {
            JTextField tagField = new JTextField("New Tag");
            tagsContentPanel.add(tagField);
            tagsContentPanel.revalidate();
            tagsContentPanel.repaint();
        });

        // Settings tab
        JPanel settingsPanel = new JPanel(new BorderLayout());
        JPanel settingsTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel settingsLabel = new JLabel("Settings");
        settingsTopPanel.add(settingsLabel);
        settingsPanel.add(settingsTopPanel, BorderLayout.NORTH);

        // Add tabs to tabbedPane
        tabbedPane.addTab("Locations", locationsPanel);
        tabbedPane.addTab("Tags", tagsPanel);
        tabbedPane.addTab("Settings", settingsPanel);

        leftPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add panels to the main frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }
}
