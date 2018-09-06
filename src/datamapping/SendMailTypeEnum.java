package datamapping;

import java.util.Arrays;

/**
 * Enum for list of sending mail base on requirement
 *
 */
public enum SendMailTypeEnum {
    NEW_CUSTOMER_CARE("new_customer_care"), UPDATE_CUSTOMER_CARE("update_customer_care"),
    NEW_TRAINING("new_training"), UPDATE_TRAINING("update_training");

    private String type;

    SendMailTypeEnum(String type) { this.type = type;}

    public String getType() { return type; }

    public static SendMailTypeEnum getSendMailType(String sendType) {
        return Arrays.stream(SendMailTypeEnum.values()).filter(type -> type.getType().equals(sendType))
                .findFirst().orElse(null);
    }
}
