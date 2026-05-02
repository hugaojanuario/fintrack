package br.com.fintrack.domain.report.service;

import br.com.fintrack.domain.debt.repository.DebtRepository;
import br.com.fintrack.domain.expense.repository.ExpenseRepository;
import br.com.fintrack.domain.goal.entity.enums.GoalStatus;
import br.com.fintrack.domain.goal.repository.GoalRepository;
import br.com.fintrack.domain.user.repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportGeneratorService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtRepository debtRepository;
    private final GoalRepository goalRepository;
    private final S3Service s3Service;
    private final EmailService emailService;

    @Async
    public void generateAndSend(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException());

        var expenses = expenseRepository.findByUserAndActiveTrue(user);
        var debts = debtRepository.findByUserAndActiveTrueAndStatus(user, br.com.fintrack.domain.debt.entity.enums.DebtStatus.ACTIVE);
        var goals = goalRepository.findByUserAndActiveTrueAndStatus(user, GoalStatus.ACTIVE);

        var baos = new ByteArrayOutputStream();
        var writer = new PdfWriter(baos);
        var pdf = new PdfDocument(writer);
        var document = new Document(pdf);

        document.add(new Paragraph("Relatório Financeiro — FinTrack")
                .setBold().setFontSize(18));
        document.add(new Paragraph("Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFontSize(10));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Dados do Usuário").setBold().setFontSize(14));
        document.add(new Paragraph("Email: " + user.getEmail()));
        document.add(new Paragraph("Renda Mensal: R$ " + user.getMonthlyIncome().toPlainString()));
        document.add(new Paragraph("Perfil de Investidor: " + user.getInvestorProfile()));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Despesas Ativas").setBold().setFontSize(14));
        var expenseTable = new Table(3).useAllAvailableWidth();
        expenseTable.addCell(new Cell().add(new Paragraph("Descrição").setBold()));
        expenseTable.addCell(new Cell().add(new Paragraph("Categoria").setBold()));
        expenseTable.addCell(new Cell().add(new Paragraph("Valor").setBold()));
        for (var expense : expenses) {
            expenseTable.addCell(expense.getDescription());
            expenseTable.addCell(expense.getCategory().name());
            expenseTable.addCell("R$ " + expense.getAmount().toPlainString());
        }
        document.add(expenseTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Dívidas Ativas").setBold().setFontSize(14));
        var debtTable = new Table(3).useAllAvailableWidth();
        debtTable.addCell(new Cell().add(new Paragraph("Credor").setBold()));
        debtTable.addCell(new Cell().add(new Paragraph("Saldo Restante").setBold()));
        debtTable.addCell(new Cell().add(new Paragraph("Taxa de Juros").setBold()));
        for (var debt : debts) {
            debtTable.addCell(debt.getCreditor());
            debtTable.addCell("R$ " + debt.getRemainingAmount().toPlainString());
            debtTable.addCell(debt.getInterestRate().toPlainString() + "%");
        }
        document.add(debtTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Metas Ativas").setBold().setFontSize(14));
        var goalTable = new Table(3).useAllAvailableWidth();
        goalTable.addCell(new Cell().add(new Paragraph("Título").setBold()));
        goalTable.addCell(new Cell().add(new Paragraph("Meta").setBold()));
        goalTable.addCell(new Cell().add(new Paragraph("Acumulado").setBold()));
        for (var goal : goals) {
            goalTable.addCell(goal.getTitle());
            goalTable.addCell("R$ " + goal.getTargetAmount().toPlainString());
            goalTable.addCell("R$ " + goal.getCurrentAmount().toPlainString());
        }
        document.add(goalTable);

        document.close();

        var key = "reports/%s/%s-report.pdf".formatted(user.getId(), System.currentTimeMillis());
        var reportUrl = s3Service.upload(key, baos.toByteArray());

        emailService.sendReportReady(userEmail, reportUrl);
    }
}
