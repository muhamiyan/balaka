#!/usr/bin/env python3
"""
Convert Artivisi JSON templates to the new ZIP export format.

Usage:
    python convert-to-export-format.py

Output:
    artivisi-seed-data.zip - Import this via /settings/import
"""

import json
import zipfile
import io
from datetime import datetime

def escape_csv(value):
    """Escape CSV field value."""
    if value is None:
        return ""
    value = str(value)
    if "," in value or '"' in value or "\n" in value:
        return '"' + value.replace('"', '""') + '"'
    return value

def convert_coa_to_csv(coa_data):
    """Convert COA JSON to CSV format."""
    lines = ["account_code,account_name,account_type,parent_code,normal_balance,active,created_at"]

    for account in coa_data["accounts"]:
        code = escape_csv(account.get("code", ""))
        name = escape_csv(account.get("name", ""))
        acc_type = account.get("type", "")
        parent_code = escape_csv(account.get("parentCode", ""))
        normal_balance = account.get("normalBalance", "DEBIT")
        active = "true"
        created_at = ""

        lines.append(f"{code},{name},{acc_type},{parent_code},{normal_balance},{active},{created_at}")

    return "\n".join(lines) + "\n"

def convert_templates_to_csv(template_data):
    """Convert journal templates JSON to CSV format."""
    # Main templates CSV
    template_lines = ["template_name,category,cash_flow_category,template_type,description,is_favorite,is_system,active,version,usage_count,last_used_at"]

    # Template lines CSV
    line_csv = ["template_name,line_order,account_code,account_hint,position,formula,description"]

    # Template tags CSV
    tag_csv = ["template_name,tag"]

    for template in template_data["templates"]:
        name = escape_csv(template.get("name", ""))
        category = template.get("category", "EXPENSE")
        cash_flow_category = template.get("cashFlowCategory", "OPERATING")
        template_type = "SIMPLE"  # Default type (SIMPLE or DETAILED)
        description = escape_csv(template.get("description", ""))
        is_favorite = "false"
        is_system = "false"
        active = "true"
        version = "1"
        usage_count = "0"
        last_used_at = ""

        template_lines.append(f"{name},{category},{cash_flow_category},{template_type},{description},{is_favorite},{is_system},{active},{version},{usage_count},{last_used_at}")

        # Process template lines
        for idx, line in enumerate(template.get("lines", []), start=1):
            account_code = escape_csv(line.get("accountCode", ""))
            account_hint = escape_csv(line.get("accountHint", ""))
            position = line.get("position", "DEBIT")
            formula = escape_csv(line.get("formula", ""))
            line_desc = escape_csv(line.get("description", ""))

            line_csv.append(f"{name},{idx},{account_code},{account_hint},{position},{formula},{line_desc}")

        # Process tags
        for tag in template.get("tags", []):
            tag_csv.append(f"{name},{escape_csv(tag)}")

    return (
        "\n".join(template_lines) + "\n",
        "\n".join(line_csv) + "\n",
        "\n".join(tag_csv) + "\n"
    )

def create_empty_csv(header):
    """Create an empty CSV with just the header."""
    return header + "\n"

def create_manifest(coa_count, template_count):
    """Create MANIFEST.md content."""
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    return f"""# Data Export Manifest

Export Date: {now}
Application: Aplikasi Akunting
Format Version: 2.0

## Company Information
Name: PT Artivisi Intermedia
NPWP: -

## Export Contents
- Chart of Accounts: {coa_count} records
- Journal Templates: {template_count} records
- Journal Entries: 0 records
- Transactions: 0 records
- Clients: 0 records
- Projects: 0 records
- Invoices: 0 records
- Employees: 0 records
- Payroll Runs: 0 records
- Users: 0 records
- Documents: 0 files
- Audit Logs: 0 records

## Notes
This is a seed data export containing only:
- Chart of Accounts (Artivisi IT Services COA v2.1)
- Journal Templates (Artivisi templates v2.1)

All other tables are empty placeholders.
"""

