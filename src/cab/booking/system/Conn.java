package cab.booking.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import javax.swing.*;

public class BookCab extends JFrame {
    private JLabel ReservationDetails, Source, Destination;
    private JButton show, book, cancel;
    private Choice sourceChoice, destinationChoice;
    private JLabel driverLabel, priceLabel, carLabel, routeLabel, referenceLabel, nameLabel, usernameLabel;

    private static final String DB_URL = "jdbc:mysql://localhost/my_database";
    private static final String USER = "root";
    private static final String PASSWORD = "sonusoni00";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookCab("").setVisible(true));
    }

    public BookCab(String username) {
        setTitle("Book Cab");
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        ReservationDetails = new JLabel("BOOK INTRACITY CAB");
        ReservationDetails.setForeground(Color.BLUE);
        ReservationDetails.setFont(new Font("Tahoma", Font.PLAIN, 31));
        ReservationDetails.setBounds(280, 27, 359, 31);
        add(ReservationDetails);

        Source = new JLabel("SOURCE");
        Source.setFont(new Font("Tahoma", Font.PLAIN, 19));
        Source.setBounds(60, 100, 70, 27);
        add(Source);

        Destination = new JLabel("DESTINATION");
        Destination.setFont(new Font("Tahoma", Font.PLAIN, 19));
        Destination.setBounds(350, 100, 150, 27);
        add(Destination);

        sourceChoice = new Choice();
        sourceChoice.setBounds(150, 100, 150, 27);
        add(sourceChoice);

        destinationChoice = new Choice();
        destinationChoice.setBounds(500, 100, 150, 27);
        add(destinationChoice);

        show = new JButton("SHOW DETAILS");
        show.setBackground(Color.BLACK);
        show.setForeground(Color.WHITE);
        show.setBounds(680, 100, 130, 27);
        add(show);

        // Labels for displaying cab details
        driverLabel = createLabel("Driver Name : ", 50, 300);
        priceLabel = createLabel("Price : ", 50, 450);
        carLabel = createLabel("Car : ", 50, 350);
        routeLabel = createLabel("Route : ", 50, 400);
        referenceLabel = createLabel("Reference Number : ", 50, 150);
        nameLabel = createLabel("Name : ", 50, 200);
        usernameLabel = createLabel("Username : ", 50, 250);

        Random random = new Random();
        JLabel referenceValueLabel = createLabel(String.valueOf(Math.abs(random.nextInt() % 100000)), 250, 150);

        show.addActionListener(e -> showDetails(username));

        book = createButton("Book CAB", 50, 500, e -> bookCab(username, referenceValueLabel.getText()));
        cancel = createButton("Cancel", 250, 500, e -> setVisible(false));

        loadChoices();

        setSize(860, 600);
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.PLAIN, 19));
        label.setBounds(x, y, 250, 27);
        add(label);
        return label;
    }

    private JButton createButton(String text, int x, int y, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBounds(x, y, 150, 30);
        button.addActionListener(listener);
        add(button);
        return button;
    }

    private void loadChoices() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM intracity")) {

            while (resultSet.next()) {
                sourceChoice.add(resultSet.getString("source"));
                destinationChoice.add(resultSet.getString("destination"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(String username) {
        String src = sourceChoice.getSelectedItem();
        String dst = destinationChoice.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM intracity WHERE destination = '" + dst + "'");
            if (resultSet.next()) {
                driverLabel.setText("Driver: " + resultSet.getString("driver"));
                priceLabel.setText("Price: Rs " + resultSet.getString("price"));
                carLabel.setText("Car: " + resultSet.getString("car"));
            }

            resultSet = statement.executeQuery("SELECT * FROM customer WHERE username = '" + username + "'");
            if (resultSet.next()) {
                nameLabel.setText("Name: " + resultSet.getString("name"));
            }

            routeLabel.setText("Route: " + src + " --> " + dst);
            usernameLabel.setText("Username: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void bookCab(String username, String referenceNumber) {
        String src = sourceChoice.getSelectedItem();
        String dst = destinationChoice.getSelectedItem();

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM intraCab");

            String query = "INSERT INTO intraCab VALUES('" + username + "','" + driverLabel.getText() + "','" +
                    src + "','" + dst + "','" + carLabel.getText() + "','" + priceLabel.getText() + "','" +
                    referenceNumber + "')";
            statement.executeUpdate(query);

            JOptionPane.showMessageDialog(null, "Cab Booked Successfully");
            setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
