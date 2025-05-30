package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDto;
import com.quickcart.quickCart.store.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModerationRequestDao extends JpaRepository<ModerationRequest, Long> {

    @Query(value = "SELECT status FROM store WHERE name = :name", nativeQuery = true)
    Optional<String> getStatusStringByStoreName(@Param("name") String storeName);

    @Query("SELECT new com.quickcart.quickCart.moderation.dto.ModerationDto(" +
            "s.id, s.name, s.logoUrl, s.location, s.description, s.workingHours, s.rating, s.status, " +
            "new com.quickcart.quickCart.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u")
    List<ModerationDto> getStores(Pageable pageable);

    @Query("SELECT new com.quickcart.quickCart.moderation.dto.ModerationDto(" +
            "s.id, s.name, s.logoUrl, s.location, s.description, s.workingHours, s.rating, s.status, " +
            "new com.quickcart.quickCart.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE u.id = :adminId")
    List<ModerationDto> getRequestsByAdminId(@Param("adminId") Long adminId);


}

