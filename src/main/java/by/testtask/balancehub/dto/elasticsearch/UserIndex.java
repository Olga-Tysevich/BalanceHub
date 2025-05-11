package by.testtask.balancehub.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.util.List;

@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIndex {
    @Id
    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirthday;

    private List<PhoneIndex> phones;

    private List<EmailIndex> emails;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhoneIndex {
        private Long id;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmailIndex {
        private Long id;
        private String email;
    }

}

