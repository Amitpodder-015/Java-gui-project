import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProductManagementFrame extends JFrame {
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField idField, nameField, priceField, quantityField;

    public ProductManagementFrame() {
        setTitle("Product Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        String[] columns = {"ID", "Name", "Price", "Quantity"};
        Object[][] data = {
            {"P001", "Milk", 2.99, 50},
            {"P002", "Bread", 1.99, 30},
            {"P003", "Eggs", 3.49, 20}
        };
        tableModel = new DefaultTableModel(data, columns);
        productTable = new JTable(tableModel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        formPanel.add(new JLabel("Product ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String name = nameField.getText();
                String price = priceField.getText();
                String quantity = quantityField.getText();

                if (id.isEmpty() || name.isEmpty() || price.isEmpty() || quantity.isEmpty()) {
                    JOptionPane.showMessageDialog(ProductManagementFrame.this, 
                        "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                tableModel.addRow(new Object[]{id, name, price, quantity});
                clearFields();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ProductManagementFrame.this, 
                        "Please select a row to update", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String id = idField.getText();
                String name = nameField.getText();
                String price = priceField.getText();
                String quantity = quantityField.getText();

                tableModel.setValueAt(id, selectedRow, 0);
                tableModel.setValueAt(name, selectedRow, 1);
                tableModel.setValueAt(price, selectedRow, 2);
                tableModel.setValueAt(quantity, selectedRow, 3);
                clearFields();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ProductManagementFrame.this, 
                        "Please select a row to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tableModel.removeRow(selectedRow);
                clearFields();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

     
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                idField.setText(productTable.getValueAt(selectedRow, 0).toString());
                nameField.setText(productTable.getValueAt(selectedRow, 1).toString());
                priceField.setText(productTable.getValueAt(selectedRow, 2).toString());
                quantityField.setText(productTable.getValueAt(selectedRow, 3).toString());
            }
        });

        add(mainPanel);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }
}
