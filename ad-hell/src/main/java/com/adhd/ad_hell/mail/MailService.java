package com.adhd.ad_hell.mail;

import jakarta.mail.internet.MimeMessage;

public interface MailService {

  MimeMessage createMessage(String toEmail, String receiverName, MailType type, String content);
  void sendMail(String toEmail, String receiverName, MailType type, String content);

}
