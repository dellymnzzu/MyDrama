package com.MyDrama.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final String senderEmail = "fudnr4010@naver.com";
    private final String ePw = createKey();

    public MimeMessage createMessage(String to) throws MessagingException {
        System.out.println("보내는 대상 : "+to);
        System.out.println("인증번호 : "+ePw);
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.addRecipients(Message.RecipientType.TO, to); // 보내는 대상
        message.setSubject("MyDrama 회원 가입 이메일 인증");

        String msg = "";
        msg += "<div style='margin:20px;'>";
        msg += "<h1> 안녕하세요 MyDrama 입니다. </h1>";
        msg += "<br>";
        msg += "<p>아래 코드를 복사해 입력해주세요.<p>";
        msg += "<br>";
        msg += "<p>감사합니다</p>";
        msg += "<br>";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "CODE: <strong>";
        msg += ePw + "</strong><div><br/>";
        msg += "</div>";
        message.setText(msg, "utf-8", "html"); //내용
        return message;


    }

    public static String createKey(){
        StringBuffer key = new StringBuffer();
        Random r = new Random();

        for (int i = 0; i < 8; i++) {
            int index = r.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) ((int) r.nextInt(26) + 97));//a~z
                    break;
                case 1:
                    key.append((char) ((int) (r.nextInt(26)) + 65));//A~Z
                    break;
                case 2:
                    key.append(r.nextInt(10));//0~9
                    break;
            }
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to) throws MessagingException{
        System.out.println("이메일 to : "+ to);
    MimeMessage message = createMessage(to);
        System.out.println("메일 제목: " + message.getSubject());
        System.out.println("받는 사람: " + Arrays.toString(message.getAllRecipients()));
        System.out.println("보낸 사람: " + Arrays.toString(message.getFrom()));
        System.out.println("javaMailSender : "+ javaMailSender);
        if(javaMailSender==null){
            throw new IllegalStateException("null이 아닙니다. Bean이 정상작동하는지 확인해봐");
        }
    try{
        javaMailSender.send(message);
        System.out.println("성공");
    }catch (MailException es){
        es.printStackTrace();
        throw new IllegalArgumentException();
    }
    return ePw;
    }
    public void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // UTF-8 인코딩 설정

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom("dpsgkdlvms5031@gmail.com");

            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

