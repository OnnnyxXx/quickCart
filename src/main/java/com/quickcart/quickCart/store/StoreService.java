package com.quickcart.quickCart.store;

import com.quickcart.quickCart.moderation.ModerationRequest;
import com.quickcart.quickCart.moderation.ModerationRequestDao;
import com.quickcart.quickCart.moderation.ModerationRequestStatus;
import com.quickcart.quickCart.store.dto.StoreDTO;
import com.quickcart.quickCart.store.dto.StoreDtoUpdate;
import com.quickcart.quickCart.store.dto.StoreWithUserDto;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {
    // TODO Add Redis please

    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    private final ModerationRequestDao moderationRequestDao;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreService(ModerationRequestDao moderationRequestDao, StoreRepository storeRepository, UserRepository userRepository) {
        this.moderationRequestDao = moderationRequestDao;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public void registerStore(StoreDTO storeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        Optional<User> currentUserOP = userRepository.findByEmail(currentUserEmail);
        if (currentUserOP.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован.");
        }

        User currentUser = currentUserOP.get();
        String storeName = storeDTO.getName();
        ModerationRequestStatus status = moderationRequestDao.getStatusByStoreName(storeName);

        if (status == ModerationRequestStatus.PENDING) {
            logger.warn("Попытка регистрации магазина с активной заявкой на модерацию: {}", storeName);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Существует активная заявка на модерацию для этого магазина.");
        } else if (status == ModerationRequestStatus.ACTIVE) {
            createAndSaveStore(storeDTO, currentUser);
        } else if (status == ModerationRequestStatus.BLOCKED) {
            logger.warn("Попытка регистрации заблокированного магазина: {}", storeName);
            throw new IllegalArgumentException("Магазин заблокирован, регистрация невозможна.");
        } else {
            createAndSaveStore(storeDTO, currentUser, ModerationRequestStatus.PENDING.name());
            createModerationRequest(storeDTO, currentUser, storeName);
        }

        logger.info("Магазин успешно зарегистрирован: {}", storeName);
    }

    private Store createAndSaveStore(StoreDTO storeDTO, User currentUser) {
        return createAndSaveStore(storeDTO, currentUser, ModerationRequestStatus.ACTIVE.name());
    }

    private Store createAndSaveStore(StoreDTO storeDTO, User currentUser, String status) {
        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setLocation(storeDTO.getLocation());
        store.setStatus(Store.StoreStatus.valueOf(status));
        store.setRating(0);
        store.setWorkingHours(storeDTO.getWorkingHours());
        store.setLogoUrl(storeDTO.getLogoUrl());
        store.setUser(currentUser);
        return storeRepository.save(store);
    }

    private void createModerationRequest(StoreDTO storeDTO, User currentUser, String storeName) {
        ModerationRequest moderationRequest = new ModerationRequest();
        moderationRequest.setUser(currentUser);
        moderationRequest.setStoreName(storeName);
        moderationRequest.setLocation(storeDTO.getLocation());
        moderationRequest.setDescription(storeDTO.getDescription());
        moderationRequest.setRequestDate(LocalDateTime.now());
        moderationRequest.setStatus(Store.StoreStatus.PENDING); // Статус
        moderationRequestDao.save(moderationRequest);
    }

    public List<StoreWithUserDto> storeList() {
        logger.info("GET Store");
        return storeRepository.findStoreWithUserFullInfo();
    }

    public StoreWithUserDto storeDTO(Long id) {
        return storeRepository.findStoreWithUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + id));
    }

    @Transactional
    public HashMap<String, String> updateStore(Long id, StoreDtoUpdate withUserDto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + id));

        HashMap<String, String> updatedFields = new HashMap<>();
        if (withUserDto.getStoreName() != null) {
            store.setName(withUserDto.getStoreName());
            updatedFields.put("name", withUserDto.getStoreName());
        }

        if (withUserDto.getStoreDescription() != null) {
            store.setDescription(withUserDto.getStoreDescription());
            updatedFields.put("description", withUserDto.getStoreDescription());
        }

        if (withUserDto.getStoreLocation() != null) {
            store.setLocation(withUserDto.getStoreLocation());
            updatedFields.put("location", withUserDto.getStoreLocation());
        }

        if (withUserDto.getStoreUrlLogo() != null) {
            store.setLogoUrl(withUserDto.getStoreUrlLogo());
            updatedFields.put("logo", withUserDto.getStoreUrlLogo());
        }

        storeRepository.save(store);
        logger.info("Updating store with id: {}", id);

        return updatedFields;
    }

}
