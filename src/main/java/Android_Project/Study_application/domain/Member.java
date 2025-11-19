package Android_Project.Study_application.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Member {
    private Long id;
    private String userid;
    private String pw;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime joinDate;
}
