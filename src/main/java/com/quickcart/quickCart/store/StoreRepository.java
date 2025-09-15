package com.quickcart.quickCart.store;

import com.quickcart.quickCart.store.dto.StoreDto;
import com.quickcart.quickCart.store.dto.StoreWithUserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT new com.quickcart.quickCart.store.dto.StoreWithUserDto(" +
            "s.id, s.name, s.location, s.description, s.workingHours, s.rating, s.logoUrl, " +
            "new com.quickcart.quickCart.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE s.id = :storeId AND s.deleted = false")
    Optional<StoreWithUserDto> findStoreWithUserById(Long storeId);

    @Query("SELECT new com.quickcart.quickCart.store.dto.StoreWithUserDto(" +
            "s.id, s.name, s.location, s.description, s.workingHours, s.rating, s.logoUrl, " +
            "new com.quickcart.quickCart.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE s.status='ACTIVE' AND s.deleted = false")
    List<StoreWithUserDto> findStoreWithUserFullInfo();


    @Query("SELECT new com.quickcart.quickCart.store.dto.StoreDto(" +
            "u.id, s.id, s.name, s.location, s.description, s.workingHours, s.rating, s.status, s.logoUrl) " +
            "FROM Store s JOIN s.user u WHERE u.email = :userName AND s.deleted = false")
    List<StoreDto> myStore(String userName);


}
