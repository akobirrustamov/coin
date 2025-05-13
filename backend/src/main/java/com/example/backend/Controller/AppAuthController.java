package com.example.backend.Controller;

import com.example.backend.DTO.ReqStudentLogin;
import com.example.backend.DTO.UserDTO;
import com.example.backend.Entity.Administrator;
import com.example.backend.Entity.Role;
import com.example.backend.Enums.UserRoles;
import com.example.backend.Repository.AdministratorRepo;
import com.example.backend.Repository.RoleRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/app/auth")
public class AppAuthController {

    private final AdministratorRepo administratorRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ReqStudentLogin user) {
        Administrator administrator = administratorRepo.findAllByLogin(user.getLogin());
        if (administrator == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Administrator not found"));
        }
        if (passwordEncoder.matches(user.getPassword(), administrator.getPassword())) {
            String jwt = generateJwtToken(administrator);
            Role role = roleRepo.findByName(UserRoles.ROLE_ADMINISTRATOR);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("role", role.getName());
            response.put("administratorId", administrator.getId());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
    }
    private String generateJwtToken(Administrator administrator) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setSubject(administrator.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 6000*24*30))
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode("333aae7133c19eda8f7f61ce07e64281c295df67681b1ed47c9c270a488f94d0");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
