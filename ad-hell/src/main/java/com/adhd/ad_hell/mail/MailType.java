package com.adhd.ad_hell.mail;

public enum MailType {
  VERIFICATION("이메일 인증"),
  REWARD("경품 코드 안내");

  private final String title;

  MailType(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
}
