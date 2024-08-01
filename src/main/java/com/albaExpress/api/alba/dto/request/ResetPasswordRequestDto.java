package com.albaExpress.api.alba.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResetPasswordRequestDto {
    private String email;
    private String password;
}