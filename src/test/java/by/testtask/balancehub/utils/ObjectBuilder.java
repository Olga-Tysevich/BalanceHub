package by.testtask.balancehub.utils;

import by.testtask.balancehub.domain.Account;
import by.testtask.balancehub.domain.EmailData;
import by.testtask.balancehub.domain.PhoneData;
import by.testtask.balancehub.domain.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static by.testtask.balancehub.utils.TestConstants.*;

@UtilityClass
public class ObjectBuilder {

    public User buildUser1() {
        User user =  User.builder()
                .name(USERNAME_FOR_NEW_1)
                .dateOfBirth(USERNAME_FOR_NEW_1_DATE_OF_BIRTH)
                .password(USERNAME_FOR_NEW_1_PASSWORD)
                .build();

        user.setEmails(buildEmailList(user, USERNAME_FOR_NEW_1_EMAIL_LIST));
        user.setPhones(buildPhoneList(user, USERNAME_FOR_NEW_1_PHONE_LIST));

        Account account = buildEmptyAccount1(user);
        user.setAccount(account);

        return user;
    }

    public Account buildEmptyAccount1(User user) {
        return Account.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .hold(BigDecimal.ZERO)
                .initialBalance(BigDecimal.ZERO)
                .build();
    }

    public Set<PhoneData> buildPhoneList(User user, List<String> phones) {
        return phones.stream()
                .map(ph -> {
                    PhoneData phoneData = new PhoneData();
                    phoneData.setUser(user);
                    phoneData.setPhoneNumber(ph);
                    return phoneData;
                }).collect(Collectors.toSet());
    }


    public Set<EmailData> buildEmailList(User user, List<String> phones) {
        return phones.stream()
                .map(email -> {
                    EmailData emailData = new EmailData();
                    emailData.setUser(user);
                    emailData.setEmail(email);
                    return emailData;
                }).collect(Collectors.toSet());
    }
}
