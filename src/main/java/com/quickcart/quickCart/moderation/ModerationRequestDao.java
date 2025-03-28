package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationRequestDao extends JpaRepository<ModerationRequest, Long> {

    @Query(value = "SELECT status FROM store WHERE name = :name", nativeQuery = true)
    ModerationRequestStatus getStatusByStoreName(@Param("name") String storeName);

    @Query("SELECT new com.quickcart.quickCart.moderation.dto.ModerationDTO(" +
            "s.id, s.name, s.logoUrl, s.location, s.description, s.workingHours, s.rating, s.status, " +
            "new com.quickcart.quickCart.user.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u")
    List<ModerationDTO> getStores(Pageable pageable);

    @Query("SELECT new com.quickcart.quickCart.moderation.dto.ModerationDTO(" +
            "s.id, s.name, s.logoUrl, s.location, s.description, s.workingHours, s.rating, s.status, " +
            "new com.quickcart.quickCart.user.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE u.id = :adminId")
    List<ModerationDTO> getRequestsByAdminId(@Param("adminId") Long adminId);


}

