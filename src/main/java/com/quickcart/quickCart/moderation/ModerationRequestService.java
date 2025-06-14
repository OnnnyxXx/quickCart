package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDto;
import com.quickcart.quickCart.moderation.dto.ModerationRequestDto;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreRepository;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;

@Service
public class ModerationRequestService {

    private final StoreRepository storeRepository;
    private final ModerationRequestDao moderationRequestDao;
    private final UserRepository userRepository;

    public ModerationRequestService(StoreRepository storeRepository, ModerationRequestDao moderationRequestDao, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.moderationRequestDao = moderationRequestDao;
        this.userRepository = userRepository;
    }


    public Map<User, List<ModerationDto>> getStores() {
        try {
            List<User> userAdmin = userRepository.getModer();
            Pageable twenty = PageRequest.of(0, 20); // Limit
            List<ModerationDto> moderationRequestDaoList = moderationRequestDao.getStores(twenty);

            Map<User, List<ModerationDto>> adminRequestsMap = new HashMap<>();

            if (userAdmin.isEmpty()){
                return adminRequestsMap;
            }

            for (User admin : userAdmin) {
                adminRequestsMap.put(admin, new ArrayList<>());
            }

            for (int i = 0; i < moderationRequestDaoList.size(); i++) {
                User assignedAdmin = userAdmin.get(i % userAdmin.size());
                adminRequestsMap.get(assignedAdmin).add(moderationRequestDaoList.get(i));
            }

            return adminRequestsMap;
        }catch (Exception e){
            return Collections.emptyMap();
        }

    }

    public List<ModerationDto> getStoresForModer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentModerEmail = authentication.getName();

        Optional<User> currentModerOpt = userRepository.findByEmail(currentModerEmail);
        if (currentModerOpt.isEmpty()) {
            return Collections.emptyList();
        }

        User currentAdmin = currentModerOpt.get();
        Map<User, List<ModerationDto>> moderRequestsMap = getStores();

        return moderRequestsMap.getOrDefault(currentAdmin, Collections.emptyList());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "storeAll", allEntries = true),
        @CacheEvict(value = "store", key = "#id")
    })
    public HashMap<String, String> changeStoreStatus(Long id, ModerationRequestDto moderationDTO) {

        if (moderationDTO == null || moderationDTO.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус не должен быть пустым");
        }

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден " + id));

        ModerationRequest moderationRequest = moderationRequestDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос на модерацию не найден " + id));

        store.setStatus(moderationDTO.getStatus());
        store.getUser().setRole(moderationDTO.getStatus() == Store.StoreStatus.ACTIVE ? User.Role.SELLER : User.Role.BUYER);

        moderationRequest.setStatus(moderationDTO.getStatus());

        HashMap<String, String> updatedFields = new HashMap<>();
        updatedFields.put("status", store.getStatus().name());

        storeRepository.save(store);
        moderationRequestDao.save(moderationRequest);

        return updatedFields;
    }

}




