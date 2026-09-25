package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.service.CustomerService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer", description = "Registrasi, pencarian, dan pengelolaan data nasabah (customer)")
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    //get all customer with pagination
    @Operation(summary = "Ambil semua customer (paginated)", description = "Mengembalikan daftar customer dengan pagination, bisa difilter dengan keyword. Membutuhkan salah satu role staff atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar customer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.getCustomerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<CustomerResponse.getCustomerResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return ResponseEntity.ok(customerService.findAll(keyword, page, size));
    }

    //get customer by Id
    @Operation(summary = "Ambil customer berdasarkan Id", description = "Mencari satu data customer berdasarkan Id yang dikirim lewat header idCustomerSearch. Membutuhkan salah satu role staff atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.getCustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idCustomerSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idCustomerSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @GetMapping(headers = "idCustomerSearch")
    public ResponseEntity<CustomerResponse.getCustomerResponse> getCustomerById(
            @Parameter(description = "Id customer yang dicari, dikirim lewat header idCustomerSearch") @RequestHeader("idCustomerSearch") Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }



    //add customer
    @Operation(summary = "Registrasi customer baru", description = "Mendaftarkan akun customer baru. Endpoint ini publik (tidak butuh JWT), dipakai untuk fitur registrasi.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer berhasil didaftarkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.getCustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Email harus diisi"))),
            @ApiResponse(responseCode = "409", description = "Email/NIK/username sudah terdaftar (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping
    public ResponseEntity<CustomerResponse.getCustomerResponse> addCustomer(
            @Valid @RequestBody CustomerRequest.customerAddRequest request) {
        return ResponseEntity.ok(customerService.addCustomer(request));
    }

    //update customer
    @Operation(summary = "Update data customer (admin)", description = "Memperbarui data customer berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.customerUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Customer Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "email harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Email/NIK/username sudah dipakai customer lain (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PutMapping
    public ResponseEntity<CustomerResponse.customerUpdateResponse> updateCustomer(
            @RequestParam Integer id,
            @Valid @RequestBody CustomerRequest.customerUpdateRequest request
    ) {
        customerService.updateCustomer(id, request.userName, request.sidName, request.email, request.password,
                request.nik, request.tempatLahir, request.tanggalLahir, request.alamat, request.pekerjaan,
                request.pendapatan, request.maritalStatus, request.gender, request.noRekening);

        CustomerResponse.customerUpdateResponse respUpdate = new CustomerResponse.customerUpdateResponse();
        respUpdate.setMessage("Customer Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //update profil sendiri (dipakai customer, bukan admin) -- id diambil dari JWT
    @Operation(summary = "Update profil sendiri", description = "Memperbarui profil milik customer yang sedang login (Id customer diambil dari JWT, bukan dari request). Hanya bisa diakses oleh CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.customerUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Profil berhasil diperbarui\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Nama harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya CUSTOMER), atau sesi tidak memiliki data user Id valid (body kosong)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @PutMapping("/me")
    public ResponseEntity<CustomerResponse.customerUpdateResponse> updateMyProfile(
            @Valid @RequestBody CustomerRequest.customerSelfUpdateRequest request) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AppUser appUser) || appUser.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        customerService.updateOwnProfile(appUser.getUserId(), request.sidName, request.alamat, request.pekerjaan,
                request.pendapatan, request.maritalStatus, request.noRekening, request.tempatLahir, request.tanggalLahir, request.gender);

        CustomerResponse.customerUpdateResponse respUpdate = new CustomerResponse.customerUpdateResponse();
        respUpdate.setMessage("Profil berhasil diperbarui");
        return ResponseEntity.ok(respUpdate);
    }

    // fcm-token sebagai kunci untuk ngirim notifikasi
    @Operation(summary = "Update FCM token", description = "Menyimpan/memperbarui FCM token milik customer yang sedang login, dipakai sebagai kunci untuk mengirim push notification. Hanya bisa diakses oleh CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token FCM berhasil diperbarui", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.customerUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Token FCM berhasil diperbarui\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "fcmToken harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya CUSTOMER), atau sesi tidak memiliki data user Id valid (body kosong)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @PutMapping("/fcm-token")
    public ResponseEntity<CustomerResponse.customerUpdateResponse> updateFcmToken(
            @Valid @RequestBody CustomerRequest.customerFcmTokenRequest request) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AppUser appUser) || appUser.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        customerService.updateFcmToken(appUser.getUserId(), request.fcmToken);

        CustomerResponse.customerUpdateResponse respUpdate = new CustomerResponse.customerUpdateResponse();
        respUpdate.setMessage("Token FCM berhasil diperbarui");
        return ResponseEntity.ok(respUpdate);
    }

    @Operation(summary = "Hapus FCM token", description = "Menghapus FCM token milik customer yang sedang login (misal saat logout, agar tidak menerima notifikasi lagi). Hanya bisa diakses oleh CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token FCM berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.customerUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Token FCM berhasil dihapus\"}"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya CUSTOMER), atau sesi tidak memiliki data user Id valid (body kosong)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @DeleteMapping("/fcm-token")
    public ResponseEntity<CustomerResponse.customerUpdateResponse> clearFcmToken() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AppUser appUser) || appUser.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        customerService.updateFcmToken(appUser.getUserId(), null);

        CustomerResponse.customerUpdateResponse respUpdate = new CustomerResponse.customerUpdateResponse();
        respUpdate.setMessage("Token FCM berhasil dihapus");
        return ResponseEntity.ok(respUpdate);
    }

    //delete customer
    @Operation(summary = "Hapus customer", description = "Menghapus data customer berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.customerDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete customer successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<CustomerResponse.customerDeleteResponse> deleteCustomer(@RequestParam Integer Id) {
        customerService.deleteCustomer(Id);

        CustomerResponse.customerDeleteResponse respDelete = new CustomerResponse.customerDeleteResponse();
        respDelete.setMessage("Delete customer successfully");
        return ResponseEntity.ok(respDelete);
    }
}
