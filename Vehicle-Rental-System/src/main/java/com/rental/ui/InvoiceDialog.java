package com.rental.ui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Premium Printable Invoice Display Dialog Component.
 * Bypasses local frame buttons to send pristine canvas tables directly to OS Spoolers.
 * * @author Pathum Srinath
 */
public class InvoiceDialog extends JDialog {

    private JPanel printablePanel;
    private String bookingId;
    private String customerName;
    private String vehicleName;
    private int rentalDays;
    private double ratePerDay;
    private double totalAmount;

    public InvoiceDialog(Frame owner, String bookingId, String customerName, String vehicleName, int rentalDays, double ratePerDay, double totalAmount) {
        super(owner, "System Invoice Generator", true);
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.vehicleName = vehicleName;
        this.rentalDays = rentalDays;
        this.ratePerDay = ratePerDay;
        this.totalAmount = totalAmount;

        initComponents();
    }

    private void initComponents() {
        setSize(450, 600);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        getContentPane().setLayout(new BorderLayout());

        // --- 1. THE PRINTABLE RECEIPT CANVAS ---
        printablePanel = new JPanel();
        printablePanel.setBackground(Color.WHITE);
        printablePanel.setLayout(new BoxLayout(printablePanel, BoxLayout.Y_AXIS));
        printablePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Corporate Branding Elements
        JLabel lblCompany = new JLabel("DRIVEFLOW RENTALS");
        lblCompany.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCompany.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCompany.setForeground(new Color(15, 23, 42));

        JLabel lblTagline = new JLabel("Premium Vehicle Rental Management System");
        lblTagline.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTagline.setForeground(new Color(100, 116, 139));

        JLabel lblDivider1 = new JLabel("-------------------------------------------------------------------------");
        lblDivider1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDivider1.setForeground(Color.LIGHT_GRAY);

        // Meta Transaction Details Grid
        JPanel metaPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        metaPanel.setBackground(Color.WHITE);
        metaPanel.setMaximumSize(new Dimension(400, 50));
        
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        metaPanel.add(new JLabel("Invoice No: #INV-" + bookingId));
        metaPanel.add(new JLabel("Date: " + currentDate, JLabel.RIGHT));
        metaPanel.add(new JLabel("Customer: " + customerName));
        metaPanel.add(new JLabel("Status: PAID", JLabel.RIGHT));

        JLabel lblDivider2 = new JLabel("-------------------------------------------------------------------------");
        lblDivider2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDivider2.setForeground(Color.LIGHT_GRAY);

        // Ledger Pricing Calculations Table Layout
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Section Form headers
        gbc.gridy = 0;
        gbc.gridx = 0; tablePanel.add(createBoldLabel("Description"), gbc);
        gbc.gridx = 1; tablePanel.add(createBoldLabel("Rate"), gbc);
        gbc.gridx = 2; tablePanel.add(createBoldLabel("Days"), gbc);
        gbc.gridx = 3; tablePanel.add(createBoldLabel("Total"), gbc);

        // Core dynamic metrics values insertion mapping
        gbc.gridy = 1;
        gbc.gridx = 0; tablePanel.add(new JLabel(vehicleName), gbc);
        gbc.gridx = 1; tablePanel.add(new JLabel("LKR " + String.format("%.2f", ratePerDay)), gbc);
        gbc.gridx = 2; tablePanel.add(new JLabel(String.valueOf(rentalDays)), gbc);
        gbc.gridx = 3; tablePanel.add(new JLabel("LKR " + String.format("%.2f", totalAmount)), gbc);

        JLabel lblDivider3 = new JLabel("-------------------------------------------------------------------------");
        lblDivider3.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDivider3.setForeground(Color.LIGHT_GRAY);

        // Bottom Accounting Line
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setMaximumSize(new Dimension(400, 40));
        JLabel lblTotalText = new JLabel("Grand Total:", JLabel.LEFT);
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lblTotalVal = new JLabel("LKR " + String.format("%.2f", totalAmount), JLabel.RIGHT);
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalVal.setForeground(new Color(37, 99, 235));
        summaryPanel.add(lblTotalText, BorderLayout.WEST);
        summaryPanel.add(lblTotalVal, BorderLayout.EAST);

        JLabel lblFooter = new JLabel("Thank you for riding with us!");
        lblFooter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFooter.setForeground(new Color(71, 85, 105));

        // Assemble Canvas Area layout
        printablePanel.add(lblCompany);
        printablePanel.add(Box.createVerticalStrut(4));
        printablePanel.add(lblTagline);
        printablePanel.add(Box.createVerticalStrut(10));
        printablePanel.add(lblDivider1);
        printablePanel.add(metaPanel);
        printablePanel.add(lblDivider2);
        printablePanel.add(tablePanel);
        printablePanel.add(Box.createVerticalGlue());
        printablePanel.add(lblDivider3);
        printablePanel.add(summaryPanel);
        printablePanel.add(Box.createVerticalStrut(30));
        printablePanel.add(lblFooter);

        JScrollPane scrollPane = new JScrollPane(printablePanel);
        scrollPane.setBorder(null);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // --- 2. CONTROL HUD PANEL INTERFACE (NOT PRINTED ON EXPORT) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(241, 245, 249));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnPrint = new JButton("Print / Save as PDF");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.setBackground(new Color(37, 99, 235));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Functional Button Trigger Listeners
        btnClose.addActionListener(e -> dispose());
        btnPrint.addActionListener(e -> spoolPrintJob());
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void spoolPrintJob() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("DriveFlow_Invoice_" + bookingId);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Scale system down uniformly to print layout size definitions safely on A4 dimensions
                double scaleX = pageFormat.getImageableWidth() / printablePanel.getWidth();
                double scale = Math.min(scaleX, 1.0);
                g2d.scale(scale, scale);

                printablePanel.printAll(g2d);
                return Printable.PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Document spooled to printer successfully.", "Print Job Broadcast", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print Error: " + ex.getMessage(), "Spooling Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}