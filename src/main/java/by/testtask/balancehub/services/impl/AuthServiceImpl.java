package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.req.UserLoginDTO;
import by.testtask.balancehub.dto.resp.LoggedUserDTO;
import by.testtask.balancehub.exceptions.PasswordMismatchException;
import by.testtask.balancehub.services.AuthService;
import by.testtask.balancehub.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    @Override
    public LoggedUserDTO loginUser(UserLoginDTO req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                req.getEmail(),
                req.getPassword()
        ));

        User user = (User) userDetailsService.loadUserByUsername(req.getEmail());

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }

        return jwtService.generatePairOfTokens(user);
    }

    @Override
    public LoggedUserDTO reLoginUser(String refreshToken) {
        return jwtService.regeneratePairOfTokens(refreshToken);
    }

}