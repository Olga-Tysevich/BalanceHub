package by.testtask.balancehub.services;


import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;

public interface JwtService {

    LoggedUserDTO generatePairOfTokens(User user, String currentLogin);

    LoggedUserDTO regeneratePairOfTokens(String refreshToken);

}
