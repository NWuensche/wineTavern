package guestbook;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by nwuensche on 20.10.16.
 */
public class EmailTransmitter {

    public static void sendEmail(String name, String comment, String receiver) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

        ctx.register(EmailConfiguration.class);
        ctx.refresh();
        JavaMailSenderImpl mailSender = ctx.getBean(JavaMailSenderImpl.class);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "New message from " + name;
        String message = name + " wrote:\n\n" + comment;

        setUpMessageHelper(mimeMessage, subject, message, receiver);
        mailSender.send(mimeMessage);
    }

    private static void setUpMessageHelper(MimeMessage mime, String subject, String message, String receiver){
        MimeMessageHelper helper = new MimeMessageHelper(mime);

        try {
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(message);
        }
        catch(MessagingException e) {
            e.printStackTrace();
        }
    }
}
