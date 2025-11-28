# Kebijakan Penyimpanan Data

## Ringkasan

Aplikasi ini menyimpan data keuangan Anda sesuai dengan ketentuan perpajakan Indonesia. Dokumen ini menjelaskan berapa lama data disimpan dan bagaimana data dikelola.

## Dasar Hukum

Berdasarkan **UU No. 28 Tahun 2007 tentang Ketentuan Umum Perpajakan (KUP)**, catatan keuangan wajib disimpan selama **10 tahun** sejak akhir tahun pajak.

## Periode Penyimpanan

### Data Keuangan (10 Tahun)

Data berikut disimpan selama 10 tahun dan tidak dapat dihapus:

| Jenis Data | Keterangan |
|------------|------------|
| Transaksi | Semua transaksi pendapatan dan pengeluaran |
| Jurnal | Semua entri jurnal (termasuk yang di-void) |
| Invoice | Faktur dan bukti penagihan |
| Laporan Pajak | PPN, PPh 21, PPh 23, dll |
| Bukti Potong | 1721-A1 dan bukti potong lainnya |
| Data Payroll | Gaji, potongan, dan tunjangan karyawan |
| Dokumen Pendukung | Scan nota, kwitansi, dan bukti transaksi |

### Data Klien dan Proyek (10 Tahun)

| Jenis Data | Keterangan |
|------------|------------|
| Data Klien | Nama, alamat, NPWP klien |
| Data Proyek | Informasi proyek dan milestone |
| Kontrak | Dokumen kontrak yang diunggah |

### Data Karyawan (10 Tahun Setelah Berhenti)

| Jenis Data | Keterangan |
|------------|------------|
| Data Pribadi | NIK, NPWP, alamat |
| Riwayat Gaji | Slip gaji dan komponen |
| BPJS | Nomor dan kontribusi BPJS |

### Data Operasional (Dapat Dihapus)

| Jenis Data | Periode | Keterangan |
|------------|---------|------------|
| Log Aktivitas | 2 tahun | Riwayat login dan perubahan |
| Draft Telegram | 90 hari | Struk yang belum diproses |

## Penghapusan Data

### Soft Delete (Default)

Ketika Anda menghapus data melalui aplikasi:
- Data tidak benar-benar dihapus dari database
- Data ditandai sebagai "dihapus" dan disembunyikan dari tampilan
- Data masih dapat dipulihkan jika diperlukan
- Ini memastikan jejak audit tetap lengkap

### Hard Delete

Penghapusan permanen TIDAK tersedia melalui aplikasi untuk data keuangan. Ini sesuai dengan ketentuan perpajakan yang mewajibkan penyimpanan 10 tahun.

## Backup Data

### Jadwal Backup

| Tipe Backup | Retensi |
|-------------|---------|
| Harian | 7 hari terakhir |
| Mingguan | 4 minggu terakhir |
| Bulanan | 12 bulan terakhir |
| Tahunan | 10 tahun |

### Lokasi Backup

Backup disimpan di lokasi terpisah dari server utama untuk keamanan data.

## Ekspor Data

Anda dapat mengekspor data Anda kapan saja:

### Per Laporan
1. Buka halaman laporan yang diinginkan
2. Klik tombol **Export PDF** atau **Export Excel**

### Ekspor Lengkap
Untuk mengekspor seluruh data perusahaan:
1. Buka **Pengaturan** > **Ekspor Data**
2. Pilih periode yang diinginkan
3. Klik **Ekspor Semua**
4. Unduh file ZIP yang berisi semua data

## Hak Anda

### Akses Data
Anda dapat melihat semua data Anda melalui aplikasi.

### Koreksi Data
- Data profil dapat diubah melalui menu Profil
- Data keuangan yang sudah diposting tidak dapat diubah (harus void dan buat baru)

### Penghapusan Data
- Data keuangan dalam periode 10 tahun tidak dapat dihapus (kewajiban hukum)
- Data di luar periode retensi dapat diajukan untuk penghapusan

## Keamanan Data

### Enkripsi
- Koneksi ke aplikasi menggunakan HTTPS (enkripsi dalam transit)
- Backup dienkripsi saat disimpan

### Akses Terbatas
- Setiap pengguna hanya dapat mengakses data sesuai role-nya
- Aktivitas pengguna dicatat dalam log audit

## Kontak

Untuk pertanyaan tentang data Anda, hubungi administrator sistem.

## Pembaruan Kebijakan

| Versi | Tanggal | Perubahan |
|-------|---------|-----------|
| 1.0 | November 2024 | Kebijakan awal |
