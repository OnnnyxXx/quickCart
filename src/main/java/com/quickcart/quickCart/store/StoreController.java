package com.quickcart.quickCart.store;

import com.quickcart.quickCart.store.dto.StoreDTO;
import com.quickcart.quickCart.store.dto.StoreDtoUpdate;
import com.quickcart.quickCart.store.dto.StoreWithUserDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/store")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@RequestBody @Valid StoreDTO storeDTO) {
        storeService.registerStore(storeDTO);
        return new ResponseEntity<>("Магазин отправлен на модерацию", HttpStatus.CREATED);
    }

    @Cacheable(value = "cache1")
    @GetMapping("/all/store")
    public List<StoreWithUserDto> storeList() {
        return storeService.storeList();
    }

    @GetMapping("/{id}")
    public StoreWithUserDto storeDTO(@PathVariable("id") Long id) {
        return storeService.storeDTO(id);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<HashMap<String, String>> updateStore(@PathVariable("id") Long id,
                                                               @Valid @RequestBody StoreDtoUpdate withUserDto) {
        // TODO Проверка на автора!
        HashMap<String, String> result = storeService.updateStore(id, withUserDto);
        return ResponseEntity.ok(result);
    }

}
