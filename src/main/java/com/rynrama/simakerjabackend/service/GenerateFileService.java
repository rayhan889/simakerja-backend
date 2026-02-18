package com.rynrama.simakerjabackend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rynrama.simakerjabackend.model.MoAIAPDFViewModel;
import com.rynrama.simakerjabackend.model.PDFViewModel;
import com.rynrama.simakerjabackend.util.renderer.PDFDocumentRenderer;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GenerateFileService {

    private final TemplateEngine templateEngine;
    private final List<PDFDocumentRenderer<?>> renderers;

    public GenerateFileService(TemplateEngine templateEngine,  List<PDFDocumentRenderer<?>> renderers) {
        this.templateEngine = templateEngine;
        this.renderers = renderers;
    }

    public <T extends PDFViewModel> byte[] generatePdf(T model) throws IOException {
        if (model instanceof MoAIAPDFViewModel) {
            System.out.println("Partner name: " + ((MoAIAPDFViewModel) model).getPartnerName());
        }

        PDFDocumentRenderer<T> renderer = findRenderer(model);
        Context context = renderer.buildContext(model);

        String html = templateEngine.process(renderer.templateName(), context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private <T extends PDFViewModel> PDFDocumentRenderer<T> findRenderer(T model) {

        return (PDFDocumentRenderer<T>) renderers.stream()
                .filter(r -> r.modelType().equals(model.getClass()))
                .findFirst()
                .orElseThrow();
    }
}
