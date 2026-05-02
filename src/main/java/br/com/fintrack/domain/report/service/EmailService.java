package br.com.fintrack.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesV2Client sesV2Client;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public void sendReportReady(String to, String reportUrl) {
        var html = """
                <html><body>
                <h2>Seu relatório financeiro está pronto!</h2>
                <p>Acesse o link abaixo para baixar seu relatório:</p>
                <a href="%s">Baixar relatório</a>
                <br/><br/>
                <small>FinTrack — sua plataforma de gestão financeira</small>
                </body></html>
                """.formatted(reportUrl);

        send(to, "FinTrack — Relatório Financeiro Pronto", html);
    }

    public void sendGoalAchieved(String to, String goalTitle, BigDecimal amount) {
        var html = """
                <html><body>
                <h2>🎯 Meta atingida!</h2>
                <p>Parabéns! Você atingiu sua meta <strong>%s</strong> no valor de <strong>R$ %s</strong>.</p>
                <p>Continue assim, você está no caminho certo!</p>
                <br/>
                <small>FinTrack — sua plataforma de gestão financeira</small>
                </body></html>
                """.formatted(goalTitle, amount.toPlainString());

        send(to, "FinTrack — Meta Atingida: " + goalTitle, html);
    }

    private void send(String to, String subject, String htmlBody) {
        var request = SendEmailRequest.builder()
                .fromEmailAddress(fromEmail)
                .destination(Destination.builder().toAddresses(to).build())
                .content(EmailContent.builder()
                        .simple(Message.builder()
                                .subject(Content.builder().data(subject).charset("UTF-8").build())
                                .body(Body.builder()
                                        .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                                        .build())
                                .build())
                        .build())
                .build();

        sesV2Client.sendEmail(request);
    }
}
