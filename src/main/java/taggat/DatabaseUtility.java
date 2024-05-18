package taggat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtility {

    private static final Logger logger = Logger.getLogger(DatabaseUtility.class.getName());
    private Connection connection;

    // Initialize the database connection
    public void startSession() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:taggat.db");
        connection.setAutoCommit(false);
    }

    // Close the database connection
    public void endSession() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    //Commit actions to database
    public void commit() throws SQLException {
        connection.commit(); // Assuming conn is your Connection object
    }

    // Create the database
    public void createDatabase() throws SQLException {
        // Database will be created automatically when connection is established
    }

    // Create Files table
    public void createFilesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY AUTOINCREMENT, filepath TEXT UNIQUE NOT NULL)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    // Create Tags table
    public void createTagsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS tags (id INTEGER PRIMARY KEY AUTOINCREMENT, tag TEXT UNIQUE NOT NULL)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    // Create FilesTags table (many-to-many relationship)
    public void createFilesTagsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS filestags (file_id INTEGER NOT NULL, tag_id INTEGER NOT NULL, FOREIGN KEY(file_id) REFERENCES files(id), FOREIGN KEY(tag_id) REFERENCES tags(id))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    // Add a new file
    public void createFile(File file) throws SQLException {
        String sql = "INSERT INTO files(filepath) VALUES(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, file.getAbsolutePath());
            preparedStatement.executeUpdate();
        }
    }

    // Add multiple files without overwriting duplicates
    public void createFiles(List<File> fileList) throws SQLException {
        String sql = "INSERT OR IGNORE INTO files(filepath) VALUES(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Set<String> filePaths = new HashSet<>(); // To keep track of duplicates
            for (File file : fileList) {
                String absolutePath = file.getAbsolutePath();
                if (!filePaths.contains(absolutePath)) { // Check for duplicates
                    preparedStatement.setString(1, absolutePath);
                    preparedStatement.addBatch(); // Add to batch
                    filePaths.add(absolutePath);
                }
            }
            preparedStatement.executeBatch(); // Execute batch
        }
    }

    // List all files
    public List<String> readFiles() throws SQLException {
        List<String> filepaths = new ArrayList<>();
        String sql = "SELECT filepath FROM files";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                filepaths.add(rs.getString("filepath"));
            }
        }
        return filepaths;
    }

    // Update a file's path by id
    public void updateFile(Integer id, File file) throws SQLException {
        String sql = "UPDATE files SET filepath = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, file.getAbsolutePath());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    // Delete a file by filepath
    public void deleteFile(File file) throws SQLException {
        String sql = "DELETE FROM files WHERE filepath = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, file.getAbsolutePath());
            preparedStatement.executeUpdate();
        }
    }

    // Add a new tag
    public void createTag(String tag) throws SQLException {
        String sql = "INSERT INTO tags(tag) VALUES(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tag);
            preparedStatement.executeUpdate();
        }
    }

    // List all tags
    public List<String> readTags() throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT tag FROM tags";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                tags.add(rs.getString("tag"));
            }
        }
        return tags;
    }

    // Update a tag
    public void updateTag(String tag, String newTag) throws SQLException {
        String sql = "UPDATE tags SET tag = ? WHERE tag = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newTag);
            preparedStatement.setString(2, tag);
            preparedStatement.executeUpdate();
        }
    }

    // Delete a tag
    public void deleteTag(String tag) throws SQLException {
        String sql = "DELETE FROM tags WHERE tag = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tag);
            preparedStatement.executeUpdate();
        }
    }

    // Create relationship between a file and a tag
    public void createFilesTagsRelationship(File file, String tag) throws SQLException {
        String sql = "INSERT INTO filestags (file_id, tag_id) VALUES ((SELECT id FROM files WHERE filepath = ?), (SELECT id FROM tags WHERE tag = ?))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, file.getAbsolutePath());
            preparedStatement.setString(2, tag);
            preparedStatement.executeUpdate();
        }
    }

    // List all files with a specific tag
    public List<String> readFiles(String tag) throws SQLException {
        List<String> filepaths = new ArrayList<>();
        String sql = "SELECT f.filepath FROM files f INNER JOIN filestags ft ON f.id = ft.file_id INNER JOIN tags t ON t.id = ft.tag_id WHERE t.tag = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tag);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    filepaths.add(rs.getString("filepath"));
                }
            }
        }
        return filepaths;
    }

    // List all tags for a specific file
    public List<String> readTags(File file) throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.tag FROM tags t INNER JOIN filestags ft ON t.id = ft.tag_id INNER JOIN files f ON f.id = ft.file_id WHERE f.filepath = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, file.getAbsolutePath());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("tag"));
                }
            }
        }
        return tags;
    }

    public static void main(String[] args) {
        DatabaseUtility databaseUtility = new DatabaseUtility();
        File tempDbFile = new File("taggat.db");

        try {
            // Set up the database connection
            databaseUtility.startSession();

            // Create tables
            databaseUtility.createFilesTable();
            databaseUtility.commit();

            databaseUtility.createTagsTable();
            databaseUtility.commit();

            databaseUtility.createFilesTagsTable();
            databaseUtility.commit();

            // Test creating and reading files
            File file1 = new File("file1.txt");
            File file2 = new File("file2.txt");
            databaseUtility.createFile(file1);
            databaseUtility.createFile(file2);
            databaseUtility.commit();

            System.out.println("Files after creation:");
            List<String> files = databaseUtility.readFiles();
            files.forEach(System.out::println);
            
            // Test creating and reading multiple files
            List<File> fileList = new ArrayList<>(Arrays.asList(new File("file3.txt"), new File("file4.txt"), new File("file5.txt")));
            databaseUtility.createFiles(fileList);
            databaseUtility.commit();

            System.out.println("Files after batch creation:");
            files = databaseUtility.readFiles();
            files.forEach(System.out::println);

            // Test updating file
            databaseUtility.updateFile(1, new File("fileUpdated.txt"));
            System.out.println("Files after update:");
            files = databaseUtility.readFiles();
            files.forEach(System.out::println);

            // Test deleting file
            databaseUtility.deleteFile(file2);
            System.out.println("Files after deletion:");
            files = databaseUtility.readFiles();
            files.forEach(System.out::println);

            // Test creating and reading tags
            databaseUtility.createTag("Tag1");
            databaseUtility.createTag("Tag2");
            System.out.println("Tags after creation:");
            List<String> tags = databaseUtility.readTags();
            tags.forEach(System.out::println);

            // Test updating tag
            databaseUtility.updateTag("Tag1", "UpdatedTag1");
            System.out.println("Tags after update:");
            tags = databaseUtility.readTags();
            tags.forEach(System.out::println);

            // Test deleting tag
            databaseUtility.deleteTag("Tag2");
            System.out.println("Tags after deletion:");
            tags = databaseUtility.readTags();
            tags.forEach(System.out::println);

            // Test many-to-many relationship (FilesTags)
            System.out.println("Create relationship between file and tag");
            databaseUtility.createFilesTagsRelationship(new File("fileUpdated.txt"), "UpdatedTag1");
            System.out.println("Files for UpdatedTag1:");
            List<String> taggedFiles = databaseUtility.readFiles("UpdatedTag1");
            taggedFiles.forEach(System.out::println);

            System.out.println("Tags for fileUpdated.txt:");
            List<String> fileTags = databaseUtility.readTags(new File("fileUpdated.txt"));
            fileTags.forEach(System.out::println);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                // End the database session
                databaseUtility.endSession();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            try {
                // Delete the temporary database file
                Files.deleteIfExists(tempDbFile.toPath());
                System.out.println("Temporary database file deleted.");
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}
