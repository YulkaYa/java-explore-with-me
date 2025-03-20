package ru.practicum.compilation.model;

import jakarta.persistence.*;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false, length = 50)
    private String title;

    @Override //todo возможно, надо убрать
    public int hashCode() {
        return 13;
    }

    @Override //todo возможно, надо убрать
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Compilation other = (Compilation) obj;
        return id != null && id.equals(other.getId());
    }
}