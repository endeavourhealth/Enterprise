package org.endeavour.enterprise.email;

import org.endeavour.enterprise.framework.config.models.Template;
import org.endeavourhealth.enterprise.core.database.models.EnduserEntity;
import org.endeavourhealth.enterprise.core.database.models.OrganisationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import java.util.*;

public final class EmailProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EmailProvider.class);

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

    private Template findTemplate(EmailTemplateUse use) {
        for (Template template: templates) {
            if (template.getUse().equalsIgnoreCase(use.toString())) {
                return template;
            }
        }
        return null;
    }

    private boolean sendEmail(EmailTemplateUse use, HashMap<EmailTemplateParameter, String> parameters) {

        Template t = findTemplate(use);
        if (t == null) {
            return false;
        }

        String htmlBody = t.getHtmlBody();
        String sendFrom = t.getSendFrom();
        String subject = t.getSubject();
        String sendTo = parameters.get(EmailTemplateParameter.EMAIL_TO);

        //replace parameters in the email body with values
        Iterator it=parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<EmailTemplateParameter, String> entry = (Map.Entry<EmailTemplateParameter, String>)it.next();
            EmailTemplateParameter key = entry.getKey();
            String value = entry.getValue();
            htmlBody = htmlBody.replace(key.toString(), value);
        }

        //SSL
        Properties props = new Properties();
        props.put("mail.smtp.host", url);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //TLS
        /*Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
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
            if (sendFrom != null) {
                message.setFrom(new InternetAddress("from-email@gmail.com"));
            }
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            LOG.error("Error sending email to {}", sendTo);
            LOG.error("Exception", e);
            return false;
        }
    }

    private HashMap<EmailTemplateParameter, String> buildEmailParameters(EnduserEntity user, OrganisationEntity org, String token) {

        String email = user.getEmail();
        String title = user.getTitle();
        String forename = user.getForename();
        String surname = user.getSurname();
        String orgName = org.getName();
        String orgId = org.getNationalid();

        HashMap<EmailTemplateParameter, String> ret = new HashMap<>();
        ret.put(EmailTemplateParameter.EMAIL_TO, email);
        ret.put(EmailTemplateParameter.TOKEN, token);
        ret.put(EmailTemplateParameter.TITLE, title);
        ret.put(EmailTemplateParameter.FORENAME, forename);
        ret.put(EmailTemplateParameter.SURNAME, surname);
        ret.put(EmailTemplateParameter.ORGANISATION_NAME, orgName);
        ret.put(EmailTemplateParameter.ORGANISATION_ID, orgId);

        return ret;
    }


    public boolean sendInviteEmail(EnduserEntity user, OrganisationEntity org, String token) {
        HashMap<EmailTemplateParameter, String> parameters = buildEmailParameters(user, org, token);
        return sendEmail(EmailTemplateUse.INVITATION, parameters);
    }
    public boolean sendPasswordResetEmail(EnduserEntity user, OrganisationEntity org, String token) {
        HashMap<EmailTemplateParameter, String> parameters = buildEmailParameters(user, org, token);
        return sendEmail(EmailTemplateUse.PASSWORD_RESET, parameters);
    }
    public boolean sendNewAccessGrantedEmail(EnduserEntity user, OrganisationEntity org) {
        HashMap<EmailTemplateParameter, String> parameters = buildEmailParameters(user, org, "");
        return sendEmail(EmailTemplateUse.NEW_ORGANISATION, parameters);
    }

}