def main():
    # Load JSON files
    with open("templates/coa.json", "r", encoding="utf-8") as f:
        coa_data = json.load(f)

    with open("templates/journal-templates.json", "r", encoding="utf-8") as f:
        template_data = json.load(f)

    # Convert COA
    coa_csv = convert_coa_to_csv(coa_data)
    coa_count = len(coa_data["accounts"])

    # Convert templates
    templates_csv, template_lines_csv, template_tags_csv = convert_templates_to_csv(template_data)
    template_count = len(template_data["templates"])

    # Create ZIP file
    zip_buffer = io.BytesIO()
    with zipfile.ZipFile(zip_buffer, "w", zipfile.ZIP_DEFLATED) as zf:
        # Manifest
        zf.writestr("MANIFEST.md", create_manifest(coa_count, template_count))

        # 01: Company config (empty - will use defaults)
        zf.writestr("01_company_config.csv", create_empty_csv(
            "company_name,company_address,company_phone,company_email,tax_id,npwp,nitku,fiscal_year_start_month,currency_code,signing_officer_name,signing_officer_title"
        ))

        # 02: Chart of Accounts
        zf.writestr("02_chart_of_accounts.csv", coa_csv)

        # 03: Salary components (empty)
        zf.writestr("03_salary_components.csv", create_empty_csv(
            "code,name,description,component_type,is_percentage,default_rate,default_amount,is_system,display_order,active,is_taxable,bpjs_category"
        ))

        # 04-06: Journal templates
        zf.writestr("04_journal_templates.csv", templates_csv)
        zf.writestr("05_journal_template_lines.csv", template_lines_csv)
        zf.writestr("06_journal_template_tags.csv", template_tags_csv)

        # 07-10: Reference data (empty)
        zf.writestr("07_clients.csv", create_empty_csv(
            "code,name,contact_person,email,phone,address,npwp,nik,nitku,active,created_at"
        ))
        zf.writestr("08_projects.csv", create_empty_csv(
            "code,name,client_code,status,start_date,end_date,budget_amount,contract_value,description,created_at"
        ))
        zf.writestr("09_project_milestones.csv", create_empty_csv(
            "project_code,sequence,name,description,status,weight_percent,target_date,actual_date"
        ))
        zf.writestr("10_project_payment_terms.csv", create_empty_csv(
            "project_code,sequence,milestone_sequence,template_name,name,is_percentage,percentage,amount,due_trigger,auto_post"
        ))

        # 11-14: System configuration (empty)
        zf.writestr("11_fiscal_periods.csv", create_empty_csv(
            "year,month,status,month_closed_at,month_closed_by,tax_filed_at,tax_filed_by"
        ))
        zf.writestr("12_tax_deadlines.csv", create_empty_csv(
            "deadline_type,name,description,due_day,use_last_day_of_month,reminder_days_before,active"
        ))
        zf.writestr("13_company_bank_accounts.csv", create_empty_csv(
            "bank_name,account_number,account_name,bank_branch,is_default,active"
        ))
        zf.writestr("14_merchant_mappings.csv", create_empty_csv(
            "merchant_pattern,match_type,template_name,default_description,match_count,last_used_at"
        ))

        # 15-16: Employee data (empty)
        zf.writestr("15_employees.csv", create_empty_csv(
            "employee_id,name,email,nik_ktp,npwp,ptkp_status,job_title,department,employment_type,hire_date,resign_date,bank_name,bank_account,bpjs_kesehatan_number,bpjs_ketenagakerjaan_number,employment_status,username"
        ))
        zf.writestr("16_employee_salary_components.csv", create_empty_csv(
            "employee_id,component_code,rate,amount,effective_date,end_date"
        ))

        # 17-20: Transactional data (empty)
        zf.writestr("17_invoices.csv", create_empty_csv(
            "invoice_number,invoice_date,due_date,client_code,project_code,status,amount,notes,created_at"
        ))
        zf.writestr("18_transactions.csv", create_empty_csv(
            "transaction_number,transaction_date,template_name,project_code,amount,description,reference_number,notes,status,void_reason,void_notes,voided_at,voided_by,posted_at,posted_by,created_at"
        ))
        zf.writestr("19_transaction_account_mappings.csv", create_empty_csv(
            "transaction_number,template_name,line_order,account_code,amount"
        ))
        zf.writestr("20_journal_entries.csv", create_empty_csv(
            "journal_number,journal_date,transaction_number,description,status,account_code,debit_amount,credit_amount,posted_at,voided_at,void_reason"
        ))

        # 21-24: Payroll and amortization (empty)
        zf.writestr("21_payroll_runs.csv", create_empty_csv(
            "payroll_period,period_start,period_end,status,total_gross,total_deductions,total_net_pay,total_company_bpjs,total_pph21,employee_count,notes,posted_at,cancelled_at,cancel_reason,created_at"
        ))
        zf.writestr("22_payroll_details.csv", create_empty_csv(
            "payroll_period,employee_id,gross_salary,total_deductions,net_pay,bpjs_kes_employee,bpjs_kes_company,bpjs_jht_employee,bpjs_jht_company,bpjs_jp_employee,bpjs_jp_company,bpjs_jkk,bpjs_jkm,pph21"
        ))
        zf.writestr("23_amortization_schedules.csv", create_empty_csv(
            "code,name,schedule_type,source_account_code,target_account_code,total_amount,total_periods,period_amount,start_date,status,auto_post,completed_periods,amortized_amount"
        ))
        zf.writestr("24_amortization_entries.csv", create_empty_csv(
            "schedule_code,period_number,period_start,period_end,amount,status,journal_number,posted_at"
        ))

        # 25-27: Tax and draft data (empty)
        zf.writestr("25_tax_transaction_details.csv", create_empty_csv(
            "transaction_number,tax_type,counterparty_name,counterparty_npwp,counterparty_nik,counterparty_nitku,tax_object_code,dpp,tax_amount,faktur_number,faktur_date"
        ))
        zf.writestr("26_tax_deadline_completions.csv", create_empty_csv(
            "deadline_type,year,month,completed_date,completed_by,reference_number,notes"
        ))
        zf.writestr("27_draft_transactions.csv", create_empty_csv(
            "source,status,merchant_name,transaction_date,amount,suggested_template_name,merchant_confidence,date_confidence,amount_confidence,raw_ocr_text,processed_at,processed_by,rejection_reason"
        ))

        # 28-31: User data (empty - existing users will be preserved)
        zf.writestr("28_users.csv", create_empty_csv(
            "username,password,full_name,email,active,created_at"
        ))
        zf.writestr("29_user_roles.csv", create_empty_csv(
            "username,role,created_by,created_at"
        ))
        zf.writestr("30_user_template_preferences.csv", create_empty_csv(
            "username,template_name,is_favorite,use_count,last_used_at"
        ))
        zf.writestr("31_telegram_user_links.csv", create_empty_csv(
            "telegram_user_id,telegram_username,username,is_active,linked_at"
        ))

        # 32-33: System state (empty)
        zf.writestr("32_audit_logs.csv", create_empty_csv(
            "timestamp,username,action,entity_type,entity_id,ip_address"
        ))
        zf.writestr("33_transaction_sequences.csv", create_empty_csv(
            "sequence_type,prefix,year,last_number"
        ))

        # Documents index (empty)
        zf.writestr("documents/index.csv", create_empty_csv(
            "storage_path,original_filename,content_type,file_size,transaction_number,journal_number,uploaded_at"
        ))

    # Write ZIP file
    with open("artivisi-seed-data.zip", "wb") as f:
        f.write(zip_buffer.getvalue())

    print(f"Created artivisi-seed-data.zip")
    print(f"  - {coa_count} chart of accounts")
    print(f"  - {template_count} journal templates")
    print()
    print("Import via: Settings > Import Data > Upload artivisi-seed-data.zip")

if __name__ == "__main__":
    main()
