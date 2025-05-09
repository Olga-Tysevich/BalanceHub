package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;
import by.testtask.balancehub.exceptions.InvalidRefreshTokenException;
import by.testtask.balancehub.repos.UserRepo;
import by.testtask.balancehub.services.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static by.testtask.balancehub.utils.Constants.*;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class JwtServiceImpl implements JwtService {

    private final JwtProvider jwtProvider;

    private final UserRepo userRepo;

    @Override
    public LoggedUserDTO generatePairOfTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return LoggedUserDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public LoggedUserDTO regeneratePairOfTokens(@Valid @NotBlank(message = TOKEN_CANNOT_BE_NULL_OR_EMPTY)
                                                String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        Long id = ((Number) jwtProvider.getRefreshClaims(refreshToken).get(USER_CLAIM_KEY)).longValue();

        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return generatePairOfTokens(user);
    }

}