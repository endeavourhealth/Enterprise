package org.endeavour.enterprise.utility;

import com.sun.mail.util.MailLogger;
import org.endeavourhealth.enterprise.core.database.DbAbstractTable;
import org.endeavourhealth.enterprise.core.database.administration.DbEndUser;
import org.endeavourhealth.enterprise.core.database.administration.DbOrganisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import java.util.List;
import java.util.Properties;

public final class EmailProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EmailProvider.class);

    private String url = null;
    private String password = null;
    private String username = null;

    //singleton
    private static EmailProvider ourInstance = new EmailProvider();
    public static EmailProvider getInstance() {
        return ourInstance;
    }

    public void setConnectionProperties(String url, String username, String password) {
        this.url = url;
        this.password = password;
        this.username = username;
    }


    /**
     * sends the invite email for this person
     */
    public void sendInviteEmail(DbEndUser user, DbOrganisation org, String token) {
        String emailTo = user.getEmail();
        emailTo = "drewlittler@hotmail.com";

        String forename = user.getForename();

        //TODO: 2016-02-22 DL - send invite email to the new user

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from-email@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to-email@gmail.com"));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler,"
                    + "\n\n No spam to my email, please!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * sends an email to the person, telling them about the new access that
     * has been added to their account
     */
    public void sendNewAccessGrantedEmail(DbEndUser user, DbOrganisation org, List<DbAbstractTable> toSave) {

        //TODO: 2016-02-22 DL - send email to the user about new acess granted
    }
}
