package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.NotificationEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.NotificationRepository;
import com.project.binar.okariru.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<NotificationResponse.getNotificationResponse> getAllNotification() {
        return notificationRepository.findAll()
                .stream()
                .map(notification -> new NotificationResponse.getNotificationResponse(
                        notification.getNotifId(),
                        notification.getCustomer().getCustomerId(),
                        notification.getIsiNotifikasi(),
                        notification.getTanggalNotifikasi()
                ))
                .toList();
    }


//    @Override
//    public NotificationResponse.getNotificationResponse getNotificationCustomerId(Integer id) {
//        NotificationEntity notification = notificationRepository.findByCustomer_CustomerId(id)
//                .orElseThrow(() -> new EntityNotFoundException("customer dengan Id " + id + " tidak ditemukan"));
//        return new NotificationResponse.getNotificationResponse(
//                notification.getNotifId(),
//                notification.getCustomer().getCustomerId(),
//                notification.getIsiNotifikasi(),
//                notification.getTanggalNotifikasi()
//        );
//    }

    @Override
    public List<NotificationResponse.getNotificationResponse> getNotificationByCustomerId(Integer id) {

        List<NotificationEntity> notificationSearch = notificationRepository.findByCustomer_CustomerId(id);

        if (notificationSearch.isEmpty()) {
            throw new EntityNotFoundException("Notifikasi untuk customer dengan Id " + id + " tidak ditemukan");
        }

       return notificationRepository.findByCustomer_CustomerId(id)
               .stream()
               .map(notifList -> new NotificationResponse.getNotificationResponse(
                       notifList.getNotifId(),
                       notifList.getCustomer().getCustomerId(),
                       notifList.getIsiNotifikasi(),
                       notifList.getTanggalNotifikasi()
               ))
               .collect(Collectors.toList());

    }

    @Override
    public NotificationResponse.getNotificationResponse addNotification(NotificationRequest.notificationAddRequest addRequest) {
        CustomerEntity customer = customerRepository.findById(addRequest.customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + addRequest.customerId + " tidak ditemukan"));

        NotificationEntity notification = new NotificationEntity();
        notification.setCustomer(customer);
        notification.setIsiNotifikasi(addRequest.isiNotifikasi);
        notification.setTanggalNotifikasi(LocalDate.now());

        NotificationEntity saved = notificationRepository.save(notification);
        return new NotificationResponse.getNotificationResponse(
                saved.getNotifId(),
                saved.getCustomer().getCustomerId(),
                saved.getIsiNotifikasi(),
                saved.getTanggalNotifikasi()
        );
    }

    @Override
    @Transactional
    public void updateNotification(Integer id, Integer customerId, String isiNotifikasi) {
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(id);

        if (notificationOpt.isEmpty()) {
            throw new EntityNotFoundException("notification id tidak ditemukan");
        }

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + customerId + " tidak ditemukan"));

        NotificationEntity notificationUpdate = notificationOpt.get();
        notificationUpdate.setCustomer(customer);
        notificationUpdate.setIsiNotifikasi(isiNotifikasi);

        notificationRepository.save(notificationUpdate);
    }

    @Override
    public String deleteNotification(Integer id) {
        NotificationEntity notificationDelete = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("notification id: " + id + " tidak ditemukan"));

        notificationRepository.delete(notificationDelete);

        return "notification dengan ID: " + id + " Telah di hapus";
    }
}
