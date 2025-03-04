package com.mmieczkowski.serverguard.email;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class ResourcesEmailTemplateProvider implements EmailTemplateProvider {
    @Override
    public String getTemplate(String templateName) throws IOException {
        return new ClassPathResource("templates/" + templateName + ".html").getContentAsString(Charset.defaultCharset());
    }
}
