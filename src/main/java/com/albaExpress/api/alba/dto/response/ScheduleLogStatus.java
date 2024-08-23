package com.albaExpress.api.alba.dto.response;

public enum ScheduleLogStatus {

    NORMAL("정상"), ABSENT("결근"), LATE("지각"), EARLYLEAVE("조퇴"), OVERTIME("연장");

    private final String scheduleStatus;

    ScheduleLogStatus(String scheduleLogStatus) {
        this.scheduleStatus = scheduleLogStatus;
    }

    public String getScheduleStatus () {
        return scheduleStatus;
    }
}
