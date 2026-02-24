package com.artivisi.accountingfinance.functional.taxdetail;

import com.artivisi.accountingfinance.enums.Role;
import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@TestConfiguration
@Profile("functional")
@RequiredArgsConstructor
@Slf4j
public class TaxDetailTestDataInitializer {

    private final DataImportService dataImportService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void importTaxDetailTestData() {
        try {
            // Step 1: Import IT service industry seed data
            log.info("Importing IT Service seed data for tax detail tests...");
            byte[] seedZip = createZipFromDirectory("industry-seed/it-service/seed-data");
            DataImportService.ImportResult seedResult = dataImportService.importAllData(seedZip);
            log.info("Seed data imported: {} records in {}ms", seedResult.totalRecords(), seedResult.durationMs());

            // Step 2: Import test master data (clients, projects with NPWP)
            log.info("Importing test master data...");
            byte[] testDataZip = createZipFromTestData("src/test/resources/testdata/service");
            DataImportService.ImportResult testResult = dataImportService.importAllData(testDataZip);
            log.info("Test master data imported: {} records in {}ms", testResult.totalRecords(), testResult.durationMs());

            // Step 3: Import tax-specific transactions (PPN, PPh 23, non-tax)
            log.info("Importing tax detail test transactions...");
            byte[] taxDataZip = createZipFromTaxTestData("src/test/resources/testdata/taxdetail");
            DataImportService.ImportResult taxResult = dataImportService.importAllData(taxDataZip);
            log.info("Tax test data imported: {} records in {}ms", taxResult.totalRecords(), taxResult.durationMs());

            // Step 4: Create test users
            createTestUsers();

        } catch (Exception e) {
            log.error("Failed to import tax detail test data", e);
            throw new RuntimeException("Tax detail test data initialization failed", e);
        }
    }

    private void createTestUsers() {
        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("password"));
            staff.setFullName("Staff User");
            staff.setEmail("staff@example.com");
            staff.setActive(true);
            staff.addRole(Role.STAFF, "system");
            userRepository.save(staff);
        }
    }

    private byte[] createZipFromTaxTestData(String testDataDir) throws IOException {
        Path testDir = Paths.get(testDataDir).toAbsolutePath();
        if (!Files.exists(testDir)) {
            throw new IOException("Tax test data directory not found: " + testDir);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            addTestFileToZip(zos, testDir, "financial-transactions.csv", "18_transactions.csv");
            addTestFileToZip(zos, testDir, "financial-journal-entries.csv", "20_journal_entries.csv");
        }
        return baos.toByteArray();
    }

    private byte[] createZipFromTestData(String testDataDir) throws IOException {
        Path testDir = Paths.get(testDataDir).toAbsolutePath();
        if (!Files.exists(testDir)) {
            throw new IOException("Test data directory not found: " + testDir);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            addTestFileToZip(zos, testDir, "company-config.csv", "01_company_config.csv");
            addTestFileToZip(zos, testDir, "clients.csv", "07_clients.csv");
            addTestFileToZip(zos, testDir, "projects.csv", "08_projects.csv");
            addTestFileToZip(zos, testDir, "fiscal-periods.csv", "11_fiscal_periods.csv");
            addTestFileToZip(zos, testDir, "employees.csv", "15_employees.csv");
        }
        return baos.toByteArray();
    }

    private void addTestFileToZip(ZipOutputStream zos, Path testDir, String sourceFile, String zipEntry) throws IOException {
        Path filePath = testDir.resolve(sourceFile);
        if (Files.exists(filePath)) {
            ZipEntry entry = new ZipEntry(zipEntry);
            zos.putNextEntry(entry);
            Files.copy(filePath, zos);
            zos.closeEntry();
        }
    }

    private byte[] createZipFromDirectory(String dirPath) throws IOException {
        Path seedDir = Paths.get(dirPath).toAbsolutePath();
        if (!Files.exists(seedDir)) {
            throw new IOException("Seed data directory not found: " + seedDir);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(seedDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            Path relativePath = seedDir.relativize(path);
                            ZipEntry entry = new ZipEntry(relativePath.toString().replace('\\', '/'));
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to zip file: " + path, e);
                        }
                    });
        }
        return baos.toByteArray();
    }
}
