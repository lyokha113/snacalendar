package datamapping;

import java.util.Arrays;

/**
 * Enum for list of holiday vacation status base on requirement
 *
 */
public enum HolidayVacationEnum {

    OFF(true), WORKING(false);

    private boolean vacation;

    HolidayVacationEnum(boolean vacation) {
        this.vacation = vacation;
    }

    public boolean isVacation() {
        return vacation;
    }

    public static HolidayVacationEnum getHolidayVacation(boolean vacation) {
        return Arrays.stream(HolidayVacationEnum.values()).filter(holiday -> holiday.vacation == vacation)
                .findFirst().orElse(null);
    }

}
