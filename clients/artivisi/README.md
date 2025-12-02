# PT Artivisi Intermedia

Client-specific configuration for PT Artivisi Intermedia - IT services company.

## Business Profile

| Attribute | Value |
|-----------|-------|
| Business Type | IT Services (B2B) |
| Services | Training, Consulting, Development, Remittance |
| Employees | <10 |
| Active Clients | ≤20 |
| Location | Jakarta, Indonesia |

## Files

| File | Description |
|------|-------------|
| `capacity-planning.md` | Infrastructure sizing and cost estimates |
| `templates/coa.json` | Chart of Accounts (v2.1) |
| `templates/journal-templates.json` | Journal entry templates (v2.1) |

## Templates Version History

### v2.1 (2024-11)
- Added separate accounts for Deposito, Logam Mulia, Dinar/Dirham
- Added gain/loss accounts for asset sales
- Added PPh 23 credit account
- Added 14 new journal templates (investments, assets, bonus)

### v2.0 (2024-10)
- Added payroll-related accounts and templates
- Tax accounts (PPh 21, 23, 4(2), PPN)

### v1.0 (2024-09)
- Initial COA for IT services
- Basic income/expense templates

## Deployment

Domain: `akunting.artivisi.id`

See:
- `capacity-planning.md` for VPS sizing
- `docs/deployment-guide.md` for deployment steps

## Import Seed Data

The seed data (COA + Journal Templates) is available in the new ZIP export format.

### Generate ZIP file

```bash
cd clients/artivisi
python3 convert-to-export-format.py
```

This creates `artivisi-seed-data.zip` containing:
- 83 chart of accounts
- 58 journal templates

### Import via UI

1. Login as admin
2. Go to Settings > Import Data
3. Upload `artivisi-seed-data.zip`

### Import via curl

```bash
curl -X POST http://localhost:10000/settings/import \
  -u admin:admin \
  -F "file=@clients/artivisi/artivisi-seed-data.zip"
```

### Source Files

The original JSON files are preserved for reference:
- `templates/coa.json` - Chart of Accounts definition
- `templates/journal-templates.json` - Journal template definitions
