package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.moderation.dto.ModerationDTO;
import com.quickcart.quickCart.moderation.dto.ModerationRequestDTO;
import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('MODER') or hasAuthority('ADMIN')")
@RequestMapping("api/v1/moderation")
public class ModerationRequestController {
    private final ModerationRequestService moderationRequestService;

    public ModerationRequestController(ModerationRequestService moderationRequestService) {
        this.moderationRequestService = moderationRequestService;
    }


    @GetMapping("/admin/store")
    public ResponseEntity<Map<User, List<ModerationDTO>>> storeAll() {
        Map<User, List<ModerationDTO>> stores = moderationRequestService.getStores();
        return ResponseEntity.ok(stores);
    }


    @GetMapping("/manage/store")
    public ResponseEntity<List<ModerationDTO>> getStoresForModer() {
        List<ModerationDTO> stores = moderationRequestService.getStoresForModer();
        return ResponseEntity.ok(stores);
    }


    @PatchMapping("/manage/store/{id}")
    public HashMap<String, String> changeOfStatus(@PathVariable("id") Long id,
                                                  @Valid @RequestBody ModerationRequestDTO moderationDTO) {
        return moderationRequestService.changeStoreStatus(id, moderationDTO);
    }

}
