package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.NotificationEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private NotificationServiceImpl service;

    private CustomerEntity customer;
    private NotificationEntity notification;

    @BeforeEach
    void setUp() {
        customer = new CustomerEntity();
        customer.setCustomerId(7);

        notification = new NotificationEntity();
        notification.setNotifId(3);
        notification.setCustomer(customer);
        notification.setIsiNotifikasi("Pengajuan disetujui");
        notification.setTanggalNotifikasi(LocalDate.of(2026, 1, 1));
    }

    @Test
    void getAllNotification_memetakanSemua() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationResponse.getNotificationResponse> result = service.getAllNotification();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getNotifId());
        assertEquals(7, result.get(0).getCustomerId());
    }

    @Test
    void getNotificationByCustomerId_ditemukan() {
        when(notificationRepository.findByCustomer_CustomerId(7)).thenReturn(List.of(notification));

        List<NotificationResponse.getNotificationResponse> result = service.getNotificationByCustomerId(7);

        assertEquals(1, result.size());
        assertEquals("Pengajuan disetujui", result.get(0).getIsiNotifikasi());
    }

    @Test
    void getNotificationByCustomerId_kosong_melemparNotFound() {
        when(notificationRepository.findByCustomer_CustomerId(7)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> service.getNotificationByCustomerId(7));
    }

    @Test
    void addNotification_berhasil() {
        NotificationRequest.notificationAddRequest req = new NotificationRequest.notificationAddRequest();
        req.customerId = 7;
        req.isiNotifikasi = "Halo";
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse.getNotificationResponse r = service.addNotification(req);

        assertEquals(7, r.getCustomerId());
        assertEquals("Halo", r.getIsiNotifikasi());
        assertEquals(LocalDate.now(), r.getTanggalNotifikasi());
    }

    @Test
    void addNotification_customerTidakDitemukan() {
        NotificationRequest.notificationAddRequest req = new NotificationRequest.notificationAddRequest();
        req.customerId = 7;
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addNotification(req));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void updateNotification_berhasil() {
        when(notificationRepository.findById(3)).thenReturn(Optional.of(notification));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        service.updateNotification(3, 7, "Isi baru");

        assertEquals("Isi baru", notification.getIsiNotifikasi());
        verify(notificationRepository).save(notification);
    }

    @Test
    void updateNotification_notifTidakDitemukan() {
        when(notificationRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateNotification(3, 7, "x"));
    }

    @Test
    void updateNotification_customerTidakDitemukan() {
        when(notificationRepository.findById(3)).thenReturn(Optional.of(notification));
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateNotification(3, 7, "x"));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void deleteNotification_berhasil() {
        when(notificationRepository.findById(3)).thenReturn(Optional.of(notification));

        String msg = service.deleteNotification(3);

        assertTrue(msg.contains("3"));
        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_tidakDitemukan() {
        when(notificationRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteNotification(3));
    }
}
