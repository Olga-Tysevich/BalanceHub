package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.events.Events;
import by.testtask.balancehub.exceptions.EmailAlreadyInUse;
import by.testtask.balancehub.exceptions.PhoneAlreadyInUse;
import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.EmailDataRepo;
import by.testtask.balancehub.repos.PhoneDataRepo;
import by.testtask.balancehub.repos.UserRepo;
import by.testtask.balancehub.services.UserSearchService;
import by.testtask.balancehub.services.UserService;
import by.testtask.balancehub.utils.PrincipalExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmailDataRepo emailDataRepo;
    private final PhoneDataRepo phoneDataRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final UserSearchService userSearchService;
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final CacheManager cacheManager;

    @Override
    public Long addEmail(String email) {
        EmailData emailData = createEmail(email);

        emailDataRepo.save(emailData);
        publishEvent();
        addToCache(emailData);

        return emailData.getUser().getId();
    }

    @Override
    @CachePut(value = "users", key = "#id")
    public Long addPhone(String phone) {
        PhoneData phoneData = createPhone(phone);

        phoneDataRepo.save(phoneData);
        publishEvent();
        addToCache(phoneData);

        return phoneData.getUser().getId();
    }

    @Override
    public Long changeEmail(Long oldEmailId, String newEmail) {
        EmailData emailData = createEmail(newEmail);

        emailData.setId(oldEmailId);
        emailDataRepo.save(emailData);
        publishEvent();
        addToCache(emailData);

        return emailData.getUser().getId();
    }

    @Override
    public Long changePhone(Long oldPhoneId, String newPhone) {
        PhoneData phoneData = createPhone(newPhone);

        phoneData.setId(oldPhoneId);
        phoneDataRepo.save(phoneData);
        publishEvent();
        addToCache(phoneData);

        return phoneData.getUser().getId();
    }

    @Override
    public Long deleteEmail(Long emailId) {
        Long userId = PrincipalExtractor.getCurrentUserId();

        if (!emailDataRepo.existsByIdAndUserId(emailId, userId))
            throw new AccessDeniedException("The current user is not allowed to modify this email. User id: " + userId + ", email id: " + emailId);

        emailDataRepo.deleteById(emailId);
        publishEvent();
        clearCache(userId);

        return userId;
    }

    @Override
    public Long deletePhone(Long phoneId) {
        Long userId = PrincipalExtractor.getCurrentUserId();

        if (!phoneDataRepo.existsByIdAndUserId(phoneId, userId))
            throw new AccessDeniedException("The current user is not allowed to modify this phone. User id: " + userId + ", email id: " + phoneId);

        phoneDataRepo.deleteById(phoneId);
        publishEvent();
        clearCache(userId);

        return userId;
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDTO findUserById(Long id) {
        return userRepo.findById(id)
                .map(userMapper::toUserDTO)
                .orElseGet(() -> null);
    }

    @Override
    public Map<UserSearchType, UserPageResp> find(UserSearchReq request) {
        Map<UserSearchType, UserPageResp> users = new HashMap<>();

        if (request.searchByAllParams()) {
            users.put(UserSearchType.BY_ALL, userSearchService.searchByAll(request));
        }

        if (request.searchByName()) {
            users.put(UserSearchType.BY_NAME, userSearchService.searchByName(request.getName(), request.getPage(), request.getSize()));
        }

        if (request.searchByEmail()) {
            users.put(UserSearchType.BY_EMAIL, userSearchService.searchByEmail(request.getEmail(), request.getPage(), request.getSize()));
        }

        if (request.searchByPhone()) {
            users.put(UserSearchType.BY_PHONE, userSearchService.searchByPhone(request.getPhone(), request.getPage(), request.getSize()));
        }

        if (request.searchByDateOfBirth()) {
            users.put(UserSearchType.BY_BIRTHDAY, userSearchService.searchByDateOfBirth(request.getDateOfBirth(), request.getPage(), request.getSize()));
        }

        return users;
    }

    private EmailData createEmail(String email) {
        User currentUser = PrincipalExtractor.getCurrentUser();
        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        if (currentUser.isContainsEmail(email))
            throw new EmailAlreadyInUse(email, "Email has already been added for the current user with id: " + currentUser.getId());

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

        if (currentUser.isContainsPhone(phone))
            throw new PhoneAlreadyInUse(phone, "Phone has already been added for the current with id: " + currentUser.getId());


        Long currentUserId = currentUser.getId();

        if (phoneDataRepo.existsByPhoneNumberAndUserIdNot(phone, currentUserId)) throw new EmailAlreadyInUse(phone);

        return PhoneData.builder()
                .phoneNumber(phone)
                .user(currentUser)
                .build();
    }

    private void publishEvent() {
        User currentUser = PrincipalExtractor.getCurrentUser();

        if (Objects.isNull(currentUser)) throw new UnauthorizedException();

        UserIndexDTO index = userMapper.toUserIndex(currentUser);

        eventPublisher.publishEvent(new Events.UserChangedEvent(index));
    }

    private void addToCache(PhoneData phoneData) {
        Long userId = phoneData.getUser().getId();
        UserDTO userDTO = userMapper.toUserDTO(phoneData.getUser());

        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.put(userId, userDTO));
    }


    private void addToCache(EmailData emailData) {
        Long userId = emailData.getUser().getId();
        UserDTO userDTO = userMapper.toUserDTO(emailData.getUser());

        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.put(userId, userDTO));
    }

    private void clearCache(Long userId) {
        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.evict(userId));
    }

}
