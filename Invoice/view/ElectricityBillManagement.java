import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

abstract class Customer {
    String customerCode;
    String fullName;
    Date invoiceDate;
    int quantity; // KW consumed
    double unitPrice;

    public Customer(String customerCode, String fullName, Date invoiceDate, int quantity, double unitPrice) {
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.invoiceDate = invoiceDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    abstract double calculateAmount();
}

class VietnameseCustomer extends Customer {
    String customerType; // Living, Business, Production
    int quota;

    public VietnameseCustomer(String customerCode, String fullName, Date invoiceDate, int quantity, double unitPrice, String customerType, int quota) {
        super(customerCode, fullName, invoiceDate, quantity, unitPrice);
        this.customerType = customerType;
        this.quota = quota;
    }

    @Override
    double calculateAmount() {
        if (quantity <= quota) {
            return quantity * unitPrice;
        } else {
            return quota * unitPrice + (quantity - quota) * unitPrice * 2.5;
        }
    }
}

class ForeignCustomer extends Customer {
    String nationality;

    public ForeignCustomer(String customerCode, String fullName, Date invoiceDate, int quantity, double unitPrice, String nationality) {
        super(customerCode, fullName, invoiceDate, quantity, unitPrice);
        this.nationality = nationality;
    }

    @Override
    double calculateAmount() {
        return quantity * unitPrice;
    }
}

public class ElectricityBillManagement extends JFrame {
    private ArrayList<Customer> customers = new ArrayList<>();
    private JTextArea outputArea;

    public ElectricityBillManagement() {
        setTitle("Quản Lý Hóa Đơn Tiền Điện");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);

        JButton addInvoiceButton = new JButton("Thêm Hóa Đơn");
        JButton deleteInvoiceButton = new JButton("Xóa Hóa Đơn");
        JButton editInvoiceButton = new JButton("Sửa Hóa Đơn");
        JButton totalQuantityButton = new JButton("Tổng");
        JButton averageAmountButton = new JButton("Trung Bình Nước Ngoài");
        JButton outputInvoicesButton = new JButton("Xuất Hóa Đơn Theo Tháng");
        JButton searchInvoicesButton = new JButton("Tìm Hóa Đơn");

        controlPanel.add(addInvoiceButton);
        controlPanel.add(deleteInvoiceButton);
        controlPanel.add(editInvoiceButton);
        controlPanel.add(totalQuantityButton);
        controlPanel.add(averageAmountButton);
        controlPanel.add(outputInvoicesButton);
        controlPanel.add(searchInvoicesButton);

        addInvoiceButton.addActionListener(e -> addInvoice());
        deleteInvoiceButton.addActionListener(e -> deleteInvoice());
        editInvoiceButton.addActionListener(e -> editInvoice());
        totalQuantityButton.addActionListener(e -> calculateTotalQuantity());
        averageAmountButton.addActionListener(e -> calculateAverageAmount());
        outputInvoicesButton.addActionListener(e -> outputInvoices());
        searchInvoicesButton.addActionListener(e -> searchInvoices());

        setVisible(true);
    }

    private void addInvoice() {
        String[] customerTypes = {"Việt Nam", "Nước Ngoài"};
        String customerType = (String) JOptionPane.showInputDialog(this, "Chọn nơi khách hàng sống", "Loại Khách hàng",
                JOptionPane.QUESTION_MESSAGE, null, customerTypes, customerTypes[0]);

        if (customerType == null) return;

        String customerCode = JOptionPane.showInputDialog("Nhập mã KH:");
        String fullName = JOptionPane.showInputDialog("Nhập tên KH:");
        String dateString = JOptionPane.showInputDialog("Nhập ngày hóa đơn (dd/MM/yyyy):");
        Date invoiceDate = null;
        try {
            invoiceDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ.");
            return;
        }
        int quantity = Integer.parseInt(JOptionPane.showInputDialog("Nhập số KW:"));
        double unitPrice = Double.parseDouble(JOptionPane.showInputDialog("Nhập đơn giá:"));

        if (customerType.equals("Việt Nam")) {
            String[] vnTypes = {"Sinh hoạt", "Kinh doanh", "Sản xuất"};
            String vnCustomerType = (String) JOptionPane.showInputDialog(this, "Chọn loại khách hàng", "Loại khách hàng",
                    JOptionPane.QUESTION_MESSAGE, null, vnTypes, vnTypes[0]);
            int quota = Integer.parseInt(JOptionPane.showInputDialog("Nhập định mức:"));

            VietnameseCustomer vnCustomer = new VietnameseCustomer(customerCode, fullName, invoiceDate, quantity, unitPrice, vnCustomerType, quota);
            customers.add(vnCustomer);

        } else {
            String nationality = JOptionPane.showInputDialog("Nhập quốc tịch:");

            ForeignCustomer foreignCustomer = new ForeignCustomer(customerCode, fullName, invoiceDate, quantity, unitPrice, nationality);
            customers.add(foreignCustomer);
        }

        displayCustomers();
    }

    private void deleteInvoice() {
        String customerCode = JOptionPane.showInputDialog("Nhập mã KH để xóa:");
        customers.removeIf(c -> c.customerCode.equals(customerCode));
        displayCustomers();
    }

