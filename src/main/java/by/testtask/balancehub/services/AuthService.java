package by.testtask.balancehub.services;

import by.testtask.balancehub.dto.req.UserLoginDTO;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;

public interface AuthService {

    LoggedUserDTO loginUser(UserLoginDTO req);

    LoggedUserDTO reLoginUser(String refreshToken);

}