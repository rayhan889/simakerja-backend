package com.rynrama.simakerjabackend.util.renderer;

import com.rynrama.simakerjabackend.model.MoAIAPDFViewModel;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class MoAIAPDFRenderer implements PDFDocumentRenderer<MoAIAPDFViewModel> {

    @Override
    public String templateName() {
        return "moa_ia_template";
    }

    @Override
    public Class<MoAIAPDFViewModel> modelType() {
        return MoAIAPDFViewModel.class;
    }

    @Override
    public Context buildContext(MoAIAPDFViewModel model) {
        Context context = new Context();
        context.setVariable("facultyName", model.getFacultyName());
        context.setVariable("facultyRepresentativeName", model.getFacultyRepresentativeName());
        context.setVariable("facultyAddress", model.getFacultyAddress());
        context.setVariable("partnerName", model.getPartnerName());
        context.setVariable("partnerLogoUrl", model.getPartnerLogoUrl());
        context.setVariable("partnerNumber", model.getPartnerNumber());
        context.setVariable("partnerRepresentativeName", model.getPartnerRepresentativeName());
        context.setVariable("partnerRepresentativePosition", model.getPartnerRepresentativePosition());
        context.setVariable("partnerAddress", model.getPartnerAddress());
        context.setVariable("activityType", model.getActivityType());
        context.setVariable("day", model.getDay());
        context.setVariable("date", model.getDate());
        context.setVariable("month", model.getMonth());
        context.setVariable("yearInLongText", model.getYearInLongText());
        context.setVariable("ddMMYYYYFormatDate", model.getDdMMyyyyFormatDate());
        context.setVariable("studentSnapshots", model.getStudentSnapshots());
        context.setVariable("partnerCooperationperiod", model.getPartnerCooperationperiod());
        context.setVariable("partnerCooperationPeriodIntext", model.getPartnerCooperationPeriodIntext());
        return context;
    }
}
