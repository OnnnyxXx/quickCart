package com.quickcart.quickCart.store;

import com.quickcart.quickCart.moderation.ModerationRequest;
import com.quickcart.quickCart.moderation.ModerationRequestDao;
import com.quickcart.quickCart.store.dto.StoreDto;
import com.quickcart.quickCart.store.dto.StoreDtoUpdate;
import com.quickcart.quickCart.store.dto.StoreWithUserDto;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
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

    @Transactional
    public void registerStore(StoreDto storeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        Optional<User> currentUserOP = userRepository.findByEmail(currentUserEmail);
        if (currentUserOP.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован.");
        }

        User currentUser = currentUserOP.get();
        String storeName = storeDTO.getName();
        Optional<String> statusStringOpt = moderationRequestDao.getStatusStringByStoreName(storeName);
        Store.StoreStatus status = statusStringOpt.map(Store.StoreStatus::valueOf).orElse(null);

        if (status != null) {
            switch (status) {
                case ACTIVE:
                    logger.info("Магазин уже активен: {}", storeName);
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Магазин уже активен: " + storeName);
                case BLOCKED:
                    logger.warn("Попытка регистрации заблокированного магазина: {}", storeName);
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Магазин заблокирован, регистрация невозможна.");
                case PENDING:
                    logger.info("Заявка уже существует: {}", storeName);
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Попытка регистрации магазина с активной заявкой на модерацию");
                default:
                    break; // Ничего не делаем, если статус не определён
            }
        }

        Store store = createAndSaveStore(storeDTO, currentUser, storeDTO.getLogo());
        createModerationRequest(storeDTO, currentUser, storeName, store.getLogoUrl());

        logger.info("Магазин передан на модерацию: {}", storeName);
        logger.info("Магазин успешно зарегистрирован: {}", storeName);
    }

    private Store createAndSaveStore(StoreDto storeDTO, User currentUser, MultipartFile logo) {
        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setDescription(storeDTO.getDescription());
        store.setLocation(storeDTO.getLocation());
        store.setStatus(Store.StoreStatus.PENDING);
        store.setRating(0);
        store.setWorkingHours(storeDTO.getWorkingHours());

        if (logo != null && !logo.isEmpty()) {
            String logoUrl = saveLogoAsWebP(logo);
            store.setLogoUrl(logoUrl);
        }

        store.setUser(currentUser);
        return storeRepository.save(store);
    }

    private void createModerationRequest(StoreDto storeDTO, User currentUser, String storeName, String logo) {
        ModerationRequest moderationRequest = new ModerationRequest();
        moderationRequest.setUser(currentUser);
        moderationRequest.setStoreName(storeName);
        moderationRequest.setLogoUrl(logo);
        moderationRequest.setLocation(storeDTO.getLocation());
        moderationRequest.setDescription(storeDTO.getDescription());
        moderationRequest.setRequestDate(LocalDateTime.now());
        moderationRequest.setStatus(Store.StoreStatus.PENDING);
        moderationRequestDao.save(moderationRequest);
    }

    public String saveLogoAsWebP(MultipartFile logo) {
        try {
            String contentType = logo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Файл должен быть изображением.");
            }

            BufferedImage bufferedImage = ImageIO.read(logo.getInputStream());
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Невозможно прочитать изображение");
            }

            String originalFileName = logo.getOriginalFilename();
            String sanitizedFileName = originalFileName != null ? originalFileName.replaceAll("[^a-zA-Z0-9.]", "_") : "logo";
            String fileName = sanitizedFileName + "_" + UUID.randomUUID() + ".webp";

            File outputDir = new File("src/main/resources/static/storeLogo/");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);
            ImageIO.write(bufferedImage, "webp", outputFile);

            return fileName;

        } catch (IOException e) {
            logger.error("Ошибка при сохранении логотипа в формате WebP", e);
            throw new RuntimeException("Ошибка при сохранении логотипа в формате WebP", e);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при обработке изображения: {}", e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Resource> getLogo(String imageName, boolean download) {
        try {
            Path filePath = Paths.get("src/main/resources/static/storeLogo/" + imageName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentDisposition = download
                    ? "attachment; filename=\"" + resource.getFilename() + "\""
                    : "inline; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Ошибка при получении логотипа: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public List<StoreDto> myStore() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return storeRepository.myStore(currentUserEmail);
    }

    @Cacheable(value = "storeAll")
    public List<StoreWithUserDto> storeList() {
        return storeRepository.findStoreWithUserFullInfo();
    }

    @Cacheable(value = "store", key = "#id")
    public StoreWithUserDto storeDTO(Long id) {
        return storeRepository.findStoreWithUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Магазин не найден " + id));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "storeAll", allEntries = true),
            @CacheEvict(value = "store", key = "#id")
    })
    public HashMap<String, String> updateStore(Long id, StoreDtoUpdate withUserDto, MultipartFile logo) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Магазин не найден " + id));

        HashMap<String, String> updatedFields = new HashMap<>();
        if (withUserDto.getStoreName() != null) {
            store.setName(withUserDto.getStoreName());
            updatedFields.put("name", withUserDto.getStoreName());
        }

        if (withUserDto.getStoreDescription() != null) {
            store.setDescription(withUserDto.getStoreDescription());
            updatedFields.put("description", withUserDto.getStoreDescription());
        }
        if (withUserDto.getStoreWorkingHours() != null) {
            store.setWorkingHours(withUserDto.getStoreWorkingHours());
            updatedFields.put("workingHours", withUserDto.getStoreWorkingHours());
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
                logger.error("Error saving logo: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка обновления логотипа");
            }

        }

        storeRepository.save(store);
        logger.info("Updating store with id: {}", id);

        return updatedFields;
    }

    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Магазин с id " + storeId + " не найден."));
    }

    @Caching(evict = {
            @CacheEvict(value = "storeAll", allEntries = true),
            @CacheEvict(value = "store", key = "#id"),
            @CacheEvict(value = "productAll", allEntries = true)
    })
    public void deleted(Long id) {
        logger.info("Запрос на смену статуса id: {}", id);
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка"));

        store.setDeleted(true);
        storeRepository.save(store);
        logger.info("Статус сменен для id: {}", id);
    }

}
