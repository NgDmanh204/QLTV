package view;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import model.Borrow;

public class BorrowPrintDialog extends JDialog implements Printable {
    private Borrow borrow;
    private String readerName;

    public BorrowPrintDialog(Frame parent, Borrow b, String readerName) {
        super(parent, "In phiếu mượn", true);
        this.borrow = b;
        this.readerName = readerName;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        JButton btnPrint = new JButton("In");
        btnPrint.addActionListener(e -> print());
        add(btnPrint, BorderLayout.CENTER);
    }

    private void print() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
        dispose();
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("PHIẾU MƯỢN SÁCH", 100, 50);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Người mượn: " + readerName, 50, 100);
        g2d.drawString("Mã sách: " + borrow.getBookCode(), 50, 130);
        g2d.drawString("Số điện thoại: " + borrow.getPhoneNumber(), 50, 160);
        g2d.drawString("Ngày mượn: " + borrow.getBorrowDate(), 50, 190);
        g2d.drawString("Hạn trả: " + borrow.getReturnDate(), 50, 220);
        return PAGE_EXISTS;
    }
}