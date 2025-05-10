package by.testtask.balancehub.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserModificationRequests {

    public record AddEmailRequest(@NotBlank String email) {}

    public record AddPhoneRequest(@NotBlank String phone) {}

    public record ChangeEmailRequest(
            @NotNull Long oldEmailId,
            @NotBlank String newEmail
    ) {}

    public record ChangePhoneRequest(
            @NotNull Long oldPhoneId,
            @NotBlank String newPhone
    ) {}

    public record DeleteEmailRequest(@NotNull Long emailId) {}

    public record DeletePhoneRequest(@NotNull Long phoneId) {}
}