package datamapping;

import java.util.Arrays;

/**
 * Enum for list of permission value base on database
 *
 */
public enum PermissionEnum {

    SUPER_USER(1), NORMAL_USER(2);

    private int permissionID;

    public int getPermissionID() {
        return permissionID;
    }

    PermissionEnum(int permissionID) {
        this.permissionID = permissionID;
    }

    public static PermissionEnum getEventType(int permissionID) {
        return Arrays.stream(PermissionEnum.values()).filter(permission -> permission.permissionID == permissionID)
                .findFirst().orElse(null);
    }
}
