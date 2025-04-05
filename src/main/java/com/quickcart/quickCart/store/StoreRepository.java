package com.quickcart.quickCart.store;

import com.quickcart.quickCart.store.dto.StoreDTO;
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
            "new com.quickcart.quickCart.user.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE s.id = :storeId")
    Optional<StoreWithUserDto> findStoreWithUserById(Long storeId);

    @Query("SELECT new com.quickcart.quickCart.store.dto.StoreWithUserDto(" +
            "s.id, s.name, s.location, s.description, s.workingHours, s.rating, s.logoUrl, " +
            "new com.quickcart.quickCart.user.auth.dto.UserDtoInfo(u.id, u.username, u.email, u.location, u.role)) " +
            "FROM Store s JOIN s.user u WHERE s.status='ACTIVE'")
    List<StoreWithUserDto> findStoreWithUserFullInfo();


    @Query("SELECT new com.quickcart.quickCart.store.dto.StoreDTO(" +
            "u.id, s.name, s.location, s.description, s.workingHours, s.rating, s.status, s.logoUrl) " +
            "FROM Store s JOIN s.user u WHERE u.email = :userName")
    List<StoreDTO> myStore(String userName);


}
