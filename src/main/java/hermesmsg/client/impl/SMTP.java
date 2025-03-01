package hermesmsg.client.impl;

import hermesmsg.client.IMessageClient;
import hermesmsg.util.Constant;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

public class SMTP implements IMessageClient, Constant {
    static Logger logger = LoggerFactory.getLogger(SMTP.class);

    private String from = null;
    private String displayName = null;
    private Session session = null;

    @Override
    public IMessageClient initClient(Properties props) {
        final String authUser = props.getProperty("mail.smtp.user");
        final String authPassword = props.getProperty("mail.extra.secret");
        from = props.getProperty("mail.smtp.from", authUser);
        displayName = props.getProperty("mail.extra.displayname");

        Properties mailProps = new Properties();
        props.keySet().stream().map(Object::toString).filter(key -> key.startsWith("mail.smtp")).forEach(key -> {
            mailProps.put(key, props.getProperty(key));
        });

        Authenticator auth = null;
        if ("true".equals(mailProps.getProperty("mail.smtp.auth"))) {
            auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUser, authPassword);
                }
            };
        }
        this.session = Session.getInstance(mailProps, auth);
        return this;
    }

    private MimeBodyPart parseAttachment(JSONObject joAttachment) {
        try {
            MimeBodyPart mbp = new MimeBodyPart();
            ByteArrayDataSource bads = new ByteArrayDataSource(
                    Base64.getDecoder().decode(joAttachment.getString(MESSAGE_KEY_ATTACHMENTS_DATA)),
                    joAttachment.getString(MESSAGE_KEY_ATTACHMENTS_CONTENT_TYPE)
            );
            mbp.setDataHandler(new DataHandler(bads));
            mbp.setFileName(joAttachment.getString(MESSAGE_KEY_ATTACHMENTS_NAME));
            return mbp;
        } catch (Exception e) {
            logger.error("[PARSE][ATTACHMENT]", e);
        }
        return null;
    }

    private InternetAddress parseRecipientInternetAddress(JSONObject joRecipient) {
        try {
            String rEmail = null;
            String rDisplayName = null;
            if (joRecipient.has(MESSAGE_KEY_EMAIL)) {
                JSONObject joEmail = joRecipient.getJSONObject(MESSAGE_KEY_EMAIL);
                if (joEmail.has(MESSAGE_KEY_EMAIL_ADDRESS)) {
                    rEmail = joEmail.getString(MESSAGE_KEY_EMAIL_ADDRESS);
                    rDisplayName = joEmail.has(MESSAGE_KEY_EMAIL_NAME) ? joEmail.getString(MESSAGE_KEY_EMAIL_NAME) : null;
                    return new InternetAddress(rEmail, rDisplayName);
                }
            }
        } catch (Exception e) {
            logger.error("[PARSE][RECIPIENT]", e);
        }
        return null;
    }

    @Override
    public void send(String queueMsgContentStr) {
        this.send(new JSONObject(queueMsgContentStr));
    }

    @Override
    public void send(JSONObject queueMsgContentJO) {
        try {
            Message simpleMessage = new MimeMessage(session);
            // from
            {
                String fromEmail = from;
                String fromDisplayName = displayName;
                if (queueMsgContentJO.has(MESSAGE_KEY_FROM)) {
                    JSONObject joFrom = queueMsgContentJO.getJSONObject(MESSAGE_KEY_FROM);
                    if (joFrom.has(MESSAGE_KEY_EMAIL_ADDRESS)) {
                        fromEmail = joFrom.getString(MESSAGE_KEY_EMAIL_ADDRESS);
                    }
                    if (joFrom.has(MESSAGE_KEY_EMAIL_NAME)) {
                        fromDisplayName = joFrom.getString(MESSAGE_KEY_EMAIL_NAME);
                    }
                }
                simpleMessage.setFrom(new InternetAddress(fromEmail, fromDisplayName));
            }
            // subject
            {
                simpleMessage.setSubject(queueMsgContentJO.getString(MESSAGE_KEY_SUBJECT));
            }
            // body (content)
            {
                JSONObject joBody = queueMsgContentJO.getJSONObject(MESSAGE_KEY_BODY);
                String msgContentType = (joBody.has(MESSAGE_KEY_BODY_CONTENT_TYPE) && MESSAGE_VALUE_BODY_CONTENT_TYPE_HTML.equals(joBody.getString(MESSAGE_KEY_BODY_CONTENT_TYPE))) ? MESSAGE_CONTENT_TYPE_HTML : MESSAGE_CONTENT_TYPE_TEXT;
                if (queueMsgContentJO.has(MESSAGE_KEY_ATTACHMENTS)) {
                    Multipart mp = new MimeMultipart();
                    {
                        MimeBodyPart mbp = new MimeBodyPart();
                        mbp.setContent(joBody.getString(MESSAGE_KEY_BODY_CONTENT), msgContentType);
                        mp.addBodyPart(mbp);
                    }
                    queueMsgContentJO.getJSONArray(MESSAGE_KEY_ATTACHMENTS).forEach(jsonAttachment -> {
                        Optional.ofNullable(parseAttachment((JSONObject) jsonAttachment))
                                .map(mbp -> {
                                    try {
                                        mp.addBodyPart(mbp);
                                    } catch (Exception e) {
                                        logger.error("[SEND][ADD_BODYPART]", e);
                                    }
                                    return null;
                                });
                    });
                    simpleMessage.setContent(mp);
                } else {
                    simpleMessage.setHeader("Content-type", msgContentType + "; charset=utf-8");
                    simpleMessage.setContent(joBody.getString(MESSAGE_KEY_BODY_CONTENT), msgContentType);
                }
            }
            // recipient to
            if (queueMsgContentJO.has(MESSAGE_KEY_TO_RECIPIENTS)) {
                queueMsgContentJO.getJSONArray(MESSAGE_KEY_TO_RECIPIENTS).forEach(oRecipient -> {
                    try {
                        simpleMessage.addRecipient(Message.RecipientType.TO, parseRecipientInternetAddress((JSONObject) oRecipient));
                    } catch (Exception e) {
                        logger.error("[SEND][ADD_TO]", e);
                    }
                });
            }
            // recipient cc
            if (queueMsgContentJO.has(MESSAGE_KEY_CC_RECIPIENTS)) {
                queueMsgContentJO.getJSONArray(MESSAGE_KEY_CC_RECIPIENTS).forEach(oRecipient -> {
                    try {
                        simpleMessage.addRecipient(Message.RecipientType.CC, parseRecipientInternetAddress((JSONObject) oRecipient));
                    } catch (Exception e) {
                        logger.error("[PARSE][ADD_CC]", e);
                    }
                });
            }
            // recipient bcc
            if (queueMsgContentJO.has(MESSAGE_KEY_BCC_RECIPIENTS)) {
                queueMsgContentJO.getJSONArray(MESSAGE_KEY_BCC_RECIPIENTS).forEach(oRecipient -> {
                    try {
                        simpleMessage.addRecipient(Message.RecipientType.BCC, parseRecipientInternetAddress((JSONObject) oRecipient));
                    } catch (Exception e) {
                        logger.error("[SEND][ADD_BCC]", e);
                    }
                });
            }
            // send
            Transport.send(simpleMessage);
        } catch (Exception e) {
            logger.error("[SEND][TRANSPORT]", e);
        }
    }
}
