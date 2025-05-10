package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Service interface for managing user's contact information such as emails and phone numbers.
 * <p>
 * Each method returns the ID of the user to whom the updated, added, or deleted data belongs.
 */
public interface UserService {

    Long addEmail(@NotBlank String email);

    Long addPhone(@NotBlank String phone);

    Long changeEmail(@NotNull Long oldEmailId,@NotBlank String newEmail);

    Long changePhone(@NotNull Long oldPhoneId,@NotBlank String newPhone);

    Long deleteEmail(@NotNull Long emailId);

    Long deletePhone(@NotNull Long phoneId);

    Set<UserDTO> getAllUsers();
}
