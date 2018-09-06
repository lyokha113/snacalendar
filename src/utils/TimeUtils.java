package utils;

import entity.HolidayDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Helper class to process with date and time
 *
 */
public class TimeUtils {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private TimeUtils(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    // Convert long milisecond to LocalDateTime
    public static TimeUtils getTimeOfDateRange(String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        LocalDateTime startTime = LocalDateTime.of(LocalDate.parse(start, formatter), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(end, formatter), LocalTime.parse("23:59:59"));
        return new TimeUtils(startTime, endTime);
    }

    // Convert long milisecond to LocalDateTime
    public static TimeUtils getTimeOfDateTimeRange(String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MMMM dd yyyy");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        return new TimeUtils(startTime, endTime);
    }

    // Count duration days of time range
    private static double countDays(LocalDateTime start, LocalDateTime end, List<HolidayDTO> holidays) {
        double days = 0;

        // Exclude weekend
        int dayOfWeek = start.toLocalDate().getDayOfWeek().getValue();
        int period = Period.between(start.toLocalDate(), end.toLocalDate()).getDays();
        for (; period >= 0; period--) {

            if (dayOfWeek == 6) {
                dayOfWeek = 1;
                period--;
                continue;
            }

            days++;
            dayOfWeek++;
        }

        // Check half day
        if (end.toLocalTime().isBefore(LocalTime.NOON) || end.toLocalTime().equals(LocalTime.NOON)) {
            days -= 0.5;
        }
        if (start.toLocalTime().isAfter(LocalTime.NOON) || start.toLocalTime().equals(LocalTime.NOON)) {
            days -= 0.5;
        }

        // Exclude holidays vacation and if vaction is weekend
        if (holidays != null && !holidays.isEmpty()) {
            days -= holidays.stream()
                    .filter(holiday -> holiday.isHolidayVacation()
                            && holiday.getHolidayStartDate().getDayOfYear() > start.getDayOfYear()
                            && holiday.getHolidayEndDate().getDayOfYear() < end.getDayOfYear())
                    .mapToInt(holiday -> holiday.getHolidayEndDate().getDayOfYear()
                            - holiday.getHolidayStartDate().getDayOfYear()
                            + 1)
                    .sum();
        }

        return days;
    }

    // Count annual leave days of time range
    public static double countAnnualLeaveDays(LocalDateTime start, LocalDateTime end, List<HolidayDTO> holidays) {
        LocalDateTime now = LocalDateTime.now();

        if (start.getYear() != now.getYear()) {
            start = LocalDateTime.of(now.with(TemporalAdjusters.firstDayOfYear()).toLocalDate(), LocalTime.MIN);
        }
        if (end.getYear() != now.getYear()) {
            end = LocalDateTime.of(now.with(TemporalAdjusters.lastDayOfYear()).toLocalDate(), LocalTime.MAX);
        }

        if (start.getDayOfYear() == end.getDayOfYear()) {
            if (end.toLocalTime().isBefore(LocalTime.NOON) || end.toLocalTime().equals(LocalTime.NOON)) return 0.5;
            if (start.toLocalTime().isAfter(LocalTime.NOON) || start.toLocalTime().equals(LocalTime.NOON)) return 0.5;
            return 1;
        } else {
            return countDays(start, end, holidays);
        }
    }

    // Get title for annuale leave event by count number day off
    public static String getAnnualLeaveTitle(LocalDateTime start, LocalDateTime end, List<HolidayDTO> holidays) {
        double day = TimeUtils.countAnnualLeaveDays(start, end, holidays);
        if (day == 0.5) {
            return start.toLocalTime().isAfter(LocalTime.NOON) || start.toLocalTime().equals(LocalTime.NOON)
                    ? "Half Day Afternoon Off"
                    : "Half Day Morning Off";
        } else if (day == 1.0) {
            return "Day Off";
        } else {
            return day + " Days Off";
        }
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

}

