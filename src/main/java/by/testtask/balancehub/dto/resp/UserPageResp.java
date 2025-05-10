package by.testtask.balancehub.dto.resp;


import by.testtask.balancehub.dto.common.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPageResp {
    private String searchType;
    private Set<UserDTO> users;
    private int page;
    private int totalPages;
    private int totalUsers;
}
