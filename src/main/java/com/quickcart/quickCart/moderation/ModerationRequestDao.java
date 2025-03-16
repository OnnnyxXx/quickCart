package com.quickcart.quickCart.moderation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationRequestDao extends JpaRepository<ModerationRequest, Long> {
    List<ModerationRequest> findByStatus(String status);
    boolean existsByStoreNameAndStatus(String storeName, String status);

    @Query(value = "select status from store where name = :name", nativeQuery = true)
    ModerationRequestStatus getStatusByStoreName(@Param("name") String storeName);

}

