package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;
import com.project.binar.okariru.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification", description = "Kelola data notifikasi customer. Semua endpoint hanya bisa diakses oleh SUPERADMIN.")
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    //get all notification
    @Operation(summary = "Ambil semua notifikasi", description = "Mengembalikan seluruh data notifikasi di sistem. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar notifikasi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.getNotificationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponse.getNotificationResponse>> findAll() {
        return ResponseEntity.ok(notificationService.getAllNotification());
    }

    //get notification by Id
    @Operation(summary = "Ambil notifikasi berdasarkan Id customer", description = "Mencari daftar notifikasi milik satu customer berdasarkan Id yang dikirim lewat header idNotificationSearch. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar notifikasi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.getNotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idNotificationSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idNotificationSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping(headers = "idNotificationSearch")
    public ResponseEntity<List<NotificationResponse.getNotificationResponse>> getNotificationByCustID(
            @Parameter(description = "Id customer pemilik notifikasi, dikirim lewat header idNotificationSearch") @RequestHeader("idNotificationSearch") Integer id) {
        return ResponseEntity.ok(notificationService.getNotificationByCustomerId(id));
    }

    //add notification
    @Operation(summary = "Tambah notifikasi", description = "Membuat data notifikasi baru untuk seorang customer. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifikasi berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.getNotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "isi notifikasi harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @PostMapping
    public ResponseEntity<NotificationResponse.getNotificationResponse> addNotification(
            @Valid @RequestBody NotificationRequest.notificationAddRequest request) {
        return ResponseEntity.ok(notificationService.addNotification(request));
    }

    //update notification
    @Operation(summary = "Update notifikasi", description = "Memperbarui data notifikasi berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifikasi berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.notificationUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Notification Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "isi notifikasi harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Notifikasi atau customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "notification dengan Id 1 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<NotificationResponse.notificationUpdateResponse> updateNotification(
            @RequestParam Integer id,
            @Valid @RequestBody NotificationRequest.notificationUpdateRequest request
    ) {
        notificationService.updateNotification(id, request.customerId, request.isiNotifikasi);

        NotificationResponse.notificationUpdateResponse respUpdate = new NotificationResponse.notificationUpdateResponse();
        respUpdate.setMessage("Notification Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete notification
    @Operation(summary = "Hapus notifikasi", description = "Menghapus data notifikasi berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifikasi berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.notificationDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete notification successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Notifikasi dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "notification dengan Id 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<NotificationResponse.notificationDeleteResponse> deleteNotification(@RequestParam Integer Id) {
        notificationService.deleteNotification(Id);

        NotificationResponse.notificationDeleteResponse respDelete = new NotificationResponse.notificationDeleteResponse();
        respDelete.setMessage("Delete notification successfully");
        return ResponseEntity.ok(respDelete);
    }
}
