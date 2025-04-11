package com.quickcart.quickCart.store;

import com.quickcart.quickCart.securityService.StoreSecurityService;
import com.quickcart.quickCart.store.dto.StoreDTO;
import com.quickcart.quickCart.store.dto.StoreDtoUpdate;
import com.quickcart.quickCart.store.dto.StoreWithUserDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/store")
public class StoreController {

    @Autowired
    private StoreSecurityService storeSecurityService;

    @Autowired
    private StoreRepository storeRepository;

    private final ResourceLoader resourceLoader;
    private final StoreService storeService;

    public StoreController(ResourceLoader resourceLoader, StoreService storeService) {
        this.resourceLoader = resourceLoader;
        this.storeService = storeService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@ModelAttribute @Valid StoreDTO storeDTO) {
        storeService.registerStore(storeDTO);
        return new ResponseEntity<>("Магазин отправлен на модерацию", HttpStatus.CREATED);
    }

    @GetMapping("/my/store")
    public List<StoreDTO> myStore() {
        return storeService.myStore();
    }

    @GetMapping("/storeLogo/{imageName:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String imageName,
                                             @RequestParam(required = false, defaultValue = "false")
                                             boolean download) {
        return storeService.getLogo(imageName, download);
    }

    @GetMapping("/all/store")
    public List<StoreWithUserDto> storeList() {
        /// Only ACTIVE
        return storeService.storeList();
    }

    @GetMapping("/{id}")
    public StoreWithUserDto storeDTO(@PathVariable("id") Long id) {
        return storeService.storeDTO(id);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("@storeSecurityService.isOwner(#id, authentication.name)")
    public ResponseEntity<HashMap<String, String>> updateStore(
            @PathVariable("id") Long id,
            @ModelAttribute @Valid StoreDtoUpdate withUserDto,
            @RequestParam(required = false) MultipartFile logo) {

        HashMap<String, String> result = storeService.updateStore(id, withUserDto, logo);
        return ResponseEntity.ok(result);
    }

}
