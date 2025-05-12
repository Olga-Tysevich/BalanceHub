package by.testtask.balancehub.controllers;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.common.UserSearchType;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static by.testtask.balancehub.dto.req.UserModificationRequests.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/users")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Add a new email address for the user",
            description = "Adds a new email address to the user's profile"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Email added successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @PostMapping("/emails/add")
    public ResponseEntity<?> addEmail(@RequestBody @Valid AddEmailRequest req) {
        Long userId = userService.addEmail(req.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", userId));
    }

    @Operation(
            summary = "Add a new phone number for the user",
            description = "Adds a new phone number to the user's profile"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Phone number added successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @PostMapping("/phones/add")
    public ResponseEntity<?> addPhone(@RequestBody @Valid AddPhoneRequest req) {
        Long userId = userService.addPhone(req.phone());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", userId));
    }

    @Operation(
            summary = "Change the user's email address",
            description = "Changes the user's email address from the old one to the new one"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Email changed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @PutMapping("/emails/change")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailRequest req) {
        Long userId = userService.changeEmail(req.oldEmailId(), req.newEmail());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @Operation(
            summary = "Change the user's phone number",
            description = "Changes the user's phone number from the old one to the new one"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Phone number changed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @PutMapping("/phones/change")
    public ResponseEntity<?> changePhone(@RequestBody @Valid ChangePhoneRequest req) {
        Long userId = userService.changePhone(req.oldPhoneId(), req.newPhone());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @Operation(
            summary = "Delete the user's email address",
            description = "Deletes the specified email address from the user's profile"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Email deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @DeleteMapping("/emails/delete")
    public ResponseEntity<?> deleteEmail(@RequestBody @Valid DeleteEmailRequest req) {
        Long userId = userService.deleteEmail(req.emailId());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @Operation(
            summary = "Delete the user's phone number",
            description = "Deletes the specified phone number from the user's profile"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Phone number deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @DeleteMapping("/phones/delete")
    public ResponseEntity<?> deletePhone(@RequestBody @Valid DeletePhoneRequest req) {
        Long userId = userService.deletePhone(req.phoneId());
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @Operation(
            summary = "Find user by ID",
            description = "Fetches the details of the user based on the provided user ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
    )
    @GetMapping("/find/{userId}")
    public ResponseEntity<UserDTO> findById(@PathVariable @NotNull Long userId) {
        UserDTO result = userService.findUserById(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Search users based on various criteria",
            description = "Allows searching users based on different criteria like email, phone number, etc."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Users found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/find")
    public ResponseEntity<Map<UserSearchType, UserPageResp>> find(@RequestBody @Valid UserSearchReq req) {
        Map<UserSearchType, UserPageResp> result = userService.find(req);
        return ResponseEntity.ok(result);
    }

}
