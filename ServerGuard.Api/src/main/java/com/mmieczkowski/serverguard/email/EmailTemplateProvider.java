package com.mmieczkowski.serverguard.email;

import java.io.IOException;

public interface EmailTemplateProvider {
    String getTemplate(String templateName) throws IOException;
}
