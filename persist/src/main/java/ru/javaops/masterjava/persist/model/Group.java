package ru.javaops.masterjava.persist.model;

import lombok.*;
import ru.javaops.masterjava.persist.model.type.GroupType;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Group  extends BaseEntity {
   private @NonNull String name;
   private @NonNull GroupType type;

   public Group(Integer id, String name, GroupType type) {
      this(name, type);
      this.id = id;
   }
}
