import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;

public class EmailSender {

    private String USERNAME;
    private String PASSWORD;
    private String EMAIL;
    private String HOST;
    private int PORT;

    private Session session;

    private class SMTPAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthenticator() {
            return new PasswordAuthentication(USERNAME, PASSWORD);
        }
    }

    public void connect(String username, String passwd, String email,
                               String host, int port) {

        USERNAME = username;  // goldberg.chain.reaction
        PASSWORD = passwd;  // n0n0n0n0
        EMAIL = email;  // goldberg.chain.reaction@gmail.com
        HOST = host;  // smtp.gmail.com
        PORT = port;  // 465

        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.store.protocol", "imaps");
        session = Session.getInstance(props, new SMTPAuthenticator());

    }

    /* Content must be already templated */
    public void send(String to, String subject, String content)
        throws Exception {
        MimeMessage msg = new MimeMessage(session);

        InternetAddress sender = new InternetAddress(EMAIL);
        msg.setSender(sender);

        InternetAddress[] recipients = new InternetAddress[] {
            new InternetAddress(to)
        };
        msg.setRecipients(Message.RecipientType.TO, recipients);

        msg.setSubject(subject);
        msg.setContent(content, "text/plain");
        Transport.send(msg);
    }

}
