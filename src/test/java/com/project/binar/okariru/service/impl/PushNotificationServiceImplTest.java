package com.project.binar.okariru.service.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PushNotificationServiceImpl service;

    private CustomerEntity customerWithToken(String token) {
        CustomerEntity c = new CustomerEntity();
        c.setCustomerId(7);
        c.setFcmToken(token);
        return c;
    }

    @Test
    void sendToCustomer_firebaseBelumTerinisialisasi_dilewati() {
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of());

            service.sendToCustomer(7, "t", "b", "ch", null);

            verifyNoInteractions(customerRepository);
        }
    }

    @Test
    void sendToCustomer_customerTidakDitemukan_dilewati() {
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            when(customerRepository.findById(7)).thenReturn(Optional.empty());

            service.sendToCustomer(7, "t", "b", "ch", null);

            messaging.verifyNoInteractions();
        }
    }

    @Test
    void sendToCustomer_tokenNull_dilewati() {
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            when(customerRepository.findById(7)).thenReturn(Optional.of(customerWithToken(null)));

            service.sendToCustomer(7, "t", "b", "ch", null);

            messaging.verifyNoInteractions();
        }
    }

    @Test
    void sendToCustomer_tokenKosong_dilewati() {
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            when(customerRepository.findById(7)).thenReturn(Optional.of(customerWithToken("   ")));

            service.sendToCustomer(7, "t", "b", "ch", null);

            messaging.verifyNoInteractions();
        }
    }

    @Test
    void sendToCustomer_berhasil_denganDeepLink() throws Exception {
        FirebaseMessaging fcm = mock(FirebaseMessaging.class);
        when(fcm.send(any(Message.class))).thenReturn("msg-1");
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            messaging.when(FirebaseMessaging::getInstance).thenReturn(fcm);
            when(customerRepository.findById(7)).thenReturn(Optional.of(customerWithToken("token-abc")));

            service.sendToCustomer(7, "Judul", "Isi", "pinjaman", "app://pinjaman/1");

            verify(fcm).send(any(Message.class));
        }
    }

    @Test
    void sendToCustomer_berhasil_tanpaDeepLink() throws Exception {
        FirebaseMessaging fcm = mock(FirebaseMessaging.class);
        when(fcm.send(any(Message.class))).thenReturn("msg-2");
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            messaging.when(FirebaseMessaging::getInstance).thenReturn(fcm);
            when(customerRepository.findById(7)).thenReturn(Optional.of(customerWithToken("token-abc")));

            service.sendToCustomer(7, "Judul", "Isi", "pinjaman", null);

            verify(fcm).send(any(Message.class));
        }
    }

    @Test
    void sendToCustomer_firebaseMelemparException_tidakMerambat() throws Exception {
        FirebaseMessaging fcm = mock(FirebaseMessaging.class);
        when(fcm.send(any(Message.class))).thenThrow(mock(FirebaseMessagingException.class));
        try (MockedStatic<FirebaseApp> app = mockStatic(FirebaseApp.class);
             MockedStatic<FirebaseMessaging> messaging = mockStatic(FirebaseMessaging.class)) {
            app.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));
            messaging.when(FirebaseMessaging::getInstance).thenReturn(fcm);
            when(customerRepository.findById(7)).thenReturn(Optional.of(customerWithToken("token-abc")));

            assertDoesNotThrow(() -> service.sendToCustomer(7, "t", "b", "ch", null));
        }
    }
}
