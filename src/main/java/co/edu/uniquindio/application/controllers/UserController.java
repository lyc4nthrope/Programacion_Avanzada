package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.create.CreateUserDTO;
import co.edu.uniquindio.application.dto.edit.EditUserDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import co.edu.uniquindio.application.models.enums.UserStatus;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateUserDTO userDTO) throws Exception {
        userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> get(@PathVariable String id) throws Exception {
        UserDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        userService.delete(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido eliminado"));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserDTO>>> listAll(
            @RequestParam(required = false) UserStatus status
    ) {
        List<UserDTO> list;

        if (status != null) {
            list = userService.listByStatus(status);
        } else {
            list = userService.listAll();
        }

        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) throws Exception {
        userService.edit(id, userDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido actualizado"));
    }

    // ✅ NUEVOS ENDPOINTS PARA GESTIONAR STATUS

    @GetMapping("/active")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> listActive() {
        List<UserDTO> list = userService.listActiveUsers();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ResponseDTO<String>> activate(@PathVariable String id) throws Exception {
        userService.activateUser(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido activado"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ResponseDTO<String>> deactivate(@PathVariable String id) throws Exception {
        userService.deactivateUser(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido desactivado"));
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<ResponseDTO<String>> softDelete(@PathVariable String id) throws Exception {
        userService.softDeleteUser(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido marcado como eliminado"));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseDTO<UserDTO>> getByEmail(@PathVariable String email) throws Exception {
        UserDTO userDTO = userService.getByEmail(email);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @GetMapping("/email/{email}/exists")
    public ResponseEntity<ResponseDTO<Boolean>> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(new ResponseDTO<>(false, exists));
    }
}