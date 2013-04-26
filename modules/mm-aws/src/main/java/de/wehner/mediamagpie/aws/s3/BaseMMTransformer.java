package de.wehner.mediamagpie.aws.s3;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.s3.model.ObjectMetadata;

import de.wehner.mediamagpie.core.util.ExceptionUtil;

public abstract class BaseMMTransformer {

    @SuppressWarnings("unchecked")
    protected void addStringIntoUserMetadata(String key, Object value, ObjectMetadata objectMetadata) {
        String strValue = null;
        if (value instanceof String) {
            strValue = (String) value;
        } else if (value instanceof Date) {
            strValue = ((Date) value).getTime() + "";
        } else if (value instanceof List<?>) {
            strValue = StringUtils.join(((List<String>) value), ',');
        }
        if (!StringUtils.isEmpty(strValue)) {
            String encodedValue = strValue;
            try {
                encodedValue = MimeUtility.encodeText(strValue);
            } catch (UnsupportedEncodingException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
            objectMetadata.addUserMetadata(key, encodedValue);
        }
    }

    protected String getValueFromUserMetadata(String key, ObjectMetadata objectMetadata) {
        Map<String, String> userMetadata = objectMetadata.getUserMetadata();
        String value = userMetadata.get(key);
        if (!StringUtils.isEmpty(value)) {
            try {
                return MimeUtility.decodeText(value);
            } catch (UnsupportedEncodingException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
        }
        return null;
    }
}
