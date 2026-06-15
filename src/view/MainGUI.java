package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.sql.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import dao.*;
import model.*;
import util.EmailSender;
import util.DBConnection;
import java.awt.Dimension;

import model.Reader;
import model.Book;

public class MainGUI extends JFrame {

    // PlaceholderTextField
    private static class PlaceholderTextField extends JTextField {
        private String placeholder;
        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setForeground(Color.GRAY);
            setText(placeholder);
            setFont(new Font("Segoe UI", Font.ITALIC, 14));
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setForeground(Color.GRAY);
                        setText(placeholder);
                        setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    }
                }
            });
        }
        public String getRealText() {
            String t = getText();
            return t.equals(placeholder) ? "" : t;
        }
        public void resetPlaceholder() {
            setText(placeholder);
            setForeground(Color.GRAY);
            setFont(new Font("Segoe UI", Font.ITALIC, 14));
        }
    }

    private JTabbedPane tabbedPane;

    // Quản lý sách
    private JTable tableBooks;
    private DefaultTableModel modelBooks;
    private JTextField txtMa, txtTen, txtTacGia, txtNXB, txtGia;
    private PlaceholderTextField txtSearchBook;
    private JComboBox<String> cbTheLoai;
    private JSpinner spSoLuong;
    private BookDAO bookDAO = new BookDAO();

    // Quản lý bạn đọc
    private JTable tableReaders;
    private DefaultTableModel modelReaders;
    private JTextField txtReaderId, txtReaderCode, txtReaderName, txtReaderBirth, txtReaderPhone, txtReaderEmail, txtReaderAddress;
    private JComboBox<String> cbReaderGender;
    private PlaceholderTextField txtSearchReader;
    private ReaderDAO readerDAO = new ReaderDAO();

    // Mượn trả sách
    private JTable tableBorrows;
    private DefaultTableModel modelBorrows;
    private JTextField txtNguoiMuon, txtSdtMuon, txtNgayTra;
    private JComboBox<String> cbReaderForBorrow, cbBookCode;
    private JTextField txtNgayMuon;
    private BorrowDAO borrowDAO = new BorrowDAO();

    // Dashboard
    private JLabel lblTotalBooks, lblTotalReaders, lblTotalBorrowing, lblTotalFine;
    private ChartPanel chartPanel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private PlaceholderTextField txtSearchBorrow;

    public MainGUI() {
        initUI();
        loadAllData();
        updateDashboard();
        updateChart();
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Thư Viện");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(240, 248, 255));

        tabbedPane.addTab("Quản Lý Sách", createBookPanel());
        tabbedPane.addTab("Quản Lý Bạn Đọc", createReaderPanel());
        tabbedPane.addTab("Mượn Trả Sách", createBorrowPanel());
        tabbedPane.addTab("Thống Kê", createDashboardPanel());
        tabbedPane.addTab("Tiện Ích", createUtilityPanel());

        add(tabbedPane);
    }

    // ==================== 1. Quản lý sách ====================
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(createTitledBorder("Thông Tin Sách", new Color(0, 102, 204)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(createLabel("Mã sách (ID):"), gbc);
        gbc.gridx = 1;
        txtMa = createTextField();
        pnlInput.add(txtMa, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Tên sách:"), gbc);
        gbc.gridx = 3;
        txtTen = createTextField();
        pnlInput.add(txtTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlInput.add(createLabel("Thể loại:"), gbc);
        gbc.gridx = 1;
        cbTheLoai = new JComboBox<>(new String[]{"Kinh tế - Đối Ngoại", "Công nghệ thông tin", "Điện Tử - Viễn Thông",
                "Điều Khiển - Tự Động Hóa", "Quản Trị Kinh Doanh", "Kế Toán - Tài Chính", "An Toàn Thông Tin",
                "Internet Of Things", "Điện - Điện tử", "Ngoại Ngữ", "Công Nghệ Đa Phương Tiện", "Marketing",
                "Lịch sử Đảng", "Văn học", "Vật lý", "Khác"});
        cbTheLoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInput.add(cbTheLoai, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Tác giả:"), gbc);
        gbc.gridx = 3;
        txtTacGia = createTextField();
        pnlInput.add(txtTacGia, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlInput.add(createLabel("Nhà xuất bản:"), gbc);
        gbc.gridx = 1;
        txtNXB = createTextField();
        pnlInput.add(txtNXB, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        spSoLuong = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        spSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spSoLuong.getEditor()).getTextField().setBackground(Color.WHITE);
        pnlInput.add(spSoLuong, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlInput.add(createLabel("Giá tiền (VNĐ):"), gbc);
        gbc.gridx = 1;
        txtGia = createTextField();
        pnlInput.add(txtGia, gbc);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBtns.setBackground(new Color(240, 248, 255));
        JButton btnThem = createButton("Thêm mới", new Color(41, 128, 185));
        JButton btnSua = createButton("Sửa", new Color(39, 174, 96));
        JButton btnXoa = createButton("Xóa", new Color(231, 76, 60));
        JButton btnClear = createButton("Làm mới", new Color(149, 165, 166));
        pnlBtns.add(btnThem); pnlBtns.add(btnSua); pnlBtns.add(btnXoa); pnlBtns.add(btnClear);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(new Color(240, 248, 255));
        pnlSearch.add(createLabel("Tìm kiếm:"));
        txtSearchBook = new PlaceholderTextField("Nhập mã sách / tên sách / tác giả");
        txtSearchBook.setPreferredSize(new Dimension(280, 32));
        pnlSearch.add(txtSearchBook);
        JButton btnSearch = createButton("Tìm", new Color(52, 152, 219));
        pnlSearch.add(btnSearch);
        JButton btnAdvanced = createButton("Tìm nâng cao", new Color(52, 152, 219));
        pnlSearch.add(btnAdvanced);

        modelBooks = new DefaultTableModel(new String[]{"ID", "Tên sách", "Thể loại", "Tác giả", "NXB", "SL", "Giá (VNĐ)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableBooks = new JTable(modelBooks);
        styleTable(tableBooks);
        JScrollPane scroll = new JScrollPane(tableBooks);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        JPanel top = new JPanel(new BorderLayout());
        top.add(pnlInput, BorderLayout.NORTH);
        top.add(pnlBtns, BorderLayout.CENTER);
        top.add(pnlSearch, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        tableBooks.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableBooks.getSelectedRow();
                if (row >= 0) {
                    txtMa.setText(modelBooks.getValueAt(row, 0).toString());
                    txtTen.setText(modelBooks.getValueAt(row, 1).toString());
                    cbTheLoai.setSelectedItem(modelBooks.getValueAt(row, 2).toString());
                    txtTacGia.setText(modelBooks.getValueAt(row, 3).toString());
                    txtNXB.setText(modelBooks.getValueAt(row, 4).toString());
                    spSoLuong.setValue(Integer.parseInt(modelBooks.getValueAt(row, 5).toString()));
                    txtGia.setText(modelBooks.getValueAt(row, 6).toString().replace(",", ""));
                }
            }
        });

        btnThem.addActionListener(e -> themSach());
        btnSua.addActionListener(e -> suaSach());
        btnXoa.addActionListener(e -> xoaSach());
        btnClear.addActionListener(e -> { clearBookFields(); txtSearchBook.resetPlaceholder(); loadBookData(); loadBookCombo(); });
        btnSearch.addActionListener(e -> timKiemSach());
        btnAdvanced.addActionListener(e -> new AdvancedSearchDialog(this).setVisible(true));

        loadBookData();
        return panel;
    }

    // ==================== 2. Quản lý bạn đọc ====================
    private JPanel createReaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(createTitledBorder("Thông Tin Bạn Đọc", new Color(0, 102, 204)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã bạn đọc
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(createLabel("Mã bạn đọc:"), gbc);
        gbc.gridx = 1;
        txtReaderCode = createTextField();
        pnlInput.add(txtReaderCode, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Họ tên:"), gbc);
        gbc.gridx = 3;
        txtReaderName = createTextField();
        pnlInput.add(txtReaderName, gbc);

        // Giới tính (ComboBox)
        gbc.gridx = 0; gbc.gridy = 1;
        pnlInput.add(createLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        cbReaderGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cbReaderGender.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInput.add(cbReaderGender, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        txtReaderBirth = createDateTextField();
        pnlInput.add(txtReaderBirth, gbc);

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2;
        pnlInput.add(createLabel("Số điện thoại (10 số):"), gbc);
        gbc.gridx = 1;
        txtReaderPhone = createTextField();
        pnlInput.add(txtReaderPhone, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Email:"), gbc);
        gbc.gridx = 3;
        txtReaderEmail = createTextField();
        pnlInput.add(txtReaderEmail, gbc);

        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = 3;
        pnlInput.add(createLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtReaderAddress = createTextField();
        pnlInput.add(txtReaderAddress, gbc);
        gbc.gridwidth = 1;

        // Ẩn txtReaderId (dùng để lưu ID khi sửa)
        txtReaderId = new JTextField();
        txtReaderId.setVisible(false);
        pnlInput.add(txtReaderId, gbc);

        // Panel nút
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBtns.setBackground(new Color(240, 248, 255));
        JButton btnAdd = createButton("Thêm mới", new Color(41, 128, 185));
        JButton btnUpdate = createButton("Sửa", new Color(39, 174, 96));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnClear = createButton("Làm mới", new Color(149, 165, 166));
        pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnDelete); pnlBtns.add(btnClear);

        // Panel tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(new Color(240, 248, 255));
        pnlSearch.add(createLabel("Tìm kiếm:"));
        txtSearchReader = new PlaceholderTextField("Nhập mã, tên, SĐT");
        txtSearchReader.setPreferredSize(new Dimension(250, 32));
        pnlSearch.add(txtSearchReader);
        JButton btnSearch = createButton("Tìm", new Color(52, 152, 219));
        pnlSearch.add(btnSearch);

        // Bảng
        modelReaders = new DefaultTableModel(new String[]{"ID", "Mã bạn đọc", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Email", "Địa chỉ"}, 0);
        tableReaders = new JTable(modelReaders);
        styleTable(tableReaders);
        JScrollPane scroll = new JScrollPane(tableReaders);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        JPanel top = new JPanel(new BorderLayout());
        top.add(pnlInput, BorderLayout.NORTH);
        top.add(pnlBtns, BorderLayout.CENTER);
        top.add(pnlSearch, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // Sự kiện chọn dòng
        tableReaders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableReaders.getSelectedRow();
                if (row >= 0) {
                    txtReaderId.setText(modelReaders.getValueAt(row, 0).toString());
                    txtReaderCode.setText(modelReaders.getValueAt(row, 1).toString());
                    txtReaderName.setText(modelReaders.getValueAt(row, 2).toString());
                    cbReaderGender.setSelectedItem(modelReaders.getValueAt(row, 3).toString());
                    txtReaderBirth.setText(modelReaders.getValueAt(row, 4).toString());
                    txtReaderPhone.setText(modelReaders.getValueAt(row, 5).toString());
                    txtReaderEmail.setText(modelReaders.getValueAt(row, 6).toString());
                    txtReaderAddress.setText(modelReaders.getValueAt(row, 7).toString());
                }
            }
        });

        // Action Listeners
        btnAdd.addActionListener(e -> themReader());
        btnUpdate.addActionListener(e -> suaReader());
        btnDelete.addActionListener(e -> xoaReader());
        btnClear.addActionListener(e -> {
            clearReaderFields();
            loadReaderData();
            txtSearchReader.resetPlaceholder();
        });
        btnSearch.addActionListener(e -> timKiemReader());

        loadReaderData();
        return panel;
    }

    // ==================== 3. Mượn trả sách ====================
    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(createTitledBorder("Thông Tin Mượn Trả", new Color(230, 126, 34)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(createLabel("Bạn đọc:"), gbc);
        gbc.gridx = 1;
        cbReaderForBorrow = new JComboBox<>();
        cbReaderForBorrow.addActionListener(e -> {
            if (cbReaderForBorrow.getSelectedIndex() >= 0) {
                 try {
                    String selected = cbReaderForBorrow.getSelectedItem().toString();
                    int readerId = Integer.parseInt(
                    selected.split(" - ")[0]);
                     Reader reader = readerDAO.getReaderById(readerId);
                if (reader != null) {
                txtSdtMuon.setText(reader.getPhone());
                } else {
                txtSdtMuon.setText("");
                     }
                } catch (Exception ex) {
                 txtSdtMuon.setText("");
                 }
           }
        });
        cbReaderForBorrow.setPreferredSize(new Dimension(200, 32));
        pnlInput.add(cbReaderForBorrow, gbc);
        cbReaderForBorrow.setPreferredSize(new Dimension(200, 32));
        pnlInput.add(cbReaderForBorrow, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Mã sách:"), gbc);
        gbc.gridx = 3;
        cbBookCode = new JComboBox<>();
        cbBookCode.setPreferredSize(new Dimension(150, 32));
        pnlInput.add(cbBookCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlInput.add(createLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtSdtMuon = createTextField();
        pnlInput.add(txtSdtMuon, gbc);
        gbc.gridx = 2;
        pnlInput.add(createLabel("Ngày mượn:"), gbc);
        gbc.gridx = 3;
        txtNgayMuon = createDateTextField();
        txtNgayMuon.setText(dateFormat.format(new Date()));
        pnlInput.add(txtNgayMuon, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlInput.add(createLabel("Hạn trả (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtNgayTra = createDateTextField();
        pnlInput.add(txtNgayTra, gbc);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBtns.setBackground(new Color(255, 248, 240));
        JButton btnMuon = createButton("Cho mượn", new Color(41, 128, 185));
        JButton btnTra = createButton("Trả sách", new Color(39, 174, 96));
        JButton btnGiaHan = createButton("Gia hạn", new Color(243, 156, 18));
        JButton btnPrint = createButton("In phiếu", new Color(52, 73, 94));
        JButton btnClear = createButton("Làm mới", new Color(149, 165, 166));
        pnlBtns.add(btnMuon); pnlBtns.add(btnTra); pnlBtns.add(btnGiaHan);
        pnlBtns.add(btnPrint); pnlBtns.add(btnClear);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(new Color(255, 248, 240));
        pnlSearch.add(createLabel("Tìm kiếm:"));
        txtSearchBorrow = new PlaceholderTextField("Nhập tên người mượn, mã sách, SĐT");
        txtSearchBorrow.setPreferredSize(new Dimension(280, 32));
        pnlSearch.add(txtSearchBorrow);
        JButton btnSearch = createButton("Tìm", new Color(230, 126, 34));
        pnlSearch.add(btnSearch);

        modelBorrows = new DefaultTableModel(new String[]{"ID", "Người mượn", "Mã sách", "SĐT", "Ngày mượn", "Hạn trả", "Trạng thái", "Tiền phạt"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableBorrows = new JTable(modelBorrows);
        styleTable(tableBorrows);
        tableBorrows.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String status = table.getValueAt(row, 6).toString();
                    if (status.contains("QUÁ HẠN")) c.setBackground(new Color(255, 220, 220));
                    else if (status.contains("CÒN")) c.setBackground(new Color(220, 255, 220));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(tableBorrows);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        JPanel top = new JPanel(new BorderLayout());
        top.add(pnlInput, BorderLayout.NORTH);
        top.add(pnlBtns, BorderLayout.CENTER);
        top.add(pnlSearch, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadReaderCombo();
        loadBookCombo();
        loadBorrowData();

        tableBorrows.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableBorrows.getSelectedRow();
                if (row >= 0) {
                    txtNguoiMuon.setText(modelBorrows.getValueAt(row, 1).toString());
                    String bookCode = modelBorrows.getValueAt(row, 2).toString();
                    cbBookCode.setSelectedItem(bookCode);
                    txtSdtMuon.setText(modelBorrows.getValueAt(row, 3).toString());
                    txtNgayMuon.setText(modelBorrows.getValueAt(row, 4).toString());
                    txtNgayTra.setText(modelBorrows.getValueAt(row, 5).toString());
                }
            }
        });

        btnMuon.addActionListener(e -> muonSach());
        btnTra.addActionListener(e -> traSach());
        btnGiaHan.addActionListener(e -> giaHanSach());
        // SỬA NÚT LÀM MỚI: reset form và tải lại dữ liệu
        btnClear.addActionListener(e -> {
            clearBorrowFields();
            loadBorrowData();
        });
        btnSearch.addActionListener(e -> timKiemBorrow());
        btnPrint.addActionListener(e -> {
            int row = tableBorrows.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(MainGUI.this, "Chọn phiếu mượn để in!"); return; }
            int id = Integer.parseInt(modelBorrows.getValueAt(row, 0).toString());
            Borrow b = borrowDAO.getBorrowById(id);
            String readerName = modelBorrows.getValueAt(row, 1).toString();
            new BorrowPrintDialog(MainGUI.this, b, readerName).setVisible(true);
        });

        return panel;
    }

    // Helper
    private void loadBookCombo() {
        cbBookCode.removeAllItems();
        for (Book b : bookDAO.getAllBooks()) {
            cbBookCode.addItem(String.valueOf(b.getId()));
        }
    }

    private void loadReaderCombo() {
        cbReaderForBorrow.removeAllItems();
        for (Reader r : readerDAO.getAllReaders()) {
            cbReaderForBorrow.addItem(r.getId() + " - " + r.getFullname());
        }
    }

    // ==================== 4. Thống kê ====================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        lblTotalBooks = new JLabel("Tổng số sách: ", SwingConstants.CENTER);
        lblTotalReaders = new JLabel("Tổng bạn đọc: ", SwingConstants.CENTER);
        lblTotalBorrowing = new JLabel("Đang mượn: ", SwingConstants.CENTER);
        lblTotalFine = new JLabel("Tổng phạt: ", SwingConstants.CENTER);
        Font bigFont = new Font("Segoe UI", Font.BOLD, 20);
        Color cardBg = Color.WHITE;
        for (JLabel l : new JLabel[]{lblTotalBooks, lblTotalReaders, lblTotalBorrowing, lblTotalFine}) {
            l.setFont(bigFont);
            l.setOpaque(true);
            l.setBackground(cardBg);
            l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
            ));
        }
        statsPanel.add(lblTotalBooks); statsPanel.add(lblTotalReaders);
        statsPanel.add(lblTotalBorrowing); statsPanel.add(lblTotalFine);

        JButton btnRefresh = createButton("Cập nhật", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> { updateDashboard(); updateChart(); });
        statsPanel.add(btnRefresh);

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        updateChart();

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT DATE_FORMAT(borrow_date, '%Y-%m') as month, COUNT(*) as total " +
                 "FROM borrows GROUP BY month ORDER BY month ASC")) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("total"), "Số sách mượn", rs.getString("month"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart chart = ChartFactory.createBarChart(
            "Thống kê số sách mượn theo tháng", "Tháng", "Số lượng", dataset);
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setMaximumBarWidth(0.1);
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel.setChart(chart);
    }

    // ==================== 5. Tiện ích ====================
    private JPanel createUtilityPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 15, 15));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JButton btnUserMgmt = createUtilityButton("Quản lý người dùng", new Color(52, 152, 219));
        JButton btnChangePass = createUtilityButton("Đổi mật khẩu", new Color(243, 156, 18));
        JButton btnBackup = createUtilityButton("Sao lưu CSDL (SQL)", new Color(46, 204, 113));
        JButton btnSendReminder = createUtilityButton("Gửi email nhắc nhở trả sách", new Color(155, 89, 182));
        JButton btnQRLogin = createUtilityButton("Đăng nhập bằng mã QR", new Color(230, 126, 34));
        panel.add(btnUserMgmt);
        panel.add(btnChangePass);
        panel.add(btnBackup);
        panel.add(btnSendReminder);
        panel.add(btnQRLogin);

        btnUserMgmt.addActionListener(e -> new UserManagementDialog(this).setVisible(true));
        btnChangePass.addActionListener(e -> new ChangePasswordDialog(this, "admin").setVisible(true));
        btnBackup.addActionListener(e -> backupDatabase());
        btnSendReminder.addActionListener(e -> sendReminderEmail());
        btnQRLogin.addActionListener(e -> new QRLoginDialog(this).setVisible(true));
        return panel;
    }

    private JButton createUtilityButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(260, 55));
        return b;
    }

    private void sendReminderEmail() {
        String sql = "SELECT DISTINCT r.email, r.fullname, b.return_date, b.book_code " +
                     "FROM borrows b JOIN readers r ON b.reader_id = r.id " +
                     "WHERE DATEDIFF(b.return_date, CURDATE()) >=0";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                String email = rs.getString("email");
                String name = rs.getString("fullname");
                String returnDate = rs.getString("return_date");
                String bookCode = rs.getString("book_code");
                String subject = "Nhắc nhở trả sách";
                String body = "Kính gửi " + name + ",\nSách có mã " + bookCode + " hạn trả vào ngày " + returnDate + ". Vui lòng trả đúng hạn.";
                EmailSender.sendEmail(email, subject, body);
                count++;
            }
            JOptionPane.showMessageDialog(this, "Đã gửi " + count + " email nhắc nhở!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==================== Các hàm xử lý nghiệp vụ ====================
    private void themSach() {
        try {
            String maStr = txtMa.getText().trim();
            if (maStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sách!"); return; }
            int ma = Integer.parseInt(maStr);
            if (bookDAO.isBookIdExists(ma)) { JOptionPane.showMessageDialog(this, "Mã sách đã tồn tại!"); return; }
            if (txtTen.getText().trim().isEmpty() || txtTacGia.getText().trim().isEmpty() || txtGia.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!"); return;
            }
            double gia = Double.parseDouble(txtGia.getText());
            if (gia <= 0) throw new NumberFormatException();
            Book b = new Book(ma, txtTen.getText(), cbTheLoai.getSelectedItem().toString(),
                    txtTacGia.getText(), txtNXB.getText(), (int) spSoLuong.getValue(), gia);
            if (bookDAO.addBook(b)) { loadBookData(); clearBookFields(); loadBookCombo(); JOptionPane.showMessageDialog(this, "Thêm sách thành công!"); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!"); }
    }

    private void suaSach() {
        if (txtMa.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Chọn sách cần sửa!"); return; }
        try {
            int ma = Integer.parseInt(txtMa.getText());
            double gia = Double.parseDouble(txtGia.getText());
            Book b = new Book(ma, txtTen.getText(), cbTheLoai.getSelectedItem().toString(),
                    txtTacGia.getText(), txtNXB.getText(), (int) spSoLuong.getValue(), gia);
            if (bookDAO.updateBook(b)) { loadBookData(); clearBookFields(); loadBookCombo(); JOptionPane.showMessageDialog(this, "Cập nhật thành công!"); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void xoaSach() {
        if (txtMa.getText().trim().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtMa.getText());
            if (bookDAO.deleteBook(id)) { loadBookData(); clearBookFields(); loadBookCombo(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
        }
    }

    private void timKiemSach() {
        String kw = txtSearchBook.getRealText().trim();
        modelBooks.setRowCount(0);
        if (kw.isEmpty()) loadBookData();
        else {
            List<Book> list = bookDAO.searchBook(kw);
            for (Book b : list) {
                modelBooks.addRow(new Object[]{b.getId(), b.getTitle(), b.getCategory(),
                        b.getAuthor(), b.getPublisher(), b.getQuantity(), String.format("%,.0f", b.getPrice())});
            }
            if (list.isEmpty()) JOptionPane.showMessageDialog(this, "Không tìm thấy sách!");
        }
    }

    private void clearBookFields() {
        txtMa.setText(""); txtTen.setText(""); txtTacGia.setText("");
        txtNXB.setText(""); txtGia.setText(""); spSoLuong.setValue(1);
        tableBooks.clearSelection();
    }

    // ==================== Xử lý bạn đọc ====================
    private void themReader() {
        if (txtReaderCode.getText().trim().isEmpty() || txtReaderName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã và tên bạn đọc không được để trống!");
            return;
        }
        String phone = txtReaderPhone.getText().trim();
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm đúng 10 chữ số!");
            return;
        }
        Reader r = new Reader(
            txtReaderCode.getText().trim(),
            txtReaderName.getText().trim(),
            cbReaderGender.getSelectedItem().toString(),
            txtReaderBirth.getText().trim(),
            phone,
            txtReaderEmail.getText().trim(),
            txtReaderAddress.getText().trim()
        );
        if (readerDAO.addReader(r)) {
            loadReaderData();
            clearReaderFields();
            JOptionPane.showMessageDialog(this, "Thêm bạn đọc thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm bạn đọc thất bại!");
        }
    }

    private void suaReader() {
        if (txtReaderId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bạn đọc cần sửa!");
            return;
        }
        String phone = txtReaderPhone.getText().trim();
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm đúng 10 chữ số!");
            return;
        }
        int id = Integer.parseInt(txtReaderId.getText());
        Reader r = new Reader(
            id,
            txtReaderCode.getText().trim(),
            txtReaderName.getText().trim(),
            cbReaderGender.getSelectedItem().toString(),
            txtReaderBirth.getText().trim(),
            phone,
            txtReaderEmail.getText().trim(),
            txtReaderAddress.getText().trim()
        );
        if (readerDAO.updateReader(r)) {
            loadReaderData();
            clearReaderFields();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaReader() {
        if (txtReaderId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bạn đọc cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bạn đọc này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtReaderId.getText());
            if (readerDAO.deleteReader(id)) {
                loadReaderData();
                clearReaderFields();
                JOptionPane.showMessageDialog(this, "Xóa bạn đọc thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }

    private void timKiemReader() {
        String keyword = txtSearchReader.getRealText().trim();
        modelReaders.setRowCount(0);
        if (keyword.isEmpty()) {
            loadReaderData();
        } else {
            for (Reader r : readerDAO.searchReader(keyword)) {
                modelReaders.addRow(new Object[]{
                    r.getId(), r.getReaderCode(), r.getFullname(),
                    r.getGender(), r.getBirthday(), r.getPhone(),
                    r.getEmail(), r.getAddress()
                });
            }
            if (modelReaders.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bạn đọc nào!");
            }
        }
        tableReaders.clearSelection();
    }

    private void clearReaderFields() {
        txtReaderId.setText("");
        txtReaderCode.setText("");
        txtReaderName.setText("");
        cbReaderGender.setSelectedIndex(0);
        txtReaderBirth.setText("");
        txtReaderPhone.setText("");
        txtReaderEmail.setText("");
        txtReaderAddress.setText("");
        tableReaders.clearSelection();
        txtSearchReader.resetPlaceholder();
    }

    private void loadReaderData() {
        modelReaders.setRowCount(0);
        for (Reader r : readerDAO.getAllReaders()) {
            modelReaders.addRow(new Object[]{
                r.getId(), r.getReaderCode(), r.getFullname(),
                r.getGender(), r.getBirthday(), r.getPhone(),
                r.getEmail(), r.getAddress()
            });
        }
    }

    // ==================== Xử lý mượn trả ====================
    private boolean kiemTraSoLuongSach(String maSach) {
        try {
            int id = Integer.parseInt(maSach);
            Book b = bookDAO.getBookById(id);
            if (b == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy sách!"); return false; }
            if (b.getQuantity() <= 0) { JOptionPane.showMessageDialog(this, "Sách đã hết!"); return false; }
            return true;
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Mã sách không hợp lệ!"); return false; }
    }

    private void capNhatSoLuongSach(String maSach, int delta) {
        try {
            int id = Integer.parseInt(maSach);
            Book b = bookDAO.getBookById(id);
            if (b != null) bookDAO.updateQuantity(id, b.getQuantity() + delta);
        } catch (Exception e) {}
    }

    private void muonSach() {
        if (cbBookCode.getSelectedItem() == null || txtNgayTra.getText().trim().isEmpty() || txtNgayMuon.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ thông tin!"); return;
        }
        String maSach = cbBookCode.getSelectedItem().toString();
        if (!kiemTraSoLuongSach(maSach)) return;
        int readerId = 0;
        if (cbReaderForBorrow.getSelectedIndex() >= 0) {
            String selected = cbReaderForBorrow.getSelectedItem().toString();
            readerId = Integer.parseInt(selected.split(" - ")[0]);
        }
        Borrow b = new Borrow(
                cbReaderForBorrow.getSelectedItem().toString().split(" - ")[1],
                txtSdtMuon.getText(), txtNgayMuon.getText(), txtNgayTra.getText(), maSach);
        b.setReaderId(readerId);
        if (borrowDAO.addBorrow(b)) {
            capNhatSoLuongSach(maSach, -1);
            JOptionPane.showMessageDialog(this, "Mượn sách thành công!");
            loadBorrowData(); loadBookData(); clearBorrowFields();
        } else JOptionPane.showMessageDialog(this, "Mượn sách thất bại!");
    }

    // SỬA PHƯƠNG THỨC TRẢ SÁCH: cập nhật thay vì xóa
    private void traSach() {
        int row = tableBorrows.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn phiếu mượn cần trả!");
            return;
        }
        int id = Integer.parseInt(modelBorrows.getValueAt(row, 0).toString());
        String maSach = modelBorrows.getValueAt(row, 2).toString();
        String hanTra = modelBorrows.getValueAt(row, 5).toString();
        String ngayTraThuc = dateFormat.format(new Date());
        double fine = borrowDAO.calculateFine(hanTra, ngayTraThuc);
        if (fine > 0) {
            int cf = JOptionPane.showConfirmDialog(this, "Sách trả muộn, phạt " + String.format("%,.0f", fine) + " VND. Tiếp tục?", "Phạt", JOptionPane.YES_NO_OPTION);
            if (cf != JOptionPane.YES_OPTION) return;
        }
        Borrow b = borrowDAO.getBorrowById(id);
        if (b != null) {
            b.setFine(fine);
            b.setReturnDate(ngayTraThuc);
            if (borrowDAO.updateBorrow(b)) {
                capNhatSoLuongSach(maSach, +1);
                JOptionPane.showMessageDialog(this, "Trả sách thành công!" + (fine > 0 ? " - Phạt " + String.format("%,.0f", fine) : ""));
                loadBorrowData();
                loadBookData();
            } else {
                JOptionPane.showMessageDialog(this, "Trả sách thất bại!");
            }
        }
    }

    private void giaHanSach() {
        int row = tableBorrows.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn phiếu mượn cần gia hạn!"); return; }
        String newDate = JOptionPane.showInputDialog(this, "Nhập ngày trả mới (YYYY-MM-DD):", modelBorrows.getValueAt(row, 5).toString());
        if (newDate != null && !newDate.trim().isEmpty()) {
            try {
                java.sql.Date.valueOf(newDate);
                int id = Integer.parseInt(modelBorrows.getValueAt(row, 0).toString());
                String borrower = modelBorrows.getValueAt(row, 1).toString();
                String phone = modelBorrows.getValueAt(row, 3).toString();
                String borrowDate = modelBorrows.getValueAt(row, 4).toString();
                String bookCode = modelBorrows.getValueAt(row, 2).toString();
                Borrow b = new Borrow(id, borrower, phone, borrowDate, newDate, bookCode);
                b.setReaderId(0);
                if (borrowDAO.updateBorrow(b)) {
                    loadBorrowData();
                    JOptionPane.showMessageDialog(this, "Gia hạn thành công! Hạn trả mới: " + newDate);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!"); }
        }
    }

    private void timKiemBorrow() {
        String kw = txtSearchBorrow.getRealText().trim().toLowerCase();
        modelBorrows.setRowCount(0);
        if (kw.isEmpty()) loadBorrowData();
        else {
            for (Borrow b : borrowDAO.getAllBorrows()) {
                if (b.getBorrowerName().toLowerCase().contains(kw) ||
                    b.getBookCode().toLowerCase().contains(kw) ||
                    b.getPhoneNumber().contains(kw)) {
                    String fineText = (b.getFine() > 0) ? String.format("%,.0f VND", b.getFine()) : "0 VND";
                    modelBorrows.addRow(new Object[]{
                        b.getId(), b.getBorrowerName(), b.getBookCode(), b.getPhoneNumber(),
                        b.getBorrowDate(), b.getReturnDate(), getStatus(b.getReturnDate()), fineText
                    });
                }
            }
        }
    }

    private void clearBorrowFields() {
         cbReaderForBorrow.setSelectedIndex(-1);
         cbBookCode.setSelectedIndex(-1);
         txtSdtMuon.setText("");
         txtNgayMuon.setText(dateFormat.format(new Date())); 
         txtNgayTra.setText("");
         txtSearchBorrow.setText("");
         tableBorrows.clearSelection(); 
     }

    private String getStatus(String returnDate) {
        try {
            Date today = new Date();
            Date ret = java.sql.Date.valueOf(returnDate);
            if (ret.before(today)) return "QUÁ HẠN";
            else {
                long diff = ret.getTime() - today.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                return "CÒN " + days + " NGÀY";
            }
        } catch (Exception e) { return "KHÔNG RÕ"; }
    }

    private void updateDashboard() {
        int totalBooks = bookDAO.getAllBooks().size();
        int totalReaders = readerDAO.getAllReaders().size();
        int borrowing = borrowDAO.getAllBorrows().size();
        double totalFine = 0;
        for (Borrow b : borrowDAO.getAllBorrows()) {
         totalFine += tinhTienPhat(b.getReturnDate()); 
        }
        lblTotalBooks.setText("Tổng số sách: " + totalBooks);
        lblTotalReaders.setText("Tổng bạn đọc: " + totalReaders);
        lblTotalBorrowing.setText("Đang mượn: " + borrowing);
        lblTotalFine.setText("Tổng phạt: " + String.format("%,.0f VND", totalFine));
    }

    private void backupDatabase() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter out = new PrintWriter(chooser.getSelectedFile())) {
                out.println("-- Backup generated on " + new Date());
                out.println("SET FOREIGN_KEY_CHECKS=0;");
                out.println("DROP TABLE IF EXISTS books;");
                out.println("CREATE TABLE books LIKE QuanLyThuVien.books;");
                for (Book b : bookDAO.getAllBooks()) {
                    out.println(String.format("INSERT INTO books VALUES (%d, '%s', '%s', '%s', '%s', %d, %f);",
                            b.getId(), escape(b.getTitle()), escape(b.getCategory()), escape(b.getAuthor()),
                            escape(b.getPublisher()), b.getQuantity(), b.getPrice()));
                }
                out.println("DROP TABLE IF EXISTS readers;");
                out.println("CREATE TABLE readers LIKE QuanLyThuVien.readers;");
                for (Reader r : readerDAO.getAllReaders()) {
                    out.println(String.format("INSERT INTO readers VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                            r.getId(), escape(r.getReaderCode()), escape(r.getFullname()), escape(r.getGender()),
                            r.getBirthday(), escape(r.getPhone()), escape(r.getEmail()), escape(r.getAddress())));
                }
                out.println("DROP TABLE IF EXISTS borrows;");
                out.println("CREATE TABLE borrows LIKE QuanLyThuVien.borrows;");
                for (Borrow b : borrowDAO.getAllBorrows()) {
                    out.println(String.format("INSERT INTO borrows VALUES (%d, '%s', '%s', '%s', '%s', '%s', %d, %f);",
                            b.getId(), escape(b.getBorrowerName()), escape(b.getPhoneNumber()),
                            b.getBorrowDate(), b.getReturnDate(), escape(b.getBookCode()), b.getReaderId(), b.getFine()));
                }
                out.println("SET FOREIGN_KEY_CHECKS=1;");
                JOptionPane.showMessageDialog(this, "Sao lưu thành công!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi sao lưu: " + e.getMessage()); }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'");
    }

    private void loadAllData() { loadBookData(); loadReaderData(); loadBorrowData(); }

    private void loadBookData() {
        modelBooks.setRowCount(0);
        for (Book b : bookDAO.getAllBooks()) {
            modelBooks.addRow(new Object[]{b.getId(), b.getTitle(), b.getCategory(),
                    b.getAuthor(), b.getPublisher(), b.getQuantity(), String.format("%,.0f", b.getPrice())});
        }
    }
    private double tinhTienPhat(String hanTraStr) {
         try {
              Date hanTra = dateFormat.parse(hanTraStr);
             Date now = new Date();

             long diff = now.getTime() - hanTra.getTime();
             long days = diff / (1000 * 60 * 60 * 24);

             if (days > 0) {
                  return days * 5000;
            }
         } catch (Exception e) {
             e.printStackTrace();
        }
          return 0;
    }
    private void loadBorrowData() {
        modelBorrows.setRowCount(0);
        for (Borrow b : borrowDAO.getAllBorrows()) {

             double fine = tinhTienPhat(b.getReturnDate());
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
             String fineText = (fine > 0) ? nf.format(fine) + " VND" : "0 VND";          
              modelBorrows.addRow(new Object[]{
                b.getId(), b.getBorrowerName(), b.getBookCode(), b.getPhoneNumber(),
                b.getBorrowDate(), b.getReturnDate(), getStatus(b.getReturnDate()), fineText
            });
        }
    }

    // ==================== Các Dialog Inner Class ====================
    private class UserManagementDialog extends JDialog {
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtUsername, txtFullname, txtNewPass;
        private JComboBox<String> cbRole;
        public UserManagementDialog(Frame parent) {
            super(parent, "Quản lý người dùng", true);
            setSize(600, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            model = new DefaultTableModel(new String[]{"ID", "Username", "Họ tên", "Role"}, 0);
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);
            JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 10));
            pnlForm.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            pnlForm.add(new JLabel("Username:"));
            txtUsername = new JTextField();
            pnlForm.add(txtUsername);
            pnlForm.add(new JLabel("Họ tên:"));
            txtFullname = new JTextField();
            pnlForm.add(txtFullname);
            pnlForm.add(new JLabel("Mật khẩu mới:"));
            txtNewPass = new JTextField();
            pnlForm.add(txtNewPass);
            pnlForm.add(new JLabel("Role:"));
            cbRole = new JComboBox<>(new String[]{"admin", "librarian"});
            pnlForm.add(cbRole);
            add(pnlForm, BorderLayout.NORTH);
            JPanel pnlBtns = new JPanel();
            JButton btnAdd = new JButton("Thêm");
            JButton btnUpdate = new JButton("Cập nhật");
            JButton btnDelete = new JButton("Xóa");
            JButton btnRefresh = new JButton("Làm mới");
            pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnDelete); pnlBtns.add(btnRefresh);
            add(pnlBtns, BorderLayout.SOUTH);
            loadUsers();
            btnAdd.addActionListener(e -> addUser());
            btnUpdate.addActionListener(e -> updateUser());
            btnDelete.addActionListener(e -> deleteUser());
            btnRefresh.addActionListener(e -> loadUsers());
            table.getSelectionModel().addListSelectionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtUsername.setText(model.getValueAt(row, 1).toString());
                    txtFullname.setText(model.getValueAt(row, 2).toString());
                    cbRole.setSelectedItem(model.getValueAt(row, 3).toString());
                }
            });
        }
        private void loadUsers() {
            model.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, username, fullname, role FROM users")) {
                while (rs.next())
                    model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            } catch (Exception e) { e.printStackTrace(); }
        }
        private void addUser() {
            String user = txtUsername.getText().trim();
            String full = txtFullname.getText().trim();
            String pass = txtNewPass.getText().trim();
            String role = cbRole.getSelectedItem().toString();
            if (user.isEmpty() || full.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
                return;
            }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, fullname, role) VALUES (?,?,?,?)")) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.setString(3, full);
                ps.setString(4, role);
                ps.executeUpdate();
                loadUsers();
                clearForm();
            } catch (Exception e) { e.printStackTrace(); }
        }
        private void updateUser() {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            String full = txtFullname.getText().trim();
            String role = cbRole.getSelectedItem().toString();
            String newPass = txtNewPass.getText().trim();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE users SET fullname=?, role=?" + (newPass.isEmpty() ? "" : ", password=?") + " WHERE id=?")) {
                ps.setString(1, full);
                ps.setString(2, role);
                if (!newPass.isEmpty()) ps.setString(3, newPass);
                ps.setInt(newPass.isEmpty() ? 3 : 4, id);
                ps.executeUpdate();
                loadUsers();
                clearForm();
            } catch (Exception e) { e.printStackTrace(); }
        }
        private void deleteUser() {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa người dùng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    loadUsers();
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        private void clearForm() {
            txtUsername.setText("");
            txtFullname.setText("");
            txtNewPass.setText("");
            cbRole.setSelectedIndex(0);
        }
    }

    private class ChangePasswordDialog extends JDialog {
        private JPasswordField txtOld, txtNew1, txtNew2;
        private String username;
        public ChangePasswordDialog(Frame parent, String username) {
            super(parent, "Đổi mật khẩu", true);
            this.username = username;
            setSize(400, 250);
            setLocationRelativeTo(parent);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10,10,10,10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx=0; gbc.gridy=0; add(new JLabel("Mật khẩu cũ:"), gbc);
            gbc.gridx=1; txtOld = new JPasswordField(15); add(txtOld, gbc);
            gbc.gridx=0; gbc.gridy=1; add(new JLabel("Mật khẩu mới:"), gbc);
            gbc.gridx=1; txtNew1 = new JPasswordField(15); add(txtNew1, gbc);
            gbc.gridx=0; gbc.gridy=2; add(new JLabel("Xác nhận mới:"), gbc);
            gbc.gridx=1; txtNew2 = new JPasswordField(15); add(txtNew2, gbc);
            JButton btnChange = new JButton("Đổi mật khẩu");
            gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
            add(btnChange, gbc);
            btnChange.addActionListener(e -> changePassword());
        }
        private void changePassword() {
            String oldPass = new String(txtOld.getPassword()).trim();
            String newPass = new String(txtNew1.getPassword()).trim();
            String confirm = new String(txtNew2.getPassword()).trim();
            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ!");
                return;
            }
            if (!newPass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp!");
                return;
            }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getString("password").equals(oldPass)) {
                    PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET password=? WHERE username=?");
                    ps2.setString(1, newPass);
                    ps2.setString(2, username);
                    ps2.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private class AdvancedSearchDialog extends JDialog {
        private JComboBox<String> cbCategory, cbStatus;
        private JTextField txtMinPrice, txtMaxPrice;
        private JTable resultTable;
        private DefaultTableModel model;
        public AdvancedSearchDialog(Frame parent) {
            super(parent, "Tìm kiếm nâng cao sách", true);
            setSize(800, 600);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            JPanel pnlFilter = new JPanel(new GridLayout(4,2,10,10));
            pnlFilter.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            pnlFilter.add(new JLabel("Thể loại:"));
            cbCategory = new JComboBox<>(new String[]{"Tất cả","Kinh tế - Đối Ngoại","Công nghệ thông tin","Điện Tử - Viễn Thông","Điều Khiển - Tự Động Hóa","Quản Trị Kinh Doanh","Kế Toán - Tài Chính","An Toàn Thông Tin","Internet Of Things","Điện - Điện tử","Ngoại Ngữ","Công Nghệ Đa Phương Tiện","Marketing","Lịch sử Đảng","Văn học","Vật lý","Khác"});
            pnlFilter.add(cbCategory);
            pnlFilter.add(new JLabel("Khoảng giá (VNĐ):"));
            JPanel pricePanel = new JPanel(new FlowLayout());
            txtMinPrice = new JTextField(10);
            txtMaxPrice = new JTextField(10);
            pricePanel.add(new JLabel("Từ"));
            pricePanel.add(txtMinPrice);
            pricePanel.add(new JLabel("đến"));
            pricePanel.add(txtMaxPrice);
            pnlFilter.add(pricePanel);
            pnlFilter.add(new JLabel("Trạng thái:"));
            cbStatus = new JComboBox<>(new String[]{"Tất cả", "Còn sách", "Hết sách"});
            pnlFilter.add(cbStatus);
            JButton btnSearch = new JButton("Tìm kiếm");
            pnlFilter.add(btnSearch);
            add(pnlFilter, BorderLayout.NORTH);
            model = new DefaultTableModel(new String[]{"ID", "Tên sách", "Thể loại", "Tác giả", "SL", "Giá"}, 0);
            resultTable = new JTable(model);
            add(new JScrollPane(resultTable), BorderLayout.CENTER);
            btnSearch.addActionListener(e -> search());
        }
        private void search() {
            model.setRowCount(0);
            StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
            if (!cbCategory.getSelectedItem().equals("Tất cả"))
                sql.append(" AND category = '").append(cbCategory.getSelectedItem()).append("'");
            if (!txtMinPrice.getText().trim().isEmpty())
                sql.append(" AND price >= ").append(Double.parseDouble(txtMinPrice.getText().trim()));
            if (!txtMaxPrice.getText().trim().isEmpty())
                sql.append(" AND price <= ").append(Double.parseDouble(txtMaxPrice.getText().trim()));
            if (cbStatus.getSelectedItem().equals("Còn sách"))
                sql.append(" AND quantity > 0");
            else if (cbStatus.getSelectedItem().equals("Hết sách"))
                sql.append(" AND quantity = 0");
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql.toString())) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                            rs.getString("author"), rs.getInt("quantity"), rs.getDouble("price")});
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private class BorrowPrintDialog extends JDialog implements Printable {
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
                try { job.print(); } catch (PrinterException e) { e.printStackTrace(); }
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

    private class QRLoginDialog extends JDialog {
        private JLabel lblResult;
        public QRLoginDialog(Frame parent) {
            super(parent, "Đăng nhập bằng mã QR", true);
            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            JButton btnSelect = new JButton("Chọn ảnh QR");
            lblResult = new JLabel("Chưa quét", SwingConstants.CENTER);
            add(btnSelect, BorderLayout.NORTH);
            add(lblResult, BorderLayout.CENTER);
            btnSelect.addActionListener(e -> selectQRImage());
        }
        private void selectQRImage() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Result result = new MultiFormatReader().decode(bitmap);
                    String text = result.getText();
                    if (text.contains(":")) {
                        String[] parts = text.split(":");
                        String user = parts[0];
                        String pass = parts[1];
                        if (new UserDAO().login(user, pass) != null) {
                            lblResult.setText("Đăng nhập thành công: " + user);
                            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                            dispose();
                        } else {
                            lblResult.setText("Sai thông tin");
                        }
                    } else {
                        lblResult.setText("QR không hợp lệ");
                    }
                } catch (Exception ex) { ex.printStackTrace(); lblResult.setText("Lỗi đọc QR"); }
            }
        }
    }

    // Helper UI
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setPreferredSize(new Dimension(180, 34));
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 40));
        return b;
    }

    private TitledBorder createTitledBorder(String title, Color color) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 2),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                color);
        border.setTitleColor(color);
        return border;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(52, 73, 94));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
    }

    private JTextField createDateTextField() {
        JTextField tf = new JTextField(15) {
            protected void processKeyEvent(java.awt.event.KeyEvent e) {
                if (e.getID() == java.awt.event.KeyEvent.KEY_TYPED) {
                    char ch = e.getKeyChar();
                    if (!Character.isDigit(ch) && ch != '-') { e.consume(); return; }
                    String cur = getText();
                    int pos = getCaretPosition();
                    if (cur.length() >= 10) { e.consume(); return; }
                    if (pos == 4 && cur.length() == 4) { setText(cur + "-"); setCaretPosition(5); }
                    else if (pos == 7 && cur.length() == 7) { setText(cur + "-"); setCaretPosition(8); }
                }
                super.processKeyEvent(e);
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }
}