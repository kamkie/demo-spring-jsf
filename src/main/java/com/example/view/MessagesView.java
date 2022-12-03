package com.example.view;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.stereotype.Component;

@Component
public class MessagesView {

    public void info() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PrimeFaces Rocks.");
        FacesContext.getCurrentInstance()
                .addMessage(null, message);
    }

    public void warn() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Watch out for PrimeFaces.");
        FacesContext.getCurrentInstance()
                .addMessage(null, message);
    }

    public void error() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Contact admin.");
        FacesContext.getCurrentInstance()
                .addMessage(null, message);
    }

    public void fatal() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "System Error");
        FacesContext.getCurrentInstance()
                .addMessage(null, message);
    }
}
