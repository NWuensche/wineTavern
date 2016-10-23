package guestbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * A configuration to manage emails with Gmail
 * You have to make the sending email
 *
 * @author Niklas Wünsche
 */
@Configuration
public class EmailConfiguration {

    @Bean
    public JavaMailSenderImpl implementMail(){
        JavaMailSenderImpl mailTransmitter = new JavaMailSenderImpl();
        Properties properties = mailTransmitter.getJavaMailProperties();
        SensibleData sensible = new SensibleDataImpl();

        mailTransmitter.setPort(587);
        mailTransmitter.setHost("smtp.gmail.com");
        mailTransmitter.setUsername(sensible.getEmailTransmitter());
        mailTransmitter.setPassword(sensible.getPassword());

        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");

        return mailTransmitter;
    }
}
