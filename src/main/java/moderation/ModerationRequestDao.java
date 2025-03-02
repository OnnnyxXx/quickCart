package moderation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationRequestDao extends JpaRepository<ModerationRequest, Long> {
    List<ModerationRequest> findByStatus(String status);
}

