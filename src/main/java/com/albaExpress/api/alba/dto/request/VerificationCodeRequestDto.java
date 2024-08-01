package com.albaExpress.api.alba.dto.request;

import lombok.Data;

@Data
public class VerificationCodeRequestDto {
    private String email;
    private int code;
}
