package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private boolean success;
    private String errorCode;
    private String message;
    private int status;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponseDto(boolean success, String errorCode, String message, int status) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
    }
}