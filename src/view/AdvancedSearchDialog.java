package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class AdvancedSearchDialog extends JDialog {
    private JComboBox<String> cbCategory, cbStatus;
    private JTextField txtMinPrice, txtMaxPrice;
    private JTable resultTable;
    private DefaultTableModel model;

    public AdvancedSearchDialog(Frame parent) {
        super(parent, "Tìm kiếm nâng cao sách", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlFilter = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlFilter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlFilter.add(new JLabel("Thể loại:"));
        cbCategory = new JComboBox<>(new String[]{
            "Tất cả", "Kinh tế - Đối Ngoại", "Công nghệ thông tin", "Điện Tử - Viễn Thông",
            "Điều Khiển - Tự Động Hóa", "Quản Trị Kinh Doanh", "Kế Toán - Tài Chính",
            "An Toàn Thông Tin", "Internet Of Things", "Điện - Điện tử", "Ngoại Ngữ",
            "Công Nghệ Đa Phương Tiện", "Marketing", "Lịch sử Đảng", "Văn học", "Vật lý", "Khác"
        });
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
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                    rs.getString("author"), rs.getInt("quantity"), rs.getDouble("price")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}