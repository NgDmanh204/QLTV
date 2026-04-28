package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.sql.*;

public class EmailSender {
    private static String host, username, password, from;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM email_config LIMIT 1")) {
            if (rs.next()) {
                host = rs.getString("host");
                username = rs.getString("username");
                password = rs.getString("password");
                from = rs.getString("from_email");
                System.out.println("Loaded email config for: " + username);
            } else {
                System.err.println("No email config found in database!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String to, String subject, String body) {
        if (host == null || username == null || password == null) {
            System.err.println("Email not configured.");
            return;
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("Email sent to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}