package com.codigo.code.test.enums;


public enum AccountStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    DELETED("DELETED");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static AccountStatus fromString(String status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.status.equalsIgnoreCase(status)) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
