package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class UserManagementDialog extends JDialog {
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
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        pnlBtns.add(btnAdd);
        pnlBtns.add(btnUpdate);
        pnlBtns.add(btnDelete);
        pnlBtns.add(btnRefresh);
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
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        String full = txtFullname.getText().trim();
        String role = cbRole.getSelectedItem().toString();
        String newPass = txtNewPass.getText().trim();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE users SET fullname=?, role=?" + (newPass.isEmpty() ? "" : ", password=?") + " WHERE id=?")) {
            ps.setString(1, full);
            ps.setString(2, role);
            if (!newPass.isEmpty()) ps.setString(3, newPass);
            ps.setInt(newPass.isEmpty() ? 3 : 4, id);
            ps.executeUpdate();
            loadUsers();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtFullname.setText("");
        txtNewPass.setText("");
        cbRole.setSelectedIndex(0);
    }
}