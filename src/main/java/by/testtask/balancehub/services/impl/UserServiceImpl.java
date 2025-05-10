package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.exceptions.EmailAlreadyInUse;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.repos.EmailDataRepo;
import by.testtask.balancehub.repos.PhoneDataRepo;
import by.testtask.balancehub.repos.UserRepo;
import by.testtask.balancehub.services.UserService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final EmailDataRepo emailDataRepo;
    private final PhoneDataRepo phoneDataRepo;

    @Override
    public Long addEmail(String email) {
        EmailData emailData = createEmail(email);

        emailDataRepo.save(emailData);

        return emailData.getUser().getId();
    }

    @Override
    public Long addPhone(String phone) {
        PhoneData phoneData = createPhone(phone);

        phoneDataRepo.save(phoneData);

        return phoneData.getUser().getId();
    }

    @Override
    public Long changeEmail(Long oldEmailId, String newEmail) {
        EmailData emailData = createEmail(newEmail);

        emailData.setId(oldEmailId);
        emailDataRepo.save(emailData);

        return emailData.getUser().getId();
    }

    @Override
    public Long changePhone(Long oldPhoneId, String newPhone) {
        PhoneData phoneData = createPhone(newPhone);

        phoneData.setId(oldPhoneId);
        phoneDataRepo.save(phoneData);

        return phoneData.getUser().getId();
    }

    @Override
    public Long deleteEmail(Long emailId) {
        Long userId = PrincipalExtractor.getCurrentUserId();

        if (!emailDataRepo.existsByIdAndUserId(emailId, userId))
            throw new AccessDeniedException("The current user is not allowed to modify this email. User id: " + userId + ", email id: " + emailId);

        emailDataRepo.deleteById(emailId);

        return userId;
    }

    @Override
    public Long deletePhone(Long phoneId) {
        Long userId = PrincipalExtractor.getCurrentUserId();

        if (!phoneDataRepo.existsByIdAndUserId(phoneId, userId))
            throw new AccessDeniedException("The current user is not allowed to modify this phone. User id: " + userId + ", email id: " + phoneId);

        phoneDataRepo.deleteById(phoneId);

        return userId;
    }

    private EmailData createEmail(String email) {
        User currentUser = PrincipalExtractor.getCurrentUser();
        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        Long currentUserId = currentUser.getId();

        if (emailDataRepo.existsByEmailAndUserIdNot(email, currentUserId)) throw new EmailAlreadyInUse(email);

        return EmailData.builder()
                .email(email)
                .user(currentUser)
                .build();
    }

    private PhoneData createPhone(String phone) {
        User currentUser = PrincipalExtractor.getCurrentUser();
        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        Long currentUserId = currentUser.getId();

        if (phoneDataRepo.existsByPhoneNumberAndUserIdNot(phone, currentUserId)) throw new EmailAlreadyInUse(phone);

        return PhoneData.builder()
                .phoneNumber(phone)
                .user(currentUser)
                .build();
    }
}
