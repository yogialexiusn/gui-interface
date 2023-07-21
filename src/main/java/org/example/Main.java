package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("API Client GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        JPanel responsePanel = new JPanel(new BorderLayout());

        // Dropdown for selecting API request
        String[] apiRequests = { "Adding Product", "Adding Category" };
        JComboBox<String> apiRequestComboBox = new JComboBox<>(apiRequests);
        apiRequestComboBox.setSelectedIndex(0); // Set the default selection to "API Request 1"

        // Text view for displaying API URL
        JTextArea apiURLTextArea = new JTextArea(2, 30);
        apiURLTextArea.setEditable(false);

        // Text view for displaying request body
        JTextArea requestBodyTextArea = new JTextArea(4, 30);
        requestBodyTextArea.setEditable(false);

        // Text view for displaying API response
        JTextArea responseTextArea = new JTextArea(10, 30);
        responseTextArea.setEditable(false);

        // Button for hitting the API
        JButton hitAPIButton = new JButton("Hit API");
        hitAPIButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code to handle API request and response will be placed here
                String selectedRequest = (String) apiRequestComboBox.getSelectedItem();
                String apiURL;
                String requestBody;

                if (selectedRequest.equals("Adding Category")) {
                    apiURL = "http://localhost:8080/category/tambah";
                    apiURLTextArea.setText("API URL: " + apiURL);

                    requestBody = "{\"item\":\"category\"}";
                    requestBodyTextArea.setText("Request Body: " + requestBody);
                } else if (selectedRequest.equals("Adding Product")) {
                    apiURL = "http://localhost:8080/product/tambah";
                    apiURLTextArea.setText("API URL: " + apiURL);

                    requestBody = "{\"item\":\"product\"}";
                    requestBodyTextArea.setText("Request Body: " + requestBody);
                } else {
                    // Handle an invalid selection, if necessary
                    return;
                }

                // Call your API request method here and display the response in the text area
                String response;
                try {
                    response = performAPIRequest(apiURL, requestBody);
                } catch (IOException ex) {
                    response = "Error: " + ex.getMessage();
                }
                responseTextArea.setText(response);
            }
        });

        inputPanel.add(new JLabel("Select API Request:"));
        inputPanel.add(apiRequestComboBox);
        inputPanel.add(apiURLTextArea);
        inputPanel.add(requestBodyTextArea);
        inputPanel.add(new JLabel());
        inputPanel.add(hitAPIButton);

        responsePanel.add(new JLabel("API Response:"), BorderLayout.NORTH);
        responsePanel.add(new JScrollPane(responseTextArea), BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(responsePanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Method to perform API request and return the response as a string
    private static String performAPIRequest(String apiURL, String requestBody) throws IOException {
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            }
        } else {
            return "Error: " + responseCode + " " + connection.getResponseMessage();
        }
    }
}
