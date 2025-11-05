package com.adhd.ad_hell.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;

  @SneakyThrows
  @Override
  public MimeMessage createMessage(String toEmail, String receiverName, MailType type, String content) {
    MimeMessage message = mailSender.createMimeMessage();

    message.addRecipients(RecipientType.TO, toEmail);
    message.setSubject("[ADHD] " + type.getTitle());

    // 공통 메일 HTML 템플릿
    StringBuilder msg = new StringBuilder();
    msg.append("<div style='margin:50px; font-family:sans-serif;'>")
       .append("<h2 style='color:#2b7de9;'>ADHD 메일 서비스</h2>")
       .append("<p>안녕하세요, <strong>").append(receiverName).append("</strong> 님.</p>")
       .append("<hr style='border:0;border-top:1px solid #eee;'>");

    // 타입별 콘텐츠
    switch (type) {
      case VERIFICATION -> {
        msg.append("<p>이메일 인증을 위해 아래 코드를 입력해주세요.</p>")
           .append("<div style='padding:10px 20px; background-color:#f1f1f1; border-radius:6px;'>")
           .append("<h3>인증 코드: <strong style='color:#333;'>").append(content).append("</strong></h3>")
           .append("</div>");
      }
      case REWARD -> {
        msg.append("<p>축하드립니다! 요청하신 경품 코드는 아래와 같습니다.</p>")
           .append("<div style='padding:10px 20px; background-color:#f1f1f1; border-radius:6px;'>")
           .append("<h3>경품 코드: <strong style='color:#2b7de9;'>").append(content).append("</strong></h3>")
           .append("</div>");
      }
    }

    msg.append("<hr style='border:0;border-top:1px solid #eee;'>")
       .append("<p style='font-size:13px; color:gray;'>본 메일은 발신 전용입니다.<br>문의: support@adhd.com</p>")
       .append("</div>");

    message.setText(msg.toString(), "utf-8", "html");
    message.setFrom(new InternetAddress("noreply@adhd.com", "ADHD 관리자"));

    return message;
  }

  @Override
  public void sendMail(String toEmail, String receiverName, MailType type, String content) {
    try {
      MimeMessage message = createMessage(toEmail, receiverName, type, content);
      mailSender.send(message);
    } catch (Exception e) {
      throw new RuntimeException("메일 전송 실패: " + e.getMessage(), e);
    }
  }
}
