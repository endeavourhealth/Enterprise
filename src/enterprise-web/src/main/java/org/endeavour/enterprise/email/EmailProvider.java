package org.endeavour.enterprise.email;

import org.endeavour.enterprise.framework.config.models.Template;
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public final class EmailProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EmailProvider.class);

    private static final String USE_INVITATION = "invitation";
    private static final String USE_PASSWORD_RESET = "passwordReset";
    private static final String USE_NEW_ORGANISATION = "newOrganisation";

    private static final String PARAMETER_TOKEN = "[Token]";
    private static final String PARAMETER_TITLE = "[Title]";
    private static final String PARAMETER_FORENAME = "[Forename]";
    private static final String PARAMETER_SURTNAME = "[Surname]";
    private static final String PARAMETER_ORGANISATION_NAME = "[OrganisationName]";
    private static final String PARAMETER_ORGANISATION_ID = "[OrganisationId]";

    private String url = null;
    private String password = null;
    private String username = null;
    private List<Template> templates = null;

    //singleton
    private static EmailProvider ourInstance = new EmailProvider();
    public static EmailProvider getInstance() {
        return ourInstance;
    }

    public void setConnectionProperties(String url, String username, String password, List<Template> templates) {
        this.url = url;
        this.password = password;
        this.username = username;
        this.templates = templates;
    }

    private Template findTemplate(String use) {
        for (Template template: templates) {
            if (template.getUse().equalsIgnoreCase(use)) {
                return template;
            }
        }
        return null;
    }

    private boolean sendEmail(String use, String recipient, HashMap<String, String> parameters) {

        Template t = findTemplate(USE_INVITATION);
        if (t == null) {
            return false;
        }
    }

    /**
     * sends the invite email for this person
     */
    public boolean sendInviteEmail(DbEndUser user, DbOrganisation org, String token) {






        String emailTo = user.getEmail();
        emailTo = "drewlittler@hotmail.com";

        String forename = user.getForename();

        //TODO: 2016-02-22 DL - send invite email to the new user

        Properties props = new Properties();

        //SSL
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //TLS
        /*props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");*/

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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
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
