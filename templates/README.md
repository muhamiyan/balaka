# Template Packages

Kumpulan template Chart of Accounts (COA) dan Journal Templates untuk berbagai jenis perusahaan.

## Available Packages

| Package | Version | Description |
|---------|---------|-------------|
| `artivisi` | 2.0 | PT Artivisi Intermedia - perusahaan jasa IT (konsultasi, development, training, remittance) |

## Package Structure

Setiap package harus memiliki struktur folder sebagai berikut:

```
templates/
├── README.md                    # File ini
└── [company-name]/
    ├── coa.json                 # Chart of Accounts
    └── journal-templates.json   # Journal Templates
```

## File Schemas

### coa.json

```json
{
  "name": "Package Name",
  "version": "1.0",
  "description": "Package description",
  "accounts": [
    {
      "code": "1",
      "name": "ASET",
      "type": "ASSET",
      "normalBalance": "DEBIT",
      "parentCode": null,
      "isHeader": true,
      "isPermanent": true,
      "description": "Optional description"
    }
  ]
}
```

#### Account Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `code` | Yes | String | Kode akun unik (e.g., "1.1.01") |
| `name` | Yes | String | Nama akun |
| `type` | Yes | Enum | `ASSET`, `LIABILITY`, `EQUITY`, `REVENUE`, `EXPENSE` |
| `normalBalance` | Yes | Enum | `DEBIT`, `CREDIT` |
| `parentCode` | No | String | Kode akun parent (untuk hierarki) |
| `isHeader` | No | Boolean | `true` jika akun ini adalah header/group (default: `false`) |
| `isPermanent` | No | Boolean | `true` untuk akun permanen, `false` untuk nominal (default: `true`) |
| `description` | No | String | Keterangan tambahan |

### journal-templates.json

```json
{
  "name": "Package Name",
  "version": "1.0",
  "description": "Package description",
  "templates": [
    {
      "name": "Template Name",
      "category": "EXPENSE",
      "cashFlowCategory": "OPERATING",
      "templateType": "SIMPLE",
      "description": "Template description",
      "tags": ["tag1", "tag2"],
      "lines": [
        {
          "accountCode": "5.1.01",
          "position": "DEBIT",
          "formula": "amount",
          "description": "Line description"
        }
      ]
    }
  ]
}
```

#### Template Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `name` | Yes | String | Nama template unik |
| `category` | Yes | Enum | `INCOME`, `EXPENSE`, `PAYMENT`, `RECEIPT`, `TRANSFER` |
| `cashFlowCategory` | Yes | Enum | `OPERATING`, `INVESTING`, `FINANCING` |
| `templateType` | No | Enum | `SIMPLE`, `DETAILED` (default: `SIMPLE`) |
| `description` | No | String | Keterangan template |
| `tags` | No | Array | Tags untuk pencarian (lowercase) |
| `lines` | Yes | Array | Minimal 2 baris (debit dan credit) |

#### Template Line Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `accountCode` | Yes | String | Kode akun (harus ada di COA) |
| `position` | Yes | Enum | `DEBIT`, `CREDIT` |
| `formula` | Yes | String | Formula perhitungan (default: `amount`) |
| `description` | No | String | Keterangan baris |

## Formula Variables

Formula mendukung variabel dan operasi matematika dasar.

### Standard Variables

| Variable | Description |
|----------|-------------|
| `amount` | Nilai transaksi utama |
| `fee` | Biaya/fee (untuk template escrow) |

### Payroll Variables

Untuk template payroll (`Post Gaji Bulanan`), variabel berikut tersedia dari sistem:

| Variable | Description |
|----------|-------------|
| `grossSalary` | Total gaji bruto seluruh karyawan |
| `companyBpjs` | Total kontribusi BPJS perusahaan |
| `netPay` | Total gaji bersih (take-home pay) |
| `totalBpjs` | Total BPJS (perusahaan + karyawan) |
| `pph21` | Total PPh 21 yang dipotong |

### Formula Examples

```
amount                    # Nilai langsung
amount * 0.11             # 11% dari amount (PPN)
amount / 1.11             # DPP dari nilai termasuk PPN
amount - fee              # Amount dikurangi fee
companyBpjs * 0.8         # 80% dari BPJS perusahaan (Kesehatan)
companyBpjs * 0.2         # 20% dari BPJS perusahaan (Ketenagakerjaan)
```

## Creating New Package

1. Buat folder baru di `templates/[company-name]/`

2. Buat `coa.json` dengan struktur akun:
   - Mulai dari akun level 1 (header utama: ASET, LIABILITAS, EKUITAS, PENDAPATAN, BEBAN)
   - Gunakan kode akun yang konsisten (e.g., `1.1.01` untuk Kas)
   - Pastikan `parentCode` mereferensi akun yang sudah didefinisikan sebelumnya

3. Buat `journal-templates.json` dengan template:
   - Pastikan semua `accountCode` di lines mereferensi akun yang ada di `coa.json`
   - Setiap template harus memiliki minimal 1 baris DEBIT dan 1 baris CREDIT
   - Gunakan tags untuk memudahkan pencarian

4. Validasi JSON:
   ```bash
   python3 -m json.tool templates/[company-name]/coa.json > /dev/null
   python3 -m json.tool templates/[company-name]/journal-templates.json > /dev/null
   ```

5. Update tabel "Available Packages" di README ini

## Import via UI

1. Buka menu **Pengaturan > Import Data**
2. Pilih tab **Chart of Accounts** atau **Journal Templates**
3. Upload file JSON
4. Preview data yang akan diimport
5. Pilih opsi "Hapus data existing" jika ingin replace semua data
6. Klik **Import**

## Version Conventions

- Major version (1.0 → 2.0): Perubahan struktur atau breaking changes
- Minor version (2.0 → 2.1): Penambahan akun/template baru tanpa mengubah existing

## Account Code Conventions (Artivisi)

| Prefix | Type | Example |
|--------|------|---------|
| `1.x` | ASET | 1.1.01 Kas |
| `2.x` | LIABILITAS | 2.1.01 Hutang Usaha |
| `3.x` | EKUITAS | 3.1.01 Modal Disetor |
| `4.x` | PENDAPATAN | 4.1.01 Pendapatan Jasa |
| `5.x` | BEBAN | 5.1.01 Beban Gaji |

## Changelog

### artivisi v2.0 (2025-11-28)

**COA:**
- Added `2.1.13` Hutang BPJS - untuk payroll processing

**Journal Templates:**
- Added `Post Gaji Bulanan` - payroll posting dengan formula variables
- Added `Bayar Hutang Gaji` - pembayaran hutang gaji ke karyawan
- Added `Bayar Hutang BPJS` - penyetoran BPJS

### artivisi v1.0 (Initial)

- Initial release dengan COA untuk perusahaan jasa IT
- 47 templates termasuk tax-related templates (PPN, PPh 21, PPh 23, PPh 4(2))
