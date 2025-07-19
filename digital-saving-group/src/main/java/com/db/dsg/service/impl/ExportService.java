package com.db.dsg.service.impl;

import com.db.dsg.model.Loan;
import com.db.dsg.model.LoanRepayment;
import com.db.dsg.model.SavingDeposit;
import com.db.dsg.repository.LoanApplicationRepository;
import com.db.dsg.repository.LoanRepaymentRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.SavingDepositRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final SavingDepositRepository savingRepo;
    private final LoanRepaymentRepository repaymentRepo;
    private final MemberRepository memberRepo;
    private final LoanApplicationRepository loanRepo;

    // ============================
    // ✅ SAVINGS EXPORTS
    // ============================

    public byte[] exportSavingsAsCSV(LocalDate from, LocalDate to, Long groupId) {
        List<SavingDeposit> deposits = (groupId != null)
                ? savingRepo.findByDateBetweenAndMember_Group_Id(from, to, groupId)
                : savingRepo.findByDateBetween(from, to);

        StringBuilder csv = new StringBuilder("Member,Group,Amount,Date,Remarks\n");
        for (SavingDeposit s : deposits) {
            csv.append(s.getMember().getName()).append(",")
                    .append(s.getMember().getGroup().getName()).append(",")
                    .append(s.getAmount()).append(",")
                    .append(s.getDate()).append(",")
                    .append(s.getRemarks() != null ? s.getRemarks() : "").append("\n");
        }

        return csv.toString().getBytes();
    }

    public byte[] exportSavingsAsPDF(LocalDate from, LocalDate to, Long groupId) throws Exception {
        List<SavingDeposit> deposits = (groupId != null)
                ? savingRepo.findByDateBetweenAndMember_Group_Id(from, to, groupId)
                : savingRepo.findByDateBetween(from, to);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Savings Report").setBold().setFontSize(14));
        doc.add(new Paragraph("From " + from + " to " + to));
        doc.add(new Paragraph("Total Entries: " + deposits.size()).setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 2, 4}))
                .useAllAvailableWidth();

        table.addHeaderCell("Member");
        table.addHeaderCell("Group");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Date");
        table.addHeaderCell("Remarks");

        for (SavingDeposit s : deposits) {
            table.addCell(s.getMember().getName());
            table.addCell(s.getMember().getGroup().getName());
            table.addCell(s.getAmount().toString());
            table.addCell(s.getDate().toString());
            table.addCell(s.getRemarks() != null ? s.getRemarks() : "");
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ============================
    // ✅ REPAYMENTS EXPORTS
    // ============================

    public byte[] exportRepaymentsAsCSV(LocalDate from, LocalDate to, Long groupId) {
        List<LoanRepayment> repayments = (groupId != null)
                ? repaymentRepo.findByDateBetweenAndLoanGroupId(from, to, groupId)
                : repaymentRepo.findByDateBetween(from, to);

        StringBuilder csv = new StringBuilder("Member,Group,Amount,Date,Remarks\n");
        for (LoanRepayment r : repayments) {
            csv.append(r.getLoan().getMember().getName()).append(",")
                    .append(r.getLoan().getMember().getGroup().getName()).append(",")
                    .append(r.getAmount()).append(",")
                    .append(r.getDate()).append(",")
                    .append(r.getRemarks() != null ? r.getRemarks() : "").append("\n");
        }

        return csv.toString().getBytes();
    }

    public byte[] exportRepaymentsAsPDF(LocalDate from, LocalDate to, Long groupId) throws Exception {
        List<LoanRepayment> repayments = (groupId != null)
                ? repaymentRepo.findByDateBetweenAndLoanGroupId(from, to, groupId)
                : repaymentRepo.findByDateBetween(from, to);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Loan Repayments Report").setBold().setFontSize(14));
        document.add(new Paragraph("Date range: " + from + " to " + to));
        document.add(new Paragraph("Total records: " + repayments.size()).setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 2, 4}))
                .useAllAvailableWidth();

        table.addHeaderCell("Member");
        table.addHeaderCell("Group");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Date");
        table.addHeaderCell("Remarks");

        for (LoanRepayment r : repayments) {
            table.addCell(r.getLoan().getMember().getName());
            table.addCell(r.getLoan().getMember().getGroup().getName());
            table.addCell(r.getAmount().toString());
            table.addCell(r.getDate().toString());
            table.addCell(r.getRemarks() != null ? r.getRemarks() : "");
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    // ----------------- Group-scoped ---------------------

    // Default: for all groups
    public byte[] exportAllSavingsAsCSV(LocalDate from, LocalDate to) {
        return exportAllSavingsAsCSV(null, from, to);
    }

    public byte[] exportAllSavingsAsPDF(LocalDate from, LocalDate to) throws Exception {
        return exportAllSavingsAsPDF(null, from, to);
    }

    // ✅ Export all repayments as CSV (filtered by date + optional groupId)
    public byte[] exportAllRepaymentsAsCSV(LocalDate from, LocalDate to, Long groupId) {
        List<LoanRepayment> repayments = (groupId != null)
                ? repaymentRepo.findByDateBetweenAndLoanGroupId(from, to, groupId)
                : repaymentRepo.findByDateBetween(from, to);

        StringBuilder csv = new StringBuilder("Member,Group,Amount,Date,Remarks\n");
        for (LoanRepayment r : repayments) {
            csv.append(r.getLoan().getMember().getName()).append(",")
                    .append(r.getLoan().getMember().getGroup().getName()).append(",")
                    .append(r.getAmount()).append(",")
                    .append(r.getDate()).append(",")
                    .append(r.getRemarks() != null ? r.getRemarks() : "").append("\n");
        }

        return csv.toString().getBytes();
    }

    // ✅ Export all repayments as PDF (filtered by date + optional groupId)
    public byte[] exportAllRepaymentsAsPDF(LocalDate from, LocalDate to, Long groupId) throws Exception {
        List<LoanRepayment> repayments = (groupId != null)
                ? repaymentRepo.findByDateBetweenAndLoanGroupId(from, to, groupId)
                : repaymentRepo.findByDateBetween(from, to);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Loan Repayments Report").setBold().setFontSize(14));
        document.add(new Paragraph("Date range: " + from + " to " + to));
        document.add(new Paragraph("Total records: " + repayments.size()).setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 2, 4}))
                .useAllAvailableWidth();

        table.addHeaderCell("Member");
        table.addHeaderCell("Group");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Date");
        table.addHeaderCell("Remarks");

        for (LoanRepayment r : repayments) {
            table.addCell(r.getLoan().getMember().getName());
            table.addCell(r.getLoan().getMember().getGroup().getName());
            table.addCell(r.getAmount().toString());
            table.addCell(r.getDate().toString());
            table.addCell(r.getRemarks() != null ? r.getRemarks() : "");
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    // Overloads: with group filter
    public byte[] exportAllSavingsAsCSV(Long groupId, LocalDate from, LocalDate to) {
        List<SavingDeposit> deposits = (groupId != null)
                ? savingRepo.findByDateBetweenAndGroupId(from, to, groupId)
                : savingRepo.findByDateBetween(from, to);

        // Generate CSV
        StringBuilder csv = new StringBuilder("Member,Group,Amount,Date,Remarks\n");
        for (SavingDeposit s : deposits) {
            csv.append(s.getMember().getName()).append(",")
                    .append(s.getMember().getGroup().getName()).append(",")
                    .append(s.getAmount()).append(",")
                    .append(s.getDate()).append(",")
                    .append(s.getRemarks() != null ? s.getRemarks() : "").append("\n");
        }
        return csv.toString().getBytes();
    }

    public byte[] exportAllSavingsAsPDF(Long groupId, LocalDate from, LocalDate to) throws Exception {
        List<SavingDeposit> deposits = (groupId != null)
                ? savingRepo.findByDateBetweenAndGroupId(from, to, groupId)
                : savingRepo.findByDateBetween(from, to);

        return generateSavingsPdf("Group Savings Report", deposits);
    }

    // ----------------- Shared PDF methods ---------------------

    public byte[] generateLoanPdf(String title, List<Loan> loans) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(title).setBold().setFontSize(14));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2, 2}));
        addHeader(table, "Member", "Amount", "Remaining", "Status", "Disbursed", "Repaid");

        for (Loan l : loans) {
            table.addCell(new Cell().add(new Paragraph(l.getMember().getName())));
            table.addCell(new Cell().add(new Paragraph(l.getAmount().toString())));
            table.addCell(new Cell().add(new Paragraph(l.getRemainingBalance().toString())));
            table.addCell(new Cell().add(new Paragraph(l.getStatus().name())));
            table.addCell(new Cell().add(new Paragraph(l.getDisbursementDate() != null ? l.getDisbursementDate().toString() : "-")));
            table.addCell(new Cell().add(new Paragraph(l.getRepaymentDate() != null ? l.getRepaymentDate().toString() : "-")));
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    public byte[] generateSavingsPdf(String title, List<SavingDeposit> deposits) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        // Title
        doc.add(new Paragraph(title).setBold().setFontSize(14).setMarginBottom(10));

        // Table with 4 columns: Member, Amount, Date, Description
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 4})).useAllAvailableWidth();

        // Header
        addHeader(table, "Member", "Amount", "Date", "Description");

        // Rows
        for (SavingDeposit s : deposits) {
            table.addCell(new Cell().add(new Paragraph(s.getMember().getName())));
            table.addCell(new Cell().add(new Paragraph(s.getAmount().toString())));
            table.addCell(new Cell().add(new Paragraph(s.getDate().toString())));

        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private void addHeader(Table table, String... headers) {
        for (String h : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(h).setBold())
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(headerCell);
        }
    }
}
