import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class WordEntryProgram extends JFrame {

    private JLabel label;
    private JTextField textField;
    private JButton enterButton;
    private int successfulEnters = 0;

    public WordEntryProgram() {
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

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(label);
        panel.add(textField);
        panel.add(enterButton);

        add(panel);
        setVisible(true);
    }

    private void handleEnterButtonClick() {
        String enteredWord = textField.getText().trim();

        if (enteredWord.isEmpty()) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WordEntryProgram();
            }
        });
    }
}