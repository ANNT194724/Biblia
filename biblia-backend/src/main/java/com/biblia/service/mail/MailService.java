package com.biblia.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService implements EmailSender{

    @Value("${biblia.app.base_url}")
    private String baseURL;

    @Autowired
    JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    public void sendVerificationEmail(String to, String token, String username) {
        Thread thread = new Thread(() -> {
            String emailContent = "<html>"
                    + "<head>"
                    + "<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<div class=\"jumbotron\">"
                    + "<h4 class=\"display-4\">Dear [name],</h4>"
                    + "<p class=\"lead\">Please click the link below to verify your registration:</p>"
                    + "<a class=\"btn btn-primary btn-lg\" href=\"[URL]\" role=\"button\">VERIFY</a>"
                    + "</div>"
                    + "<p class=\"text-muted\">Thank you,</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            emailContent = emailContent.replace("[name]", username);
            String url = baseURL + "user/verify?code=" + token + "&login_id=" + to;
            emailContent = emailContent.replace("[URL]", url);
            send(to, emailContent);
        });
        thread.start();
    }

    public void sendResetPasswordEmail(String to, String username, String token) {
        Thread thread = new Thread(() -> {
            String emailContent = "<html>"
                    + "<head>"
                    + "<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<div class=\"jumbotron\">"
                    + "<h4 class=\"display-4\">Dear [name],</h4>"
                    + "<p class=\"lead\">Mã xác nhận để đặt lại mật khẩu của bạn là: [token]<br>Mã xác nhận sẽ hết hạn sau 15 phút.</p>"
                    + "</div>"
                    + "<p class=\"text-muted\">Thank you,</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            emailContent = emailContent.replace("[name]", username);
            emailContent = emailContent.replace("[token]", token);
            send(to, emailContent);
        });
        thread.start();
    }
}
