package by.testtask.balancehub.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static by.testtask.balancehub.utils.Constants.TOKEN_TYPE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedUserDTO {
    @Builder.Default
    private String type = TOKEN_TYPE;

    private String accessToken;

    private String refreshToken;

    private Long userId;

}