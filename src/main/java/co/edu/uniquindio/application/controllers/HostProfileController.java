package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateHostProfileDTO;
import co.edu.uniquindio.application.dto.edit.EditHostProfileDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.HostProfileDTO;
import co.edu.uniquindio.application.services.HostProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/host-profiles")
@RequiredArgsConstructor
public class HostProfileController {

    private final HostProfileService hostProfileService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateHostProfileDTO hostProfileDTO) throws Exception {
        hostProfileService.create(hostProfileDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "El perfil de anfitrión ha sido creado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<HostProfileDTO>> get(@PathVariable String id) throws Exception {
        HostProfileDTO hostProfileDTO = hostProfileService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, hostProfileDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<HostProfileDTO>> getByUser(@PathVariable String userId) throws Exception {
        HostProfileDTO hostProfileDTO = hostProfileService.getByUser(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, hostProfileDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        hostProfileService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El perfil de anfitrión ha sido eliminado"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<HostProfileDTO>>> listAll() {
        List<HostProfileDTO> list = hostProfileService.listAll();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditHostProfileDTO hostProfileDTO) throws Exception {
        hostProfileService.edit(id, hostProfileDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El perfil de anfitrión ha sido actualizado"));
    }

    @GetMapping("/user/{userId}/check")
    public ResponseEntity<ResponseDTO<Boolean>> hasHostProfile(@PathVariable String userId) {
        boolean hasProfile = hostProfileService.hasHostProfile(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, hasProfile));
    }

    @GetMapping("/user/{userId}/info")
    public ResponseEntity<ResponseDTO<HostProfileDTO>> getHostInfo(@PathVariable String userId) throws Exception {
        HostProfileDTO hostProfileDTO = hostProfileService.getHostInfo(userId);
        return ResponseEntity.ok(new ResponseDTO<>(false, hostProfileDTO));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        List<HostProfileDTO> allProfiles = hostProfileService.listAll();
        stats.put("total_host_profiles", allProfiles.size());
        return ResponseEntity.ok(new ResponseDTO<>(false, stats));
    }
}