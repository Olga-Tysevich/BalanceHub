package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Service interface for managing index's contact information such as emails and phone numbers.
 * <p>
 * Each method returns the ID of the index to whom the updated, added, or deleted data belongs.
 */
public interface UserService {

    Long addEmail(@NotBlank String email);

    Long addPhone(@NotBlank String phone);

    Long changeEmail(@NotNull Long oldEmailId,@NotBlank String newEmail);

    Long changePhone(@NotNull Long oldPhoneId,@NotBlank String newPhone);

    Long deleteEmail(@NotNull Long emailId);

    Long deletePhone(@NotNull Long phoneId);

    UserDTO findUserById(@NotNull Long id);

    Map<UserSearchType, UserPageResp> find(@NotNull UserSearchReq request);
}
