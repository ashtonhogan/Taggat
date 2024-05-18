package taggat;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Taggat {

    private static final Logger logger = Logger.getLogger(Taggat.class.getName());
    private static JPanel rightPanel;
    private static JPanel tagsContentPanel;
    private static String selectedTag = null;
    private static Color selectedTagColor = null;

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
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE); // Change background color to white
        rightPanel.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7),
                Toolkit.getDefaultToolkit().getScreenSize().height));
        rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout to FlowLayout.LEFT

        // Wrap the right panel in a JScrollPane
        JScrollPane rightScrollPane = new JScrollPane(rightPanel);
        rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrolling

        // Locations tab
        JPanel locationsPanel = new JPanel();
        locationsPanel.setLayout(new BorderLayout());
        JPanel locationsTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel locationsLabel = new JLabel("Create");
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
            JPanel locationPanel = new JPanel();
            locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.X_AXIS));
            locationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

            // Create the checkbox
            JCheckBox checkBox = new JCheckBox();
            checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // Add padding to the right

            // Create the text field
            JTextField locationField = new JTextField("C:\\Users\\ashton\\Desktop\\Taggat\\thispersondoesnotexist");
            locationField.setPreferredSize(new Dimension(300, locationField.getPreferredSize().height)); // Wider text field
            locationField.setMaximumSize(new Dimension(500, locationField.getPreferredSize().height)); // Limit maximum width
            locationField.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Add ActionListener to the checkbox
            checkBox.addActionListener(evt -> {
                if (checkBox.isSelected()) {
                    // Run the image processing in a background thread
                    new SwingWorker<Void, JLabel>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Clear the right panel before adding new thumbnails
                            SwingUtilities.invokeLater(() -> rightPanel.removeAll());

                            // Print filenames to console and add thumbnails
                            String imagePath = locationField.getText();
                            File directory = new File(imagePath);
                            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif"));
                            if (files != null) {
                                for (File file : files) {
                                    System.out.println("Found image: " + file.getName());

                                    // Generate a thumbnail for each image
                                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                                    Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                    ImageIcon thumbnailIcon = new ImageIcon(image);

                                    // Create a label to display the thumbnail
                                    JLabel thumbnailLabel = new JLabel(thumbnailIcon);

                                    // Add a mouse listener to each thumbnail
                                    thumbnailLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                            // Handle the click event for the thumbnail here
                                            // For example, you can highlight the thumbnail or perform any action
                                            if (selectedTag != null) {
                                                thumbnailLabel.setBorder(BorderFactory.createLineBorder(selectedTagColor, 2));
                                            }
                                        }
                                    });

                                    // Publish the thumbnail label to the UI thread
                                    publish(thumbnailLabel);
                                }
                            } else {
                                System.out.println("No images found in the specified directory.");
                            }
                            return null;
                        }

                        @Override
                        protected void process(List<JLabel> chunks) {
                            // Add the thumbnail labels to the right panel
                            for (JLabel label : chunks) {
                                rightPanel.add(label);
                            }
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }

                        @Override
                        protected void done() {
                            // Final UI updates if needed
                        }
                    }.execute();
                } else {
                    // Remove all thumbnail labels from the right panel
                    rightPanel.removeAll();

                    // Refresh the layout
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            });

            // Add components to the panel
            locationPanel.add(checkBox);
            locationPanel.add(locationField);

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
        JLabel tagsLabel = new JLabel("Create");
        JButton addTagButton = new JButton("+");
        tagsTopPanel.add(tagsLabel);
        tagsTopPanel.add(addTagButton);
        tagsPanel.add(tagsTopPanel, BorderLayout.NORTH);
        tagsContentPanel = new JPanel();
        tagsContentPanel.setLayout(new BoxLayout(tagsContentPanel, BoxLayout.Y_AXIS));
        JScrollPane tagsScrollPane = new JScrollPane(tagsContentPanel);
        tagsPanel.add(tagsScrollPane, BorderLayout.CENTER);

        addTagButton.addActionListener(e -> {
            // Show input box and save button for new tag
            JTextField newTagField = new JTextField();
            newTagField.setPreferredSize(new Dimension(150, newTagField.getPreferredSize().height)); // Set preferred size
            JButton saveTagButton = new JButton("Save Tag");

            JPanel newTagPanel = new JPanel();
            newTagPanel.setLayout(new BoxLayout(newTagPanel, BoxLayout.X_AXIS));
            newTagPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            newTagPanel.add(newTagField);
            newTagPanel.add(saveTagButton);

            tagsContentPanel.add(newTagPanel);
            tagsContentPanel.revalidate();
            tagsContentPanel.repaint();

            saveTagButton.addActionListener(evt -> {
                String newTagName = newTagField.getText().trim();
                if (!newTagName.isEmpty()) {
                    // Generate a random color for the new tag
                    Color randomColor = new Color(new Random().nextInt(0xFFFFFF));

                    // Create a label for the new tag
                    JLabel newTagLabel = new JLabel(newTagName);
                    newTagLabel.setOpaque(true);
                    newTagLabel.setBackground(randomColor);
                    newTagLabel.setForeground(Color.WHITE);
                    newTagLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

                    // Add mouse listener to select/deselect the tag
                    newTagLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            // Deselect the previously selected tag
                            if (selectedTag != null && selectedTagColor != null) {
                                for (Component comp : tagsContentPanel.getComponents()) {
                                    if (comp instanceof JLabel) {
                                        JLabel label = (JLabel) comp;
                                        if (label.getText().equals(selectedTag)) {
                                            label.setBackground(selectedTagColor);
                                            break;
                                        }
                                    }
                                }
                            }

                            // Select the clicked tag
                            selectedTag = newTagName;
                            selectedTagColor = randomColor;
                            newTagLabel.setBackground(selectedTagColor.darker());

                            // Highlight the selected tag's thumbnails
                            for (Component comp : rightPanel.getComponents()) {
                                if (comp instanceof JLabel) {
                                    JLabel label = (JLabel) comp;
                                    if (label.getBorder() != null) { // Check if the thumbnail is selected
                                        label.setBorder(BorderFactory.createLineBorder(selectedTagColor, 2));
                                    }
                                }
                            }
                        }
                    });

                    // Add the new tag label to the content panel
                    tagsContentPanel.add(newTagLabel);
                    tagsContentPanel.remove(newTagPanel);
                    tagsContentPanel.revalidate();
                    tagsContentPanel.repaint();
                }
            });
        });

        // Delete Tag Button
        JButton deleteTagButton = new JButton("Delete Selected Tag");
        deleteTagButton.addActionListener(e -> {
            if (selectedTag != null) {
                for (Component comp : tagsContentPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getText().equals(selectedTag)) {
                            tagsContentPanel.remove(label);
                            selectedTag = null;
                            selectedTagColor = null;
                            tagsContentPanel.revalidate();
                            tagsContentPanel.repaint();
                            break;
                        }
                    }
                }
            }
        });
        tagsTopPanel.add(deleteTagButton);

        // Apply Button to persist tag-to-photo relationships
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            if (selectedTag != null) {
                // Save the selected tag and image information to the database
                for (Component comp : rightPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getBorder() != null) { // Check if the thumbnail is selected
                            File file = new File(label.getText());
                            DatabaseUtility databaseUtility = new DatabaseUtility();
                            try {
                                databaseUtility.createFile(file);
                                databaseUtility.createTag(selectedTag);
                                databaseUtility.createFilesTagsRelationship(file, selectedTag);
                            } catch (SQLException ex) {
                                Logger.getLogger(Taggat.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        });
        tagsTopPanel.add(applyButton);

        // Update Tag Button
        JButton updateTagButton = new JButton("Update Selected Tag");
        updateTagButton.addActionListener(e -> {
            if (selectedTag != null) {
                JTextField updateTagField = new JTextField(selectedTag);
                JButton saveUpdateButton = new JButton("Save");

                JPanel updateTagPanel = new JPanel();
                updateTagPanel.setLayout(new BoxLayout(updateTagPanel, BoxLayout.X_AXIS));
                updateTagPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                updateTagPanel.add(updateTagField);
                updateTagPanel.add(saveUpdateButton);

                tagsContentPanel.add(updateTagPanel);
                tagsContentPanel.revalidate();
                tagsContentPanel.repaint();

                saveUpdateButton.addActionListener(evt -> {
                    String updatedTagName = updateTagField.getText().trim();
                    if (!updatedTagName.isEmpty()) {
                        for (Component comp : tagsContentPanel.getComponents()) {
                            if (comp instanceof JLabel) {
                                JLabel label = (JLabel) comp;
                                if (label.getText().equals(selectedTag)) {
                                    label.setText(updatedTagName);
                                    selectedTag = updatedTagName;
                                    tagsContentPanel.remove(updateTagPanel);
                                    tagsContentPanel.revalidate();
                                    tagsContentPanel.repaint();
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
        tagsTopPanel.add(updateTagButton);

        // Save Button on Right Panel
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (selectedTag != null) {
                // Save the selected tag and image information to the database
                for (Component comp : rightPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getBorder() != null) { // Check if the thumbnail is selected
                            // Save the file path, tag, and relationship to the database
                            File file = new File(label.getText());
                            DatabaseUtility databaseUtility = new DatabaseUtility();
                            try {
                                databaseUtility.createFile(file);
                                databaseUtility.createTag(selectedTag);
                                databaseUtility.createFilesTagsRelationship(file, selectedTag);
                            } catch (SQLException ex) {
                                Logger.getLogger(Taggat.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        });
        rightPanel.add(saveButton);

        // Settings tab
        JPanel settingsPanel = new JPanel(new BorderLayout());
        // Remaining code for the Settings panel...

        // Add tabs to tabbedPane
        tabbedPane.addTab("Locations", locationsPanel);
        tabbedPane.addTab("Tags", tagsPanel);
        tabbedPane.addTab("Settings", settingsPanel);

        leftPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add panels to the main frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightScrollPane, BorderLayout.CENTER);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }
}
