package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDto;
import com.quickcart.quickCart.moderation.dto.ModerationRequestDto;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreRepository;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ModerationRequestService {

    Logger logger = LoggerFactory.getLogger(ModerationRequestService.class);

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
            List<User> moderators = userRepository.getModer();
            if (moderators == null || moderators.isEmpty()) {
                return Collections.emptyMap();
            }

            List<ModerationDto> moderationRequests = moderationRequestDao.getStores();

            Map<User, List<ModerationDto>> requestsByModerator = new HashMap<>();
            for (User moderator : moderators) {
                requestsByModerator.put(moderator, new ArrayList<>());
            }

            int moderatorCount = moderators.size();
            for (int i = 0; i < moderationRequests.size(); i++) {
                User assignedModerator = moderators.get(i % moderatorCount);
                requestsByModerator.get(assignedModerator).add(moderationRequests.get(i));
            }

            return requestsByModerator;
        } catch (Exception e) {
            logger.error("Ошибка при получении заявок на модерацию", e);
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
            @CacheEvict(value = "productAll", allEntries = true),
            @CacheEvict(value = "store", key = "#id"),
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
        // todo: у модера и админа, роль не должна меняться, при смене статуса магазина
        // возможно в будущем пригодиться ⬇
        // store.getUser().setRole(moderationDTO.getStatus().equals(Store.StoreStatus.ACTIVE) ? User.Role.SELLER : User.Role.BUYER);

        moderationRequest.setStatus(moderationDTO.getStatus());

        HashMap<String, String> updatedFields = new HashMap<>();
        updatedFields.put("status", store.getStatus().name());

        storeRepository.save(store);
        moderationRequestDao.save(moderationRequest);

        return updatedFields;
    }

}




