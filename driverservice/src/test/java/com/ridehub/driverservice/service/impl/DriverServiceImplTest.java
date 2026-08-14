package com.ridehub.driverservice.service.impl;

import com.ridehub.driverservice.dto.request.DriverAvailabilityUpdateRequest;
import com.ridehub.driverservice.dto.request.DriverRegistrationRequest;
import com.ridehub.driverservice.dto.request.DriverUpdateRequest;
import com.ridehub.driverservice.dto.response.DriverAvailabilityResponse;
import com.ridehub.driverservice.dto.response.DriverResponse;
import com.ridehub.driverservice.entity.Driver;
import com.ridehub.driverservice.enums.AvailabilityStatus;
import com.ridehub.driverservice.enums.DriverStatus;
import com.ridehub.driverservice.exception.BusinessRuleViolationException;
import com.ridehub.driverservice.exception.DuplicateResourceException;
import com.ridehub.driverservice.exception.ResourceNotFoundException;
import com.ridehub.driverservice.kafka.dto.DriverAvailabilityChangedEvent;
import com.ridehub.driverservice.kafka.dto.DriverRegisteredEvent;
import com.ridehub.driverservice.kafka.publisher.DriverEventPublisher;
import com.ridehub.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Driver Service Unit Tests")
class DriverServiceImplTest {

    @Mock
    private  DriverRepository driverRepository;

    @Mock
    private  DriverEventPublisher driverEventPublisher;

    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverRegistrationRequest registrationRequest;
    private DriverUpdateRequest updateRequest;
    private DriverAvailabilityUpdateRequest availabilityUpdateRequest;

    private Long userId;
    private Driver driver;

    @BeforeEach
    void setUp(){

        userId = 1L;

        registrationRequest = DriverRegistrationRequest.builder()
                .licenseNumber("licence-number")
                .vehicleColor("Black")
                .vehicleModel("Honda")
                .experienceYears(1)
                .vehicleNumber("v-number")
                .vehicleType("v-type")
                .build();

          updateRequest = DriverUpdateRequest.builder()
                .vehicleColor("Black")
                .vehicleModel("Unicorn")
                .experienceYears(2)
                .vehicleNumber("v-number")
                .vehicleType("v-type")
                .build();

        driver = Driver.builder()
                .id(1L)
                .licenseNumber("licence-number")
                .vehicleNumber("v-number")
                .vehicleType("v-type")
                .vehicleModel("Honda")
                .vehicleColor("black")
                .experienceYears(1)
                .availability(AvailabilityStatus.OFFLINE)
                .status(DriverStatus.APPROVED)
                .build();

        availabilityUpdateRequest = DriverAvailabilityUpdateRequest.builder()
                .availability(AvailabilityStatus.ONLINE)
                .build();
    }

    @Nested
    @DisplayName("Driver Registration Tests")
    class DriverRegistrationTests{

