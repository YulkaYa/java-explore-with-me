package ru.practicum.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.event.model.Event;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Override //todo возможно, надо убрать
    public int hashCode() {
        return 17;
    }

    @Override //todo возможно, надо убрать
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return id != null && id.equals(other.getId());
    }
}
