import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class PhonebookSystem {
    private DefaultTableModel tableModel;
    private JTable contactTable;
    private JTextField firstNameField, lastNameField, locationField, phoneField, searchField;
    private JComboBox<String> groupComboBox;
    private HashMap<String, ArrayList<Vector<Object>>> groups = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhonebookSystem().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("PHONE BOOK");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(204, 248, 255));

        JPanel titlePanel = createTitlePanel();
        frame.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"FIRSTNAME", "LASTNAME", "LOCATION", "PHONE", "GROUP"};
        tableModel = new DefaultTableModel(columnNames, 0);
        contactTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        contactTable.setRowSorter(sorter);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = createInputPanel();
        frame.add(inputPanel, BorderLayout.SOUTH);

        JPanel searchPanel = createSearchPanel();
        frame.add(searchPanel, BorderLayout.NORTH);

        loadContactsFromFile();

        frame.setVisible(true);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("PHONE BOOK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(204, 248, 255));
        return titlePanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        addComponent(inputPanel, new JLabel("First Name:"), gbc, 0, 0, 1, 1);
        addComponent(inputPanel, firstNameField = new JTextField(20), gbc, 1, 0, 1, 1);
        addComponent(inputPanel, new JLabel("Last Name:"), gbc, 0, 1, 1, 1);
        addComponent(inputPanel, lastNameField = new JTextField(20), gbc, 1, 1, 1, 1);
        addComponent(inputPanel, new JLabel("Location:"), gbc, 0, 2, 1, 1);
        addComponent(inputPanel, locationField = new JTextField(20), gbc, 1, 2, 1, 1);
        addComponent(inputPanel, new JLabel("Phone:"), gbc, 0, 3, 1, 1);
        addComponent(inputPanel, phoneField = new JTextField(20), gbc, 1, 3, 1, 1);
        addComponent(inputPanel, new JLabel("Group:"), gbc, 0, 4, 1, 1);
        addComponent(inputPanel, groupComboBox = new JComboBox<>(), gbc, 1, 4, 1, 1);

        JButton addGroupButton = createButton("Add Group", e -> addGroup());
        addComponent(inputPanel, addGroupButton, gbc, 0, 5, 2, 1);

        JButton addButton = createButton("Add Contact", e -> addContact());
        addComponent(inputPanel, addButton, gbc, 0, 6, 1, 1);

        JButton updateButton = createButton("Update", e -> editContact());
        addComponent(inputPanel, updateButton, gbc, 1, 6, 1, 1);

        JButton deleteButton = createButton("Delete", e -> deleteContact());
        addComponent(inputPanel, deleteButton, gbc, 0, 7, 1, 1);

        JButton clearButton = createButton("Clear", e -> clearFields());
        addComponent(inputPanel, clearButton, gbc, 1, 7, 1, 1);

        inputPanel.setBackground(new Color(204, 248, 255));
        return inputPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchContact(searchField.getText()));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.setBackground(new Color(204, 248, 255));
        return searchPanel;
    }

    private void addComponent(JPanel panel, Component component, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        panel.add(component, gbc);
    }

    private JButton createButton(String text, ActionEvent e) {
        JButton button = new JButton(text);
        button.setBackground(Color.PINK);
        return button;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.PINK);
        button.addActionListener(listener);
        return button;
    }

    private void addContact() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String location = locationField.getText();
        String phone = phoneField.getText();
        String group = (String) groupComboBox.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || location.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        tableModel.addRow(new Object[]{firstName, lastName, location, phone, group});
        saveContactsToFile();
        clearFields();
    }

    private void addGroup() {
        String groupName = JOptionPane.showInputDialog("Enter Group Name:");
        if (groupName != null && !groupName.isEmpty()) {
            groupComboBox.addItem(groupName);
            groups.put(groupName, new ArrayList<>());
            JOptionPane.showMessageDialog(null, "Group added successfully.");
        }
    }

    private void filterByGroup() {
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(null, "No group selected.");
            return;
        }

        DefaultTableModel filteredModel = new DefaultTableModel(new String[]{"FIRSTNAME", "LASTNAME", "LOCATION", "PHONE", "GROUP"}, 0);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (selectedGroup.equals(tableModel.getValueAt(i, 4))) {
                filteredModel.addRow(new Object[]{
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3),
                        tableModel.getValueAt(i, 4)
                });
            }
        }
        contactTable.setModel(filteredModel);
    }

    private void editContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a contact to edit.");
            return;
        }
        int modelRow = contactTable.convertRowIndexToModel(selectedRow);

        tableModel.setValueAt(firstNameField.getText(), modelRow, 0);
        tableModel.setValueAt(lastNameField.getText(), modelRow, 1);
        tableModel.setValueAt(locationField.getText(), modelRow, 2);
        tableModel.setValueAt(phoneField.getText(), modelRow, 3);
        tableModel.setValueAt(groupComboBox.getSelectedItem(), modelRow, 4);

        saveContactsToFile();
        clearFields();
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a contact to delete.");
            return;
        }
        int modelRow = contactTable.convertRowIndexToModel(selectedRow);
        tableModel.removeRow(modelRow);
        saveContactsToFile();
        clearFields();
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        locationField.setText("");
        phoneField.setText("");
        groupComboBox.setSelectedIndex(-1);
    }

    private void searchContact(String query) {
        String lowerCaseQuery = query.toLowerCase();
        ((TableRowSorter) contactTable.getRowSorter()).setRowFilter(RowFilter.regexFilter("(?i)" + lowerCaseQuery));
    }

    private void saveContactsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("contacts.txt"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Vector<?> row = tableModel.getDataVector().elementAt(i);
                writer.write(String.join(",", row.stream().map(Object::toString).toArray(String[]::new)));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving contacts: " + e.getMessage());
        }
    }

    private void loadContactsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("contacts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tableModel.addRow(line.split(","));
            }
        } catch (FileNotFoundException e) {
            //File not found, do nothing. Could also create an empty file here.
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading contacts: " + e.getMessage());
        }
    }
}