package com.ridehub.driverservice.repository;

import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByUserId(Long userId);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    Optional<Driver> findByVehicleNumber(String vehicleNumber);

    boolean existsByUserId(Long userId);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    List<Driver> findByStatus(DriverStatus status);

}