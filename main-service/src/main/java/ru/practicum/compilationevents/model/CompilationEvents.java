package ru.practicum.compilationevents.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;

@Getter
/*@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "compilation_events") todo возможно надо удалить*/
public class CompilationEvents {
/*    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id", nullable = false)
    private Compilation compilation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Override //todo возможно, надо убрать
    public int hashCode() {
        return 18;
    }

    @Override //todo возможно, надо убрать
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompilationEvents other = (CompilationEvents) obj;
        return id != null && id.equals(other.getId());
    }*/
}