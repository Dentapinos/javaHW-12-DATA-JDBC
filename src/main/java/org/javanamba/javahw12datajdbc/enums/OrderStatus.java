package org.javanamba.javahw12datajdbc.enums;

public enum OrderStatus {
    REJECTED("Отклонена"),
    EXECUTED("Выполнена"),
    SECONDARY_REVIEW("На рассмотрении"),
    POSTPONED("Перенесена");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}