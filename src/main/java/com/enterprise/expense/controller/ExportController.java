package com.enterprise.expense.controller;
import com.enterprise.expense.entity.ExpenseClaim;
import com.enterprise.expense.service.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.List;
@RestController @RequestMapping("/api/export") @RequiredArgsConstructor
public class ExportController {
    private final ExpenseClaimService expenseService;
    private final UserService userService;

    @GetMapping("/my-claims/excel")
    public ResponseEntity<byte[]> myClaimsExcel() throws IOException {
        return excelResponse(expenseService.getMyClaims(), "my-claims.xlsx");
    }

    @GetMapping("/all-claims/excel")
    public ResponseEntity<byte[]> allClaimsExcel() throws IOException {
        return excelResponse(expenseService.getAll(), "all-claims.xlsx");
    }

    @GetMapping("/claim/{id}/pdf")
    public ResponseEntity<byte[]> claimPdf(@PathVariable Long id) throws DocumentException {
        ExpenseClaim c = expenseService.getById(id);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, out);
        doc.open();
        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Paragraph t = new Paragraph("Enterprise Expense Reimbursement", title);
        t.setAlignment(Element.ALIGN_CENTER); t.setSpacingAfter(20); doc.add(t);
        PdfPTable table = new PdfPTable(2); table.setWidthPercentage(100);
        String[][] rows = {
            {"Claim Number", c.getClaimNumber()},
            {"Employee", c.getEmployee().getFullName()},
            {"Title", c.getTitle()},
            {"Category", c.getCategory().name()},
            {"Amount", "Rs. " + c.getAmount()},
            {"Date", c.getExpenseDate().toString()},
            {"Status", c.getStatus().name()},
            {"Vendor", c.getVendorName() != null ? c.getVendorName() : "-"},
            {"GST No", c.getGstNumber() != null ? c.getGstNumber() : "-"},
            {"Description", c.getDescription() != null ? c.getDescription() : "-"}
        };
        for (String[] row : rows) {
            PdfPCell lbl = new PdfPCell(new Phrase(row[0], bold));
            lbl.setBackgroundColor(new BaseColor(79,70,229)); lbl.setPadding(8); table.addCell(lbl);
            PdfPCell val = new PdfPCell(new Phrase(row[1], normal)); val.setPadding(8); table.addCell(val);
        }
        doc.add(table); doc.close();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claim-" + c.getClaimNumber() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF).body(out.toByteArray());
    }

    private ResponseEntity<byte[]> excelResponse(List<ExpenseClaim> claims, String filename) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Claims");
            CellStyle hs = wb.createCellStyle(); Font hf = wb.createFont(); hf.setBold(true); hs.setFont(hf);
            hs.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); hs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row header = sheet.createRow(0);
            String[] cols = {"Claim#","Employee","Department","Title","Category","Amount","Date","Status","Vendor"};
            for (int i=0;i<cols.length;i++) { Cell cell=header.createCell(i); cell.setCellValue(cols[i]); cell.setCellStyle(hs); }
            int rn=1;
            for (ExpenseClaim c : claims) {
                Row row = sheet.createRow(rn++);
                row.createCell(0).setCellValue(c.getClaimNumber());
                row.createCell(1).setCellValue(c.getEmployee().getFullName());
                row.createCell(2).setCellValue(c.getEmployee().getDepartment()!=null?c.getEmployee().getDepartment().getName():"-");
                row.createCell(3).setCellValue(c.getTitle());
                row.createCell(4).setCellValue(c.getCategory().name());
                row.createCell(5).setCellValue(c.getAmount().doubleValue());
                row.createCell(6).setCellValue(c.getExpenseDate().toString());
                row.createCell(7).setCellValue(c.getStatus().name());
                row.createCell(8).setCellValue(c.getVendorName()!=null?c.getVendorName():"-");
            }
            for (int i=0;i<cols.length;i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream(); wb.write(out);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
        }
    }
}
