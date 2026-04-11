package com.clothingstore.backend.dto.user;

import com.clothingstore.backend.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Partial profile update (matches FE snake_case fields).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProfileRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private Gender gender;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    private Object preferences;
}
