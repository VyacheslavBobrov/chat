package ru.bobrov.vyacheslav.chat.controllers.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@NonNull
public class JwtResponse {
    String jwtToken;
}
