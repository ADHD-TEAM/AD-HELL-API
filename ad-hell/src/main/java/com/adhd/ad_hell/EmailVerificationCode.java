package com.adhd.ad_hell;

import java.util.Random;

public class EmailVerificationCode {

    private static final int CODE_LENGTH = 12;

    public static String getCode() {
        StringBuffer key = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) { // 인증코드 12자리
            int index = random.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (random.nextInt(26)) + 97));
                    // a~z (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (random.nextInt(26)) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((random.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }
}