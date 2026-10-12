package server;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

public class MailOut {

    Properties prop;
    Session session;
    Transport transport;
    String recipientEmail;

    String content;

    public MailOut(String recipientEmail,String content) throws Exception {
        this.recipientEmail = recipientEmail;

        prop = new Properties();
        prop.setProperty("mail.smtp.host", "smtp.qq.com");
        prop.setProperty("mail.smtp.port", "587"); // 使用587端口，启用STARTTLS加密
        prop.setProperty("mail.smtp.auth", "true");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("1551095653@qq.com", "rtuuvzuzglrrffdh");
            }
        });

        session.setDebug(true); // 开启调试信息

        transport = session.getTransport("smtp");
        transport.connect();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("1551095653@qq.com")); // 发件人邮箱
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail)); // 设置收件人邮箱
        message.setSubject("Java Test邮件"); // 设置邮件主题

        // 创建一个包含HTML内容的MimeBodyPart
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent("<h1>"+content+"</h1>", "text/html; charset=UTF-8");

        // 创建一个MimeMultipart对象
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);

        // 将Multipart对象设置为整个邮件的内容
        message.setContent(multipart);

        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients());

        // 关闭连接
        transport.close();
    }
}
