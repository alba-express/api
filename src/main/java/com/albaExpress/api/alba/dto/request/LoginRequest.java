package com.albaExpress.api.alba.dto.request;

import lombok.*;
import javax.validation.constraints.NotBlank;
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
