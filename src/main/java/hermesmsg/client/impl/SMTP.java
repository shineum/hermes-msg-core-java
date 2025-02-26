package hermesmsg.client.impl;

import hermesmsg.client.IMessageClient;
import hermesmsg.util.Constant;
import hermesmsg.util.MessageConverter;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

public class SMTP implements IMessageClient, Constant {
    private String authUser = null;
    private String authPassword = null;
    private String from = null;
    private String displayName = null;
    private Properties mailProps = null;
    private Session session = null;

    @Override
    public IMessageClient initClient(Properties props) {
        authUser = props.getProperty("mail.smtp.user");
        authPassword = props.getProperty("mail.extra.secret");
        from = props.getProperty("mail.smtp.from", authUser);
        displayName = props.getProperty("mail.extra.displayname");

        mailProps = new Properties();
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

    @Override
    public void send(String msg, JSONObject options) {
        boolean useCompress = false;
        if (options != null && options.has("useCompress")) {
            useCompress = options.getBoolean("useCompress");
        }
        try {
            JSONObject jo = MessageConverter.parseEmailMessage(msg, useCompress);
            Message simpleMessage = new MimeMessage(session);
            if (jo.has(MESSAGE_KEY_FROM)) {
                JSONObject pJo = MessageConverter.parseEmailAddressObject(jo.getJSONObject(MESSAGE_KEY_FROM));
                System.out.println(pJo.toString());
                simpleMessage.setFrom(new InternetAddress((pJo == null) ? from : pJo.getString(MESSAGE_KEY_EMAIL_ADDRESS), (pJo == null || pJo.isNull(MESSAGE_KEY_EMAIL_NAME)) ? displayName : pJo.getString(MESSAGE_KEY_EMAIL_NAME)));
            }

            simpleMessage.setSubject(jo.getString(MESSAGE_KEY_SUBJECT));
            {
                JSONObject joBody = jo.getJSONObject(MESSAGE_KEY_BODY);
                String msgContentType = (joBody.has(MESSAGE_KEY_BODY_CONTENT_TYPE) && "html".equals(joBody.getString(MESSAGE_KEY_BODY_CONTENT_TYPE))) ? "text/html" : "text/plain";
                if (jo.has(MESSAGE_KEY_ATTACHMENTS)) {
                    // multipart
                    Multipart mp = new MimeMultipart();
                    {
                        MimeBodyPart mbp = new MimeBodyPart();
                        mbp.setContent(joBody.getString(MESSAGE_KEY_BODY_CONTENT), msgContentType);
                        mp.addBodyPart(mbp);
                    }
                    jo.getJSONArray(MESSAGE_KEY_ATTACHMENTS).forEach(po -> {
                        try {
                            JSONObject pJo = (JSONObject) po;
                            MimeBodyPart mbp = new MimeBodyPart();
                            byte[] attachmentBinary = Base64.getDecoder().decode(pJo.getString(MESSAGE_KEY_ATTACHMENTS_DATA));
                            ByteArrayDataSource bads = new ByteArrayDataSource(attachmentBinary, pJo.getString(MESSAGE_KEY_ATTACHMENTS_CONTENT_TYPE));
                            mbp.setDataHandler(new DataHandler(bads));
                            mbp.setFileName(pJo.getString(MESSAGE_KEY_ATTACHMENTS_NAME));
                            mp.addBodyPart(mbp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    simpleMessage.setContent(mp);
                } else {
                    simpleMessage.setHeader("Content-type", msgContentType + "; charset=utf-8");
                    simpleMessage.setContent(joBody.getString(MESSAGE_KEY_BODY_CONTENT), msgContentType);
                }
            }

            if (jo.has(MESSAGE_KEY_TO)) {
                Optional.ofNullable(jo.getJSONArray(MESSAGE_KEY_TO)).map(pJA -> {
                    pJA.forEach(po -> {
                        try {
                            JSONObject pJo = ((JSONObject) po).getJSONObject(MESSAGE_KEY_EMAIL);
                            simpleMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pJo.getString(MESSAGE_KEY_EMAIL_ADDRESS), pJo.has(MESSAGE_KEY_EMAIL_NAME) ? pJo.getString(MESSAGE_KEY_EMAIL_NAME) : null));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return pJA;
                });
            }
            if (jo.has(MESSAGE_KEY_CC)) {
                Optional.ofNullable(jo.getJSONArray(MESSAGE_KEY_CC)).map(pJA -> {
                    pJA.forEach(po -> {
                        try {
                            JSONObject pJo = ((JSONObject) po).getJSONObject(MESSAGE_KEY_EMAIL);
                            simpleMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(pJo.getString(MESSAGE_KEY_EMAIL_ADDRESS), pJo.has(MESSAGE_KEY_EMAIL_NAME) ? pJo.getString(MESSAGE_KEY_EMAIL_NAME) : null));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return pJA;
                });
            }
            if (jo.has(MESSAGE_KEY_BCC)) {
                Optional.ofNullable(jo.getJSONArray(MESSAGE_KEY_BCC)).map(pJA -> {
                    pJA.forEach(po -> {
                        try {
                            JSONObject pJo = ((JSONObject) po).getJSONObject(MESSAGE_KEY_EMAIL);
                            simpleMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(pJo.getString(MESSAGE_KEY_EMAIL_ADDRESS), pJo.has(MESSAGE_KEY_EMAIL_NAME) ? pJo.getString(MESSAGE_KEY_EMAIL_NAME) : null));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return pJA;
                });
            }
            Transport.send(simpleMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
