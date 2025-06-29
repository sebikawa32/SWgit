package com.example.lambda;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendMail(
            String to,
            String subject,
            String bodyHtml,
            String username,
            String password
    ) throws MessagingException {
        // SMTP 서버 고정 (구글 기준)
        final String smtpHost = "smtp.gmail.com";
        final String smtpPort = "465";

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(bodyHtml, "text/html; charset=utf-8");

        Transport.send(msg);
    }
}
