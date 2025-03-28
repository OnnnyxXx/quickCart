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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class StoreService {

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

        Store store = createAndSaveStore(storeDTO, currentUser, storeDTO.getLogo());


        if (status == ModerationRequestStatus.ACTIVE) {
            logger.info("Магазин уже активен: {}", storeName);
        } else if (status == ModerationRequestStatus.BLOCKED) {
            logger.warn("Попытка регистрации заблокированного магазина: {}", storeName);
            throw new IllegalArgumentException("Магазин заблокирован, регистрация невозможна.");
        } else if (status == ModerationRequestStatus.PENDING) {
            logger.info("Заявка уже существует: {}", storeName);
            throw new IllegalArgumentException("Попытка регистрации магазина с активной заявкой на модерацию");
        } else {
            createModerationRequest(storeDTO, currentUser, storeName, store.getLogoUrl());
            logger.info("Магазин передан на модерацию: {}", storeName);
        }
        logger.info("Магазин успешно зарегистрирован: {}", storeName);
    }


    private Store createAndSaveStore(StoreDTO storeDTO, User currentUser, String status, MultipartFile logo) {
        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setLocation(storeDTO.getLocation());
        store.setStatus(Store.StoreStatus.valueOf(status));
        store.setRating(0);
        store.setWorkingHours(storeDTO.getWorkingHours());

        if (logo != null && !logo.isEmpty()) {
            String logoUrl = saveLogoAsWebP(logo);
            store.setLogoUrl(logoUrl);
        }

        store.setUser(currentUser);
        return storeRepository.save(store);
    }


    private Store createAndSaveStore(StoreDTO storeDTO, User currentUser, MultipartFile logo) {
        return createAndSaveStore(storeDTO, currentUser, Store.StoreStatus.PENDING.name(), logo);
    }

    private void createModerationRequest(StoreDTO storeDTO, User currentUser, String storeName, String logo) {
        ModerationRequest moderationRequest = new ModerationRequest();
        moderationRequest.setUser(currentUser);
        moderationRequest.setStoreName(storeName);
        moderationRequest.setLogoUrl(logo);
        moderationRequest.setLocation(storeDTO.getLocation());
        moderationRequest.setDescription(storeDTO.getDescription());
        moderationRequest.setRequestDate(LocalDateTime.now());
        moderationRequest.setStatus(Store.StoreStatus.PENDING); // Статус
        moderationRequestDao.save(moderationRequest);
    }


    public String saveLogoAsWebP(MultipartFile logo) {
        try {
            String contentType = logo.getContentType();
            if (!contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Файл должен быть изображением.");
            }

            BufferedImage bufferedImage = ImageIO.read(logo.getInputStream());
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Невозможно прочитать изображения");
            }

            String originalFileName = logo.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
            String fileName = sanitizedFileName + "_" + UUID.randomUUID() + ".webp";
            File outputFile = new File("src/main/resources/static/storeLogo/" + fileName);

            ImageIO.write(bufferedImage, "webp", outputFile);

            return "/storeLogo/" + fileName;

        } catch (IOException e) {
            logger.error("Ошибка при сохранении логотипа в формате WebP", e);
            throw new RuntimeException("Ошибка при сохранении логотипа в формате WebP");
        }
    }

    @Cacheable(value = "allStore")
    public List<StoreWithUserDto> storeList() {
        return storeRepository.findStoreWithUserFullInfo();
    }

    @Cacheable(value = "store", key = "#id")
    public StoreWithUserDto storeDTO(Long id) {
        return storeRepository.findStoreWithUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + id));
    }

    @CacheEvict(value = "store", key = "#id")
    @Transactional
    public HashMap<String, String> updateStore(Long id, StoreDtoUpdate withUserDto, MultipartFile logo) {
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

        if (logo != null && !logo.isEmpty()) {
            try {
                String storeLogoUrl = saveLogoAsWebP(logo);
                store.setLogoUrl(storeLogoUrl);
                updatedFields.put("logo", storeLogoUrl);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error saving logo: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка обновления логотипа");
            }

        }

        storeRepository.save(store);
        logger.info("Updating store with id: {}", id);

        return updatedFields;
    }


    public Store getStoreById(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        return store.orElse(null);
    }
}
