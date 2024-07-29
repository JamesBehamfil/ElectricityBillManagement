// File: dao/InvoiceDAO.java

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Invoice;

public class InvoiceDAO {
    private Connection connection;

    public InvoiceDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "L@mborghini123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM invoices");
            while (rs.next()) {
                invoices.add(new Invoice(
                    rs.getString("customerCode"),
                    rs.getString("fullName"),
                    rs.getDate("invoiceDate"),
                    rs.getString("customerType"),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getDouble("quota"),
                    rs.getString("nationality")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public void addInvoice(Invoice invoice) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO invoices (customerCode, fullName, invoiceDate, customerType, quantity, unitPrice, quota, nationality) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pstmt.setString(1, invoice.getCustomerCode());
            pstmt.setString(2, invoice.getFullName());
            pstmt.setDate(3, new java.sql.Date(invoice.getInvoiceDate().getTime()));
            pstmt.setString(4, invoice.getCustomerType());
            pstmt.setInt(5, invoice.getQuantity());
            pstmt.setDouble(6, invoice.getUnitPrice());
            pstmt.setDouble(7, invoice.getQuota());
            pstmt.setString(8, invoice.getNationality());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInvoice(String customerCode) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM invoices WHERE customerCode = ?");
            pstmt.setString(1, customerCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInvoice(Invoice invoice) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE invoices SET fullName = ?, invoiceDate = ?, customerType = ?, quantity = ?, unitPrice = ?, quota = ?, nationality = ? WHERE customerCode = ?"
            );
            pstmt.setString(1, invoice.getFullName());
            pstmt.setDate(2, new java.sql.Date(invoice.getInvoiceDate().getTime()));
            pstmt.setString(3, invoice.getCustomerType());
            pstmt.setInt(4, invoice.getQuantity());
            pstmt.setDouble(5, invoice.getUnitPrice());
            pstmt.setDouble(6, invoice.getQuota());
            pstmt.setString(7, invoice.getNationality());
            pstmt.setString(8, invoice.getCustomerCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTotalQuantityByType(String customerType) {
        double total = 0;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT SUM(quantity) FROM invoices WHERE customerType = ?");
            pstmt.setString(1, customerType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public double getAverageAmountForForeignCustomers() {
        double average = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT AVG(quantity * unitPrice) FROM invoices WHERE customerType = 'Foreign'");
            if (rs.next()) {
                average = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return average;
    }

    public List<Invoice> getInvoicesByMonth(int month) {
        List<Invoice> invoices = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM invoices WHERE MONTH(invoiceDate) = ?");
            pstmt.setInt(1, month);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                invoices.add(new Invoice(
                    rs.getString("customerCode"),
                    rs.getString("fullName"),
                    rs.getDate("invoiceDate"),
                    rs.getString("customerType"),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getDouble("quota"),
                    rs.getString("nationality")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
}
