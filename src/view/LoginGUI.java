package view;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import model.User;

public class LoginGUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserDAO userDAO = new UserDAO();

    public LoginGUI() {
        setTitle("Đăng nhập - Hệ thống Quản lý Thư viện");
        setSize(450, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Hệ thống Quản lý Thư viện");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(createLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        panel.add(txtUsername, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(createLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        panel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(120, 38));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);
        gbc.gridy = 4;
        JButton btnQRLogin = new JButton("Đăng nhập bằng QR");
        btnQRLogin.setBackground(new Color(39, 174, 96));
        btnQRLogin.setForeground(Color.WHITE);
        btnQRLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQRLogin.setFocusPainted(false);
        btnQRLogin.setBorderPainted(false);
        btnQRLogin.setOpaque(true);
        btnQRLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQRLogin.setPreferredSize(new Dimension(120, 38));
        panel.add(btnQRLogin, gbc);

        add(panel);
        btnLogin.addActionListener(e -> login());
        btnQRLogin.addActionListener(e -> {
        QRLoginDialog qrDialog = new QRLoginDialog(this);
        qrDialog.setVisible(true);
        });
        getRootPane().setDefaultButton(btnLogin);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    private void login() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User loggedUser = userDAO.login(user, pass);
        if (loggedUser != null) {
            JOptionPane.showMessageDialog(this, "Chào mừng " + loggedUser.getFullname() + "!");
            new MainGUI().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}