package ru.practicum.participation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationRequestStatus status = ParticipationRequestStatus.PENDING;

    @Override
    public int hashCode() {
        return 16;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParticipationRequest other = (ParticipationRequest) obj;
        return id != null && id.equals(other.getId());
    }
}