        @Test
        @DisplayName("Register Driver successfully on valid registration request")
        void registerDriverSuccessfully(){

            when(driverRepository.existsByUserId(userId)).thenReturn(false);
            when(driverRepository.existsByLicenseNumber(registrationRequest.getLicenseNumber())).thenReturn(false);
            when(driverRepository.existsByVehicleNumber(registrationRequest.getVehicleNumber())).thenReturn(false);
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            doNothing().when(driverEventPublisher).publishDriverRegistered(any(DriverRegisteredEvent.class));

            DriverResponse response = driverService.registerDriver(userId, registrationRequest);

            assertEquals(response.getId(), driver.getId());
            assertEquals(response.getAvailability(), driver.getAvailability());

            verify(driverEventPublisher).publishDriverRegistered(any(DriverRegisteredEvent.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException if a driver exists with same userId")
        void shouldThrowDuplicateResourceExceptionWhenUserIdExists(){
            when(driverRepository.existsByUserId(userId)).thenReturn(true);
            assertThrows(DuplicateResourceException.class, () -> driverService.registerDriver(userId, registrationRequest));
            verify(driverRepository, never()).save(any(Driver.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException if a driver exists with same License number ")
        void shouldThrowDuplicateResourceExceptionWhenLicenseNumberExists(){
            when(driverRepository.existsByLicenseNumber(registrationRequest.getLicenseNumber())).thenReturn(true);
            assertThrows(DuplicateResourceException.class, () -> driverService.registerDriver(userId, registrationRequest));
            verify(driverRepository, never()).save(any(Driver.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException if a driver exists with same vehicle number")
        void shouldThrowDuplicateResourceExceptionWhenVehicleNumberExists(){
            when(driverRepository.existsByVehicleNumber(registrationRequest.getVehicleNumber())).thenReturn(true);
            assertThrows(DuplicateResourceException.class, () -> driverService.registerDriver(userId, registrationRequest));
            verify(driverRepository, never()).save(any(Driver.class));
        }
    }

    @Nested
    @DisplayName("Update Driver Tests")
    class UpdateDriverTests{

        @Test
        @DisplayName("Driver update successful when valid update request")
        void driverUpdateSuccessful(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            when(driverRepository.save(driver)).thenReturn(driver);

            DriverResponse response =
                    driverService.updateDriver(userId, updateRequest);

            assertEquals(updateRequest.getVehicleNumber(), response.getVehicleNumber());
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException driver id does not exist")
        void shouldThrowResourceNotFoundExceptionWhenDriverIdNotFound(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class , () -> driverService.updateDriver(userId, updateRequest));
            verify(driverRepository, never()).save(any(Driver.class));
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when the vehicle number already exists")
        void shouldThrowDuplicateResourceExceptionWhenVNExistsOrSame(){
            when(driverRepository.findByUserId(userId))
                    .thenReturn(Optional.of(driver));

            driver.setVehicleNumber("OLD123");

            updateRequest.setVehicleNumber("NEW456");

            when(driverRepository.existsByVehicleNumber(updateRequest.getVehicleNumber())).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> driverService.updateDriver(userId, updateRequest));
            verify(driverRepository, never()).save(any(Driver.class));
        }

        @Test
        @DisplayName("should update successfully when vehicle number remains unchanged")
        void shouldUpdateSuccessfullyWhenVehicleNumberIsUnchanged() {

            when(driverRepository.findByUserId(userId))
                    .thenReturn(Optional.of(driver));

            when(driverRepository.save(driver))
                    .thenReturn(driver);

            DriverResponse response =
                    driverService.updateDriver(userId, updateRequest);

            assertEquals(
                    driver.getVehicleNumber(),
                    response.getVehicleNumber()
            );

            verify(driverRepository, never())
                    .existsByVehicleNumber(anyString());

            verify(driverRepository)
                    .save(driver);
        }
    }

    @Nested
    @DisplayName("Get Driver Tests")
    class GetDriverTests{
        @Test
        @DisplayName("Fetching driver details for existing driver")
        void getDriverProfileSuccessfully(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            DriverResponse response = driverService.getDriverProfile(userId);

            assertEquals(response.getStatus(), driver.getStatus());
            assertEquals(response.getId(), driver.getId());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for fetching driver profile for non-existing driver")
        void shouldThrowResourceNotFoundExceptionWhenDriverNotFound(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,() -> driverService.getDriverProfile(userId));
        }
    }

    @Nested
    @DisplayName("Delete Driver Tests")
    class DeleteDriverTests{

        @Test
        @DisplayName("should delete driver successfully when driver exists")
        void deleteDriverSuccessfully(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            driverService.deleteDriver(userId);

            verify(driverRepository).delete(driver);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when driver does not exist")
        void shouldThrowResourceNotFoundExceptionWhenDriverNotFound(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> driverService.deleteDriver(userId));
            verify(driverRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Update Driver Availability Tests")
    class UpdateDriverAvailabilityTests {
        @Test
        @DisplayName("should successfully update the availability of driver")
        void shouldSuccessfullyUpdateDriverAvailability(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            when(driverRepository.save(driver)).thenReturn(driver);
            doNothing().when(driverEventPublisher).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
            DriverResponse response = driverService.updateAvailability(userId, availabilityUpdateRequest);


            assertEquals(driver.getAvailability(), response.getAvailability());
            verify(driverEventPublisher).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
            verify(driverRepository).save(any(Driver.class));
        }

        @Test
        @DisplayName("Driver must be approved before moving online else throw BusinessRuleViolationException")
        void shouldThrowBusinessRuleViolationExceptionIfDriverNotApproved() {
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            driver.setStatus(DriverStatus.PENDING);
            assertThrows(BusinessRuleViolationException.class, () -> driverService.updateAvailability(userId, availabilityUpdateRequest));

            verify(driverRepository, never()).save(any(Driver.class));
            verify(driverEventPublisher, never()).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
        }

        @Test
        @DisplayName("should reject going offline while driver is on trip")
        void shouldThrowBusinessRuleViolationExceptionWhileDrivingOffline(){
            availabilityUpdateRequest.setAvailability(AvailabilityStatus.OFFLINE);
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            driver.setAvailability(AvailabilityStatus.ON_TRIP);

            assertThrows(BusinessRuleViolationException.class, () -> driverService.updateAvailability(userId, availabilityUpdateRequest));

            verify(driverRepository, never()).save(any(Driver.class));
            verify(driverEventPublisher, never()).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
        }

        @Test
        @DisplayName("should reject going online while driver is on trip")
        void shouldThrowBusinessRuleViolationExceptionWhileDrivingOnline(){
            availabilityUpdateRequest.setAvailability(AvailabilityStatus.ONLINE);
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            driver.setAvailability(AvailabilityStatus.ON_TRIP);

            assertThrows(BusinessRuleViolationException.class, () -> driverService.updateAvailability(userId, availabilityUpdateRequest));

            verify(driverRepository, never()).save(any(Driver.class));
            verify(driverEventPublisher, never()).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
        }

        @Test
        @DisplayName("should reject request same as driver availability")
        void shouldThrowBusinessRuleViolationExceptionForSameRequest(){
            availabilityUpdateRequest.setAvailability(AvailabilityStatus.ONLINE);
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            driver.setAvailability(AvailabilityStatus.ONLINE);

            assertThrows(BusinessRuleViolationException.class, () -> driverService.updateAvailability(userId, availabilityUpdateRequest));

            verify(driverRepository, never()).save(any(Driver.class));
            verify(driverEventPublisher, never()).publishDriverAvailabilityChanged(any(DriverAvailabilityChangedEvent.class));
        }
    }

    @Nested
    @DisplayName("Get Driver Availability Tests")
    class GetDriverAvailabilityTests{

        @Test
        @DisplayName("Fetching availability for existing driver")
        void getDriverAvailabilitySuccessfully(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.of(driver));
            DriverAvailabilityResponse response = driverService.getAvailability(userId);
            assertEquals(response.getAvailability(), driver.getAvailability());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for fetching availability for non-existing driver")
        void shouldThrowResourceNotFoundExceptionWhenDriverNotFound(){
            when(driverRepository.findByUserId(userId)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,() -> driverService.getAvailability(userId));
        }
    }
}