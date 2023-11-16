import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WordEntryProgram extends JFrame {

    private JLabel label;
    private JTextField textField;
    private JButton enterButton;
    private int successfulEnters = 0;

    public WordEntryProgram() throws IOException {
        setTitle("Word Entry Program");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel("Type any word!", SwingConstants.CENTER);
        textField = new JTextField(15);
        enterButton = new JButton("Enter");

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnterButtonClick();
            }
        });

        // Add KeyListener to the text field
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleEnterButtonClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(label);
        panel.add(textField);
        panel.add(enterButton);

        add(panel);
        setVisible(true);
        newEntry();
    }

    private void handleEnterButtonClick() {
        String enteredWord = textField.getText().trim();

        if (enteredWord.isEmpty() || isInvalid(apiPresenter(enteredWord))) {
            JOptionPane.showMessageDialog(this, "Please enter a valid word!", "Failure", JOptionPane.ERROR_MESSAGE);
        } else {
            successfulEnters++;
            label.setText(enteredWord);
            textField.setText("");

            // Save entered word to a file
            saveToFile(enteredWord);

            if (successfulEnters >= 6) {
                JOptionPane.showMessageDialog(this, "Thank you!", "Success", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
    }

    private void saveToFile(String word) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("entered_words.txt", true))) {
            writer.write(word);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String apiPresenter(String word) {
        return "https://api.dictionaryapi.dev/api/v2/entries/en/" + word.replaceAll("\\s+", "");
    }

    private boolean isInvalid(String urlString) {
        try {
            String apiUrl = urlString;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int lastEntry() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("entered_words.txt"))) {
            String line;
            int entry = 0;

            while ((line = reader.readLine()) != null) {
                entry++;
                // Skip the next 5 lines to move to the next entry
                for (int i = 0; i < 6; i++) {
                    reader.readLine();
                }
            }
            return entry;
        }
    }

    public void newEntry() {
        // Start a new entry
        // Get time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define a format for the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the date and time using the specified format
        String formattedDateTime = currentDateTime.format(formatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("entered_words.txt", true))) {
            writer.write(lastEntry() + " " + formattedDateTime);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new WordEntryProgram();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}