package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class City {
    private @NonNull String id;
    private @NonNull String name;
}
