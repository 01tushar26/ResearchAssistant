package com.buddy.Code_Buddy.Clients;

import java.time.LocalDate;

public interface HolidayResponse {
     void loadHolidays(int year);
    LocalDate parseDate(String iso);
}
