package com.ridehub.driverservice.controller;

import com.ridehub.driverservice.dto.request.DriverAvailabilityUpdateRequest;
import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverAvailabilityResponse;
import com.ridehub.driverservice.dto.response.DriverResponse;
import com.ridehub.driverservice.security.service.JwtService;
import com.ridehub.driverservice.service.interfaces.DriverService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<DriverResponse> registerDriver(
            @Valid @RequestBody DriverRegistrationRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserId(token);

        DriverResponse response =
                driverService.registerDriver(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DriverResponse> getDriverProfile(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserId(token);

        DriverResponse response = driverService.getDriverProfile(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<DriverResponse> updateDriver(
            @Valid @RequestBody DriverUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserId(token);

        DriverResponse response =
                driverService.updateDriver(userId, request);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/me/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @Valid @RequestBody DriverAvailabilityUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserId(token);

        DriverResponse response =
                driverService.updateAvailability(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/availability")
    public ResponseEntity<DriverAvailabilityResponse> getAvailability(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserId(token);

        DriverAvailabilityResponse response =
                driverService.getAvailability(userId);

        return ResponseEntity.ok(response);
    }

}