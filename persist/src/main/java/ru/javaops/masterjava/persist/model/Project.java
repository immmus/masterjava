package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseEntity {
    private @NonNull String name;
    private String description;

    public Project(Integer id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }
}
