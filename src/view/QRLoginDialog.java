package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import dao.UserDAO;
import model.User;

public class QRLoginDialog extends JDialog {

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
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Image files",
                        "png",
                        "jpg",
                        "jpeg"));

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            try {
                BufferedImage image =
                        ImageIO.read(chooser.getSelectedFile());

                LuminanceSource source =
                        new BufferedImageLuminanceSource(image);

                BinaryBitmap bitmap =
                        new BinaryBitmap(
                                new HybridBinarizer(source));

                Result result =
                        new MultiFormatReader().decode(bitmap);

                String text = result.getText();

                if (text.contains(":")) {

                    String[] parts = text.split(":");
                    String user = parts[0];
                    String pass = parts[1];

                    User loggedUser = new UserDAO().login(user, pass);

                    if (loggedUser != null) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Đăng nhập thành công!");

                        new MainGUI(loggedUser).setVisible(true);

                        if (getOwner() != null) {
                            getOwner().dispose();
                        }

                        dispose();

                    } else {
                        lblResult.setText("Sai thông tin");
                    }

                } else {
                    lblResult.setText("QR không hợp lệ");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                lblResult.setText("Lỗi đọc QR");
            }
        }
    }
}