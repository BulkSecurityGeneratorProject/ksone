package org.crossfit.app.service;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.crossfit.app.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Inject
    private Environment env;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;
    
    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.trace("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);

        if (System.getenv("SENDGRID_USERNAME") != null && System.getenv("SENDGRID_PASSWORD") != null){
            try {
                SendGrid sendgrid = new SendGrid(System.getenv("SENDGRID_USERNAME"), System.getenv("SENDGRID_PASSWORD"));

                SendGrid.Email email = new SendGrid.Email();

                email.addTo(to);
                email.setFrom(from);
                email.setSubject(subject);
                if (isHtml){
                    email.setHtml(content);
                }
                else{
                    email.setText(content);
                }
                
				SendGrid.Response response = sendgrid.send(email);
                log.debug("Sent e-mail to User '{}'. SendGrid.Response:'{}'", to, response.getMessage());
			} catch (SendGridException e) {
                log.warn("E-mail could not be sent to user '{}' with SendGrid, exception is: {}", to, e.getMessage());
			}
        }
        else{

            // Prepare message using a Spring helper
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setText(content, isHtml);
                javaMailSender.send(mimeMessage);
                log.debug("Sent e-mail to User '{}'", to);
            } catch (Exception e) {
                log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
            }
        }
        
    }

    @Async
    public void sendActivationEmail(Member member, String clearPassword) {
        log.debug("Sending activation e-mail to '{}'", member.getLogin());
        Locale locale = Locale.forLanguageTag(member.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", member);
        context.setVariable("clearPassword", clearPassword);
        context.setVariable("box", member.getBox());
        String content = templateEngine.process("activationCompte", context);
        String subject = messageSource.getMessage("email.creation.title", new Object[]{member.getBox().getName()}, locale);
        sendEmail(member.getLogin(), subject, content, false, true);
    }
}
