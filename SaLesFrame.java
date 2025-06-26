import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SalesFrame extends JFrame {
    private DefaultTableModel productTableModel, cartTableModel;
    private JTable productTable, cartTable;
    private JTextField quantityField;
    private JLabel totalLabel;

    public SalesFrame() {
        setTitle("Sales");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] productColumns = {"ID", "Name", "Price", "Stock"};
        Object[][] productData = {
            {"P001", "Milk", 2.99, 50},
            {"P002", "Bread", 1.99, 30},
            {"P003", "Eggs", 3.49, 20},
            {"P004", "Rice", 5.99, 40},
            {"P005", "Sugar", 2.49, 25}
        };
        productTableModel = new DefaultTableModel(productData, productColumns);
        productTable = new JTable(productTableModel);

        String[] cartColumns = {"ID", "Name", "Price", "Quantity", "Subtotal"};
        Object[][] cartData = {};
        cartTableModel = new DefaultTableModel(cartData, cartColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(1, 2, 10, 10))
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addToCartPanel.setBorder(BorderFactory.createTitledBorder("Add to Cart"));

        quantityField = new JTextField(5);
        JButton addButton = new JButton("Add to Cart");
        addToCartPanel.add(new JLabel("Quantity:"));
        addToCartPanel.add(quantityField);
        addToCartPanel.add(addButton);

        // Checkout panel
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        checkoutPanel.setBorder(BorderFactory.createTitledBorder("Checkout"));

        totalLabel = new JLabel("Total: $0.00");
        JButton checkoutButton = new JButton("Complete Sale");
        JButton clearButton = new JButton("Clear Cart");
        checkoutPanel.add(totalLabel);
        checkoutPanel.add(checkoutButton);
        checkoutPanel.add(clearButton);

        controlPanel.add(addToCartPanel);
        controlPanel.add(checkoutPanel);

        mainPanel.add(productPanel, BorderLayout.WEST);
        mainPanel.add(cartPanel, BorderLayout.EAST);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(SalesFrame.this, 
                        "Please select a product", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String quantityText = quantityField.getText();
                if (quantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(SalesFrame.this, 
                        "Please enter quantity", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int quantity = Integer.parseInt(quantityText);
                    int stock = Integer.parseInt(productTable.getValueAt(selectedRow, 3).toString());
                    
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(SalesFrame.this, 
                            "Quantity must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (quantity > stock) {
                        JOptionPane.showMessageDialog(SalesFrame.this, 
                            "Not enough stock available", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String id = productTable.getValueAt(selectedRow, 0).toString();
                    String name = productTable.getValueAt(selectedRow, 1).toString();
                    double price = Double.parseDouble(productTable.getValueAt(selectedRow, 2).toString());
                    double subtotal = price * quantity;

                    // Check if product already in cart
                    boolean found = false;
                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        if (cartTableModel.getValueAt(i, 0).equals(id)) {
                            int existingQty = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
                            cartTableModel.setValueAt(existingQty + quantity, i, 3);
                            cartTableModel.setValueAt(price * (existingQty + quantity), i, 4);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        cartTableModel.addRow(new Object[]{id, name, price, quantity, subtotal});
                    }

                    productTableModel.setValueAt(stock - quantity, selectedRow, 3);
                    updateTotal();
                    quantityField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SalesFrame.this, 
                        "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartTableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(SalesFrame.this, 
                        "Cart is empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double total = calculateTotal();
                JOptionPane.showMessageDialog(SalesFrame.this, 
                    "Sale completed!\nTotal: $" + String.format("%.2f", total), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                cartTableModel.setRowCount(0);
                updateTotal();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Return items to stock
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String id = cartTableModel.getValueAt(i, 0).toString();
                    int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
                    
                    for (int j = 0; j < productTableModel.getRowCount(); j++) {
                        if (productTableModel.getValueAt(j, 0).equals(id)) {
                            int currentStock = Integer.parseInt(productTableModel.getValueAt(j, 3).toString());
                            productTableModel.setValueAt(currentStock + quantity, j, 3);
                            break;
                        }
                    }
                }
                
                cartTableModel.setRowCount(0);
                updateTotal();
            }
        });

        add(mainPanel);
    }

    private double calculateTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += Double.parseDouble(cartTableModel.getValueAt(i, 4).toString());
        }
        return total;
    }

    private void updateTotal() {
        double total = calculateTotal();
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }
}
