package cab.booking.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class BookCab extends JFrame {

    private JLabel ReservationDetails, Source, Destination, l13, l14, l9, l10, l11, l12, l1, l2, l3, l4, l5, l6, l7, l8;
    private JButton show, book, cancel;
    private Choice c1, c2;
   

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookCab("").setVisible(true));
    }

    public BookCab(String username) {
        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost/my_database", "root", "sonusoni00");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database.");
            e.printStackTrace();
            System.exit(1);
        }

        setTitle("Book Cab");
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        ReservationDetails = createLabel("BOOK INTRACITY CAB", Color.BLUE, 31, 280, 27, 359, 31);
        Source = createLabel("SOURCE", null, 19, 60, 100, 70, 27);
        Destination = createLabel("DESTINATION", null, 19, 350, 100, 150, 27);

        c1 = createChoice(150, 100, 150, 27);
        c2 = createChoice(500, 100, 150, 27);

        show = createButton("SHOW DETAILS", Color.WHITE, Color.BLACK, 680, 100, 130, 27, this::showDetails);
        book = createButton("Book CAB", Color.WHITE, Color.BLACK, 50, 500, 150, 30, this::bookCab);
        cancel = createButton("Cancel", Color.WHITE, Color.BLACK, 250, 500, 150, 30, e -> setVisible(false));

        l13 = createLabel("Reference Number : ", null, 19, 50, 150, 250, 27);
      
        l9 = createLabel("Name : ", null, 19, 50, 200, 250, 27);
        l10 = createLabel("", null, 19, 200, 200, 150, 27);
        l11 = createLabel("Username : ", null, 19, 50, 250, 150, 27);
        l12 = createLabel(username, null, 19, 200, 250, 350, 27);
        l1 = createLabel("Driver Name : ", null, 19, 50, 300, 150, 27);
        l2 = createLabel("", null, 19, 200, 300, 150, 27);
        l3 = createLabel("Price : ", null, 19, 50, 450, 150, 27);
        l4 = createLabel("", null, 19, 200, 450, 150, 27);
        l5 = createLabel("Car : ", null, 19, 50, 350, 250, 27);
        l6 = createLabel("", null, 19, 200, 350, 150, 27);
        l7 = createLabel("Route : ", null, 19, 50, 400, 150, 27);
        l8 = createLabel("", null, 19, 200, 400, 350, 27);

        loadChoices();

        setSize(860, 600);
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String text, Color color, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        if (color != null) label.setForeground(color);
        label.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        label.setBounds(x, y, width, height);
        add(label);
        return label;
    }

    private JButton createButton(String text, Color foreground, Color background, int x, int y, int width, int height, ActionListener listener) {
        JButton button = new JButton(text);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setBounds(x, y, width, height);
        button.addActionListener(listener);
        add(button);
        return button;
    }

    private Choice createChoice(int x, int y, int width, int height) {
        Choice choice = new Choice();
        choice.setBounds(x, y, width, height);
        add(choice);
        return choice;
    }

  

    private void loadChoices() {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM intracity")) {

            while (resultSet.next()) {
                c1.add(resultSet.getString("source"));
                c2.add(resultSet.getString("destination"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load choices from database.");
            e.printStackTrace();
        }
    }

    private void showDetails(ActionEvent ae) {
        try {
            String src = c1.getSelectedItem();
            String dst = c2.getSelectedItem();

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer WHERE username = ?");
            ps.setString(1, "username");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                l10.setText(rs.getString("name"));
            }

            ps = connection.prepareStatement("SELECT * FROM intracity WHERE destination = ?");
            ps.setString(1, dst);
            rs = ps.executeQuery();

            if (rs.next()) {
                l2.setText(rs.getString("driver"));
                l4.setText("Rs " + rs.getString("price"));
                l6.setText(rs.getString("car"));
            }

            l8.setText(src + " --> " + dst);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch details from database.");
            e.printStackTrace();
        }
    }

    private void bookCab(ActionEvent ae) {
        try {
            String src = c1.getSelectedItem();
            String dst = c2.getSelectedItem();
            String name = l2.getText();
            String price = l4.getText();
            String car = l6.getText();
            String ref = l14.getText();

            PreparedStatement ps = connection.prepareStatement("DELETE FROM intraCab");
            ps.executeUpdate();

            ps = connection.prepareStatement("INSERT INTO intraCab VALUES(?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, "username");
            ps.setString(2, name);
            ps.setString(3, src);
            ps.setString(4, dst);
            ps.setString(5, car);
            ps.setString(6, price);
            ps.setString(7, ref);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Cab Booked Successfully");
            setVisible(false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to book cab.");
            e.printStackTrace();
        }
    }
}
