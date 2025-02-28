package hermesmsg.util;

import hermesmsg.entity.ByteArrayAttachment;
import hermesmsg.entity.EmailMessage;
import jakarta.mail.internet.InternetAddress;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class MessageConverter implements Constant {

    static Logger logger = Logger.getLogger(MessageConverter.class.getName());

    public static ByteArrayAttachment fileToByteArrayAttachment(File file) {
        return new ByteArrayAttachment(file, null);
    }

    public static List<ByteArrayAttachment> fileListToByteArrayAttachmentList(List<File> attachmentList) {
        if (attachmentList == null) return null;
        return attachmentList.stream().map(val -> {
            try {
                return fileToByteArrayAttachment(val);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    //
    private static byte[] getCompressedData(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(baos);
            dos.write(bytes);
            dos.close();
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getDecompressedData(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InflaterOutputStream ios = new InflaterOutputStream(baos);
            ios.write(bytes);
            ios.close();
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    private static JSONObject getMessageBodyObject(String body, boolean isHtml) {
        return new JSONObject().put(MESSAGE_KEY_BODY_CONTENT, body).put(MESSAGE_KEY_BODY_CONTENT_TYPE, isHtml ? MESSAGE_VALUE_BODY_CONTENT_TYPE_HTML : MESSAGE_VALUE_BODY_CONTENT_TYPE_TEXT);
    }

    private static JSONObject parseMessageFromObject(String internetAddressStr) {
        try {
            return getMessageRecipientObject(InternetAddress.parse(internetAddressStr)[0]);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        return null;
    }

    private static JSONArray getMessageRecipientArray(String internetAddressStr) {
        if (internetAddressStr == null || internetAddressStr.isBlank()) return null;
        JSONArray jsonArray = new JSONArray();
        try {
            Stream.of(InternetAddress.parse(internetAddressStr)).map(MessageConverter::getMessageRecipientObject).forEach(jsonArray::put);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JSONObject getMessageRecipientObject(InternetAddress ia) {
        JSONObject jsonObject = new JSONObject().put(MESSAGE_KEY_EMAIL_ADDRESS, ia.getAddress());
        Optional.ofNullable(ia.getPersonal()).map(val -> {
            jsonObject.put(MESSAGE_KEY_EMAIL_NAME, val);
            return val;
        });
        return new JSONObject().put(MESSAGE_KEY_EMAIL, jsonObject);
    }

    private static JSONArray getMessageAttachmentArray(List<ByteArrayAttachment> attachments) {
        if (attachments == null || attachments.isEmpty()) return null;
        JSONArray jsonArray = new JSONArray();
        for (ByteArrayAttachment baa : attachments) {
            jsonArray.put(getMessageAttachmentObject(baa));
        }
        return jsonArray;
    }

    private static JSONObject getMessageAttachmentObject(ByteArrayAttachment attachment) {
        return new JSONObject()
                .put(MESSAGE_KEY_ATTACHMENTS_DATA_TYPE, MESSAGE_VALUE_ATTACHMENTS_DATA_TYPE)
                .put(MESSAGE_KEY_ATTACHMENTS_NAME, attachment.getFilename())
                .put(MESSAGE_KEY_ATTACHMENTS_CONTENT_TYPE, attachment.getContentType())
                .put(MESSAGE_KEY_ATTACHMENTS_DATA, attachment.getBase64Data());
    }

    private static String buildJSONMsgStr(EmailMessage emailMessage, boolean useCompress) {
        JSONObject jsonObject = new JSONObject()
                .put(MESSAGE_KEY_SUBJECT, emailMessage.getSubject())
                .put(MESSAGE_KEY_BODY, getMessageBodyObject(emailMessage.getBody(), emailMessage.isHtml()))
                .put(MESSAGE_KEY_TO, getMessageRecipientArray(emailMessage.getTo()));
        Optional.ofNullable(parseMessageFromObject(emailMessage.getFrom())).map(val -> {
            jsonObject.put(MESSAGE_KEY_FROM, val);
            return val;
        });
        Optional.ofNullable(getMessageRecipientArray(emailMessage.getCc())).map(val -> {
            jsonObject.put(MESSAGE_KEY_CC, val);
            return val;
        });
        Optional.ofNullable(getMessageRecipientArray(emailMessage.getBcc())).map(val -> {
            jsonObject.put(MESSAGE_KEY_BCC, val);
            return val;
        });
        Optional.ofNullable(getMessageAttachmentArray(emailMessage.getAttachments())).map(val -> {
            jsonObject.put(MESSAGE_KEY_ATTACHMENTS, val);
            return val;
        });

        try {
            byte[] bytes = null;
            bytes = jsonObject.toString().getBytes("UTF-8");
            if (useCompress) {
                bytes = getCompressedData(bytes);
            }
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJsonMsgStr(String connectionName, EmailMessage emailMessage, JSONObject options) {
        boolean useCompress = Optional.ofNullable(options).map(jo -> (boolean) jo.get(OPTION_USE_COMPRESS)).orElse(false);
        return new JSONObject().put("connection", connectionName).put("msg", buildJSONMsgStr(emailMessage, useCompress)).put("options", options).toString();
    }

    public static JSONObject parseJSONStr(String jsonMsgStr) {
        try {
            return new JSONObject(jsonMsgStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject parseEmailMessage(String bodyStr, boolean isCompressed) {
        try {
            byte[] bytes = Base64.getDecoder().decode(bodyStr);
            if (isCompressed) {
                bytes = getDecompressedData(bytes);
            }
            return new JSONObject(new String(bytes, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject parseEmailAddressObject(JSONObject jo) {
        return Optional.ofNullable(jo).map(pJo -> pJo.getJSONObject(MESSAGE_KEY_EMAIL)).orElse(null);
    }

    public static Properties mapToProp(Map<String, String> map) {
        return map
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (prev, next) -> next, Properties::new
                        )
                );
    }
}

