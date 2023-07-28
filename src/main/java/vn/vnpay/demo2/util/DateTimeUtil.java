package vn.vnpay.demo2.util;

import com.google.common.base.Strings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static LocalDateTime convertStringToLocalDate(String dateStr) {
        if (Strings.isNullOrEmpty(dateStr)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.parse(dateStr, formatter);
    }
}
