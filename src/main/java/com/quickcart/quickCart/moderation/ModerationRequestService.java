package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDTO;
import com.quickcart.quickCart.moderation.dto.ModerationRequestDTO;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreRepository;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
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


    public Map<User, List<ModerationDTO>> getStores() {
        List<User> userAdmin = userRepository.getModer();
        Pageable twenty = PageRequest.of(0, 20); // Limit
        List<ModerationDTO> moderationRequestDaoList = moderationRequestDao.getStores(twenty);

        Map<User, List<ModerationDTO>> adminRequestsMap = new HashMap<>();

        for (User admin : userAdmin) {
            adminRequestsMap.put(admin, new ArrayList<>());
        }

        for (int i = 0; i < moderationRequestDaoList.size(); i++) {
            User assignedAdmin = userAdmin.get(i % userAdmin.size());
            adminRequestsMap.get(assignedAdmin).add(moderationRequestDaoList.get(i));
        }

        return adminRequestsMap;
    }

    public List<ModerationDTO> getStoresForModer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentModerEmail = authentication.getName();

        Optional<User> currentModerOpt = userRepository.findByEmail(currentModerEmail);
        if (currentModerOpt.isEmpty()) {
            return Collections.emptyList();
        }

        User currentAdmin = currentModerOpt.get();
        Map<User, List<ModerationDTO>> moderRequestsMap = getStores();
        List<ModerationDTO> currentModerRequests = moderRequestsMap.getOrDefault(currentAdmin, Collections.emptyList());

        return currentModerRequests;
    }


    @CacheEvict(value = "allStore", allEntries = true)
    @Transactional
    public HashMap<String, String> changeStoreStatus(Long id, ModerationRequestDTO moderationDTO) {

        if (moderationDTO == null || moderationDTO.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must not be null");
        }

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + id));

        ModerationRequest moderationRequest = moderationRequestDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moderation request not found with id " + id));

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




