package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.type.UserFlag;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;
    @Column("city_id")
    private @NonNull String city;

    public User(Integer id, String fullName, String email, String city, UserFlag flag) {
        this(fullName, email, flag, city);
        this.id = id;
    }
}