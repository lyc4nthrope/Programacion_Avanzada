package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.CreateUserDTO;
import co.edu.uniquindio.application.dto.EditUserDTO;
import co.edu.uniquindio.application.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateUserDTO userDTO) throws Exception{
        //Lógica para crear el usuario
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @PutMapping("/{id}") // Debe coincidir con el nombre del parámetro (id)
    public void edit(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) throws Exception{

    }

    @DeleteMapping("/{id}") // Debe coincidir con el nombre del parámetro (id)
    public void delete(@PathVariable String id) throws Exception{

    }

    @GetMapping("/{id}") // Debe coincidir con el nombre del parámetro (id)
    public UserDTO get(@PathVariable String id) throws Exception{
        return null;
    }

    @GetMapping
    public List<UserDTO> listAll(){
        return null;
    }



}