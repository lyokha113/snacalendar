package datamapping;

import java.util.Arrays;

/**
 * Enum for list of annual leave status base on requirement
 *
 */
public enum AnnualStatusEnum {

    APPROVING(1), APPROVED(2), DENIED(3);

    private int status;

    public int getStatus() {
        return status;
    }

    AnnualStatusEnum(int status) {
        this.status = status;
    }

    public static AnnualStatusEnum getAnnualStatus(int status) {
        return Arrays.stream(AnnualStatusEnum.values()).filter(annualStatus -> annualStatus.getStatus() == status)
                .findFirst().orElse(null);
    }
}
