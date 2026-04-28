package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField txtOld, txtNew1, txtNew2;
    private String username;

    public ChangePasswordDialog(Frame parent, String username) {
        super(parent, "Đổi mật khẩu", true);
        this.username = username;
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridx = 1;
        txtOld = new JPasswordField(15);
        add(txtOld, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        txtNew1 = new JPasswordField(15);
        add(txtNew1, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Xác nhận mới:"), gbc);
        gbc.gridx = 1;
        txtNew2 = new JPasswordField(15);
        add(txtNew2, gbc);

        JButton btnChange = new JButton("Đổi mật khẩu");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}