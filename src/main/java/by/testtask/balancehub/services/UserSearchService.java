package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public interface UserSearchService {

    UserPageResp searchByAll(UserSearchReq req);

    UserPageResp searchByName(@NotBlank String name, int page, int size);

    UserPageResp searchByEmail(@NotBlank String email, int page, int size);
    UserPageResp searchByPhone(@NotBlank String phone, int page, int size);

    UserPageResp searchByDateOfBirth(@NotNull LocalDate dateOfBirth, int page, int size);

}
