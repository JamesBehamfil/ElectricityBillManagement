// File: service/InvoiceService.java

package service;

import dao.InvoiceDAO;
import model.Invoice;

import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO;

    public InvoiceService() {
        invoiceDAO = new InvoiceDAO();
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    public void addInvoice(Invoice invoice) {
        invoice.setAmount(calculateAmount(invoice));
        invoiceDAO.addInvoice(invoice);
    }

    public void deleteInvoice(String customerCode) {
        invoiceDAO.deleteInvoice(customerCode);
    }

    public void updateInvoice(Invoice invoice) {
        invoice.setAmount(calculateAmount(invoice));
        invoiceDAO.updateInvoice(invoice);
    }

    public double getTotalQuantityByType(String customerType) {
        return invoiceDAO.getTotalQuantityByType(customerType);
    }

    public double getAverageAmountForForeignCustomers() {
        return invoiceDAO.getAverageAmountForForeignCustomers();
    }

    public List<Invoice> getInvoicesByMonth(int month) {
        return invoiceDAO.getInvoicesByMonth(month);
    }

    private double calculateAmount(Invoice invoice) {
        double amount = 0;
        if (invoice.getCustomerType().equals("American")) {
            if (invoice.getQuantity() <= invoice.getQuota()) {
                amount = invoice.getQuantity() * invoice.getUnitPrice();
            } else {
                amount = invoice.getQuota() * invoice.getUnitPrice()
                        + (invoice.getQuantity() - invoice.getQuota()) * invoice.getUnitPrice() * 2.5;
            }
        } else if (invoice.getCustomerType().equals("Foreign")) {
            amount = invoice.getQuantity() * invoice.getUnitPrice();
        }
        return amount;
    }
}
