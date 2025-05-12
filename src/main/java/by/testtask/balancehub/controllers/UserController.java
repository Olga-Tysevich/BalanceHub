package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static by.testtask.balancehub.dto.req.UserModificationRequests.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/emails/add")
    public ResponseEntity<?> addEmail(@RequestBody @Valid AddEmailRequest req) {
        Long userId = userService.addEmail(req.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", userId));
    }

    @PostMapping("/phones/add")
    public ResponseEntity<?> addPhone(@RequestBody @Valid AddPhoneRequest req) {
        Long userId = userService.addPhone(req.phone());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", userId));
    }

    @PutMapping("/emails/change")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailRequest req) {
        Long userId = userService.changeEmail(req.oldEmailId(), req.newEmail());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @PutMapping("/phones/change")
    public ResponseEntity<?> changePhone(@RequestBody @Valid ChangePhoneRequest req) {
        Long userId = userService.changePhone(req.oldPhoneId(), req.newPhone());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @DeleteMapping("/emails/delete")
    public ResponseEntity<?> deleteEmail(@RequestBody @Valid DeleteEmailRequest req) {
        Long userId = userService.deleteEmail(req.emailId());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @DeleteMapping("/phones/delete")
    public ResponseEntity<?> deletePhone(@RequestBody @Valid DeletePhoneRequest req) {
        Long userId = userService.deletePhone(req.phoneId());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @GetMapping("/find")
    public ResponseEntity<UserDTO> findById(@RequestParam @NotNull Long userId) {
        UserDTO result = userService.findUserById(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find")
    public ResponseEntity<Map<UserSearchType, UserPageResp>> find(@RequestBody @Valid UserSearchReq req) {
        Map<UserSearchType, UserPageResp> result = userService.find(req);
        return ResponseEntity.ok(result);
    }

}
