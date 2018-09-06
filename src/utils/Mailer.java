/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Helper class to process with email
 *
 */
public class Mailer implements Serializable {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties CONFIG;
    private static final String DOMAIN;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String MAILSMTP;
    private static final String MAILPORT;

    // Set email server configuration base on config file
    static {
        CONFIG = new Properties();
        try {
            URL configFile = Mailer.class.getClassLoader().getResource(CONFIG_FILE);
            if (configFile == null) throw new RuntimeException();
            CONFIG.load(configFile.openStream());

            DOMAIN = CONFIG.getProperty("domain");
            USERNAME = CONFIG.getProperty("username");
            PASSWORD = CONFIG.getProperty("password");
            MAILSMTP = CONFIG.getProperty("mailsmtp");
            MAILPORT = CONFIG.getProperty("mailport");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Send email with abve config
    public static void send(String to, String subject, String msg) {

        Properties props = new Properties();
        props.put("mail.smtp.host", MAILSMTP);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", MAILPORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", MAILSMTP);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject, "UTF-8");
            message.setText(msg, "UTF-8", "html");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void sendRecoveryMail(String to, String link) {
        link = "<a href='" + DOMAIN + link + "'>click here</a>";
        String subject = "SNA CALENDAR - RECOVERY PASSWORD";
        String msg = "<h3>Dear,</h3>"
                + "You recently requested to reset your password for SNA Calendar account with this email " + to + ".<br><br>"
                + "<h4>Please " + link + " to reset it.</h4><br>"
                + "If you did not request a password reset, please ignore this email or reply to let us know. "
                + "This password reset is only valid for the next <b>15 minutes</b>.<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendNewAccountMail(String to, String password) {
        String link = "<a href='" + DOMAIN + "'>click here</a>";
        String subject = "SNA CALENDAR - ACCOUNT CREATED";
        String msg = "<h3>Dear,</h3>"
                + "Your SNA Calendar account was created with this login info: <br>"
                + "Email: <b>" + to + "</b><br>"
                + "Password: <b>" + password + "</b>.<br>"
                + "You can login by " + link + ".<br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendChangeActiveAccountMail(String to, boolean active) {
        String subject = active ? "SNA CALENDAR - ACCOUNT UNLOCKED" : "SNA CALENDAR - ACCOUNT LOCKED";
        String msg = "<h3>Dear,</h3>"
                + "Your SNA Calendar account was <b>" + (active ? "unlocked" : "locked") + "</b>.<br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendNewEventCustomerCare(String to, String creator, String title) {
        String link = "<a href='" + DOMAIN + "'>here</a>";
        String subject = "SNA CALENDAR - CUSTOMER CARE EVENT";
        String msg = "<h3>Dear,</h3>"
                + "There is a new customer care event was created<br>"
                + "Creator: <b>Mr/Ms " + creator + "</b>. Title: <b>" + title + "</b><br>"
                + "And you was assigned as an Attendee of this event. <br><br>"
                + "You can check it by login " + link + " for more infomations<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendUpdateEventCustomerCare(String to, String creator, String title) {
        String link = "<a href='" + DOMAIN + "'>here</a>";
        String subject = "SNA CALENDAR - CUSTOMER CARE EVENT";
        String msg = "<h3>Dear,</h3>"
                + "There is a change of a customer care event which you was assgined to.<br>"
                + "Creator: <b>Mr/Ms " + creator + "</b>. Title: <b>" + title + "</b><br><br>"
                + "You can check it by login " + link + " for more infomations<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendNewEventTraining(String to, String creator, String title) {
        String link = "<a href='" + DOMAIN + "'>here</a>";
        String subject = "SNA CALENDAR - TRAINING EVENT";
        String msg = "<h3>Dear,</h3>"
                + "There is a new training event was created<br>"
                + "Creator: <b>Mr/Ms " + creator + "</b>. Title: <b>" + title + "</b><br>"
                + "And you was assigned as an Attendee of this event. <br><br>"
                + "You can check it by login " + link + " for more infomations<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendUpdateEventTraining(String to, String creator, String title) {
        String link = "<a href='" + DOMAIN + "'>here</a>";
        String subject = "SNA CALENDAR - TRAINING EVENT";
        String msg = "<h3>Dear,</h3>"
                + "There is a change of a training event which you was assgined to.<br>"
                + "Creator: <b>Mr/Ms " + creator + "</b>. Title: <b>" + title + "</b><br><br>"
                + "You can check it by login " + link + " for more infomations<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

    public static void sendNewEventAnnualLeave(String to, String creator, String title) {
        String link = "<a href='" + DOMAIN + "'>here</a>";
        String subject = "SNA CALENDAR - ANNUAL LEAVE";
        String msg = "<h3>Dear,</h3>"
                + "There is a new annual leave request of your member.<br>"
                + "Creator: <b>Mr/Ms " + creator + "</b>. Title: <b>" + title + "</b><br><br>"
                + "You can check it by login " + link + " for more infomations<br><br>"
                + "Thanks, <br>SNA Global Team.<br><br>"
                + "<b>P.S</b> If there are any problems or ask a question, please feel free to reply or contact to this email.";
        send(to, subject, msg);
    }

}
