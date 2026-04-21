package restaurant.enums;

import lombok.Getter;

@Getter
public enum TableStatus {
    AVAILABLE("Trống"),
    OCCUPIED("Có khách"),
    RESERVED("Đã đặt trước"),
    OUT_OF_SERVICE("Dừng phục vụ");

    private final String displayValue;

    TableStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}