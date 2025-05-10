package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.common.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface UserSearchService {

    List<UserDTO> searchByAll(String name, String email, String phone, LocalDate dateOfBirth, int page, int size) throws IOException;

    List<UserDTO> searchByName(@NotBlank String name, int page, int size) throws IOException;

    List<UserDTO> searchByEmail(@NotBlank String email, int page, int size) throws IOException;

    List<UserDTO> searchByDateOfBirthday(@NotNull LocalDate dateOfBirth, int page, int size) throws IOException;

}
