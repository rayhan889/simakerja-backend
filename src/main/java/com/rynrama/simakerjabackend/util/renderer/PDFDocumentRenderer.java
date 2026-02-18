package com.rynrama.simakerjabackend.util.renderer;

import com.rynrama.simakerjabackend.model.PDFViewModel;
import org.thymeleaf.context.Context;

public interface PDFDocumentRenderer<T extends PDFViewModel> {
    String templateName();

    Class<T> modelType();

    Context buildContext(T model);
}
