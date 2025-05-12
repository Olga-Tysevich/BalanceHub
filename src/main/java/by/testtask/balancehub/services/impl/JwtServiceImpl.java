package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;
import by.testtask.balancehub.exceptions.InvalidRefreshTokenException;
import by.testtask.balancehub.repos.UserRepo;
import by.testtask.balancehub.services.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static by.testtask.balancehub.utils.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class JwtServiceImpl implements JwtService {

    private final JwtProvider jwtProvider;

    private final UserRepo userRepo;

    @Override
    public LoggedUserDTO generatePairOfTokens(User user, String currentLogin) {
        log.info("Generating token pair for user id: {} and login: {}", user.getId(), currentLogin);

        String accessToken = jwtProvider.generateAccessToken(user, currentLogin);
        String refreshToken = jwtProvider.generateRefreshToken(currentLogin);

        log.info("Token pair generated successfully for user id: {}", user.getId());

        return LoggedUserDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public LoggedUserDTO regeneratePairOfTokens(@Valid @NotBlank(message = TOKEN_CANNOT_BE_NULL_OR_EMPTY)
                                                String refreshToken) {
        log.info("Attempting to regenerate token pair using refresh token: {}", refreshToken);

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            log.error("Invalid refresh token provided: {}", refreshToken);
            throw new InvalidRefreshTokenException();
        }

        String currentLogin = jwtProvider.getSubjectFromToken(refreshToken, false);

        Long id = ((Number) jwtProvider.getRefreshClaims(refreshToken).get(USER_CLAIM_KEY)).longValue();

        log.info("Extracted user id: {} from refresh token for user with login: {}", id, currentLogin);

        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        log.info("User found with id: {}", user.getId());

        return generatePairOfTokens(user, currentLogin);
    }

}