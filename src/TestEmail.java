import util.EmailSender;

public class TestEmail {

    public static void main(String[] args) {

        EmailSender.sendEmail(
                "duymanh20004@gmail.com",
                "Test Email",
                "Xin chào từ Java"
        );

    }
}