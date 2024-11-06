package com.mmieczkowski.serverguard.service;

import java.io.IOException;

public interface EmailTemplateProvider {
    String getTemplate(String templateName) throws IOException;
}
