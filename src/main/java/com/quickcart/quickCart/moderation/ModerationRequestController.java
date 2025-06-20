package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDto;
import com.quickcart.quickCart.moderation.dto.ModerationRequestDto;
import com.quickcart.quickCart.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Moderation", description = "The Moderation API")
@RestController
@PreAuthorize("hasAuthority('MODER') or hasAuthority('ADMIN')")
@RequestMapping("api/v1/moderation")
public class ModerationRequestController {
    private final ModerationRequestService moderationRequestService;

    public ModerationRequestController(ModerationRequestService moderationRequestService) {
        this.moderationRequestService = moderationRequestService;
    }

    @GetMapping("/admin/store")
    public ResponseEntity<Map<User, List<ModerationDto>>> storeAll() {
        Map<User, List<ModerationDto>> stores = moderationRequestService.getStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/manage/store")
    public ResponseEntity<List<ModerationDto>> getStoresForModer() {
        List<ModerationDto> stores = moderationRequestService.getStoresForModer();
        return ResponseEntity.ok(stores);
    }

    @PatchMapping("/manage/store/{id}")
    public HashMap<String, String> changeOfStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody ModerationRequestDto moderationDTO) {
        return moderationRequestService.changeStoreStatus(id, moderationDTO);
    }

}