    private void editInvoice() {
        String customerCode = JOptionPane.showInputDialog("Nhập mã KH để chỉnh sửa:");
        for (Customer c : customers) {
            if (c.customerCode.equals(customerCode)) {
                String fullName = JOptionPane.showInputDialog("Nhập tên KH mới:", c.fullName);
                String dateString = JOptionPane.showInputDialog("Nhập ngày mới (dd/MM/yyyy):", new SimpleDateFormat("dd/MM/yyyy").format(c.invoiceDate));
                Date invoiceDate = null;
                try {
                    invoiceDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Ngày không hợp lệ.");
                    return;
                }
                int quantity = Integer.parseInt(JOptionPane.showInputDialog("Nhập số KW mới:", c.quantity));
                double unitPrice = Double.parseDouble(JOptionPane.showInputDialog("Nhập đơn gia mới:", c.unitPrice));

                c.fullName = fullName;
                c.invoiceDate = invoiceDate;
                c.quantity = quantity;
                c.unitPrice = unitPrice;

                if (c instanceof VietnameseCustomer) {
                    VietnameseCustomer vnCustomer = (VietnameseCustomer) c;
                    String[] vnTypes = {"Sinh hoạt", "Kinh doanh", "Sản xuất"};
                    String vnCustomerType = (String) JOptionPane.showInputDialog(this, "Nhập loại KH Việt Nam mới", "Loại KH Việt Nam mới",
                            JOptionPane.QUESTION_MESSAGE, null, vnTypes, vnTypes[0]);
                    int quota = Integer.parseInt(JOptionPane.showInputDialog("Nhập định mức mới:", vnCustomer.quota));
                    vnCustomer.customerType = vnCustomerType;
                    vnCustomer.quota = quota;

                } else if (c instanceof ForeignCustomer) {
                    ForeignCustomer foreignCustomer = (ForeignCustomer) c;
                    String nationality = JOptionPane.showInputDialog("Nhập quốc tịch mới:", foreignCustomer.nationality);
                    foreignCustomer.nationality = nationality;
                }
                break;
            }
        }
        displayCustomers();
    }

    private void calculateTotalQuantity() {
        int vnTotal = 0, foreignTotal = 0;
        for (Customer c : customers) {
            if (c instanceof VietnameseCustomer) {
                vnTotal += c.quantity;
            } else if (c instanceof ForeignCustomer) {
                foreignTotal += c.quantity;
            }
        }
        JOptionPane.showMessageDialog(this, "Tổng số lượng khách hàng Việt Nam: " + vnTotal +
                "\nTổng số lượng khách hàng Việt Nam: " + foreignTotal);
    }

    private void calculateAverageAmount() {
        double totalAmount = 0;
        int count = 0;
        for (Customer c : customers) {
            if (c instanceof ForeignCustomer) {
                totalAmount += c.calculateAmount();
                count++;
            }
        }
        double averageAmount = (count > 0) ? totalAmount / count : 0;
        JOptionPane.showMessageDialog(this, "Trung bình số KW khách nước ngoài sử dụng: " + averageAmount);
    }

    private void outputInvoices() {
        String dateString = JOptionPane.showInputDialog("Nhập tháng và năm (MM/yyyy) để lọc hóa đơn:");
        if (dateString == null || dateString.trim().isEmpty()) return;
        try {
            Date date = new SimpleDateFormat("MM/yyyy").parse(dateString);
            outputArea.setText("");
            for (Customer c : customers) {
                if (new SimpleDateFormat("MM/yyyy").format(c.invoiceDate).equals(dateString)) {
                    outputArea.append(c.customerCode + " " + c.fullName + " " + new SimpleDateFormat("dd/MM/yyyy").format(c.invoiceDate) +
                            " " + c.quantity + " " + c.unitPrice + " " + c.calculateAmount() + "\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Tháng/Năm không hợp lệ.");
        }
    }

    private void searchInvoices() {
        String customerCode = JOptionPane.showInputDialog("Nhập mã KH muốn tìm:");
        outputArea.setText("");
        for (Customer c : customers) {
            if (c.customerCode.equalsIgnoreCase(customerCode)) {
                outputArea.append(c.customerCode + " " + c.fullName + " " + new SimpleDateFormat("dd/MM/yyyy").format(c.invoiceDate) +
                        " " + c.quantity + " " + c.unitPrice + " " + c.calculateAmount() + "\n");
            }
        }
    }

    private void displayCustomers() {
        outputArea.setText("");
        for (Customer c : customers) {
            outputArea.append(c.customerCode + " " + c.fullName + " " + new SimpleDateFormat("dd/MM/yyyy").format(c.invoiceDate) +
                    " " + c.quantity + " " + c.unitPrice + " " + c.calculateAmount() + "\n");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        String url = "jdbc:mysql://localhost:3306/db221401";
        String user = "root";
        String password = "L@mborghini123";
        Connection connection = null;
        Statement statement = null;
        int rowCount = 0;
        //1. Kết nối đến CSDL db221402
        //nạp driver cho app
        Class.forName("com.mysql.cj.jdbc.Driver");

        //tạo kết nối
        connection = DriverManager.getConnection(url,user,password);
        //2. thực thi sql
        SwingUtilities.invokeLater(ElectricityBillManagement::new);
    }
}

