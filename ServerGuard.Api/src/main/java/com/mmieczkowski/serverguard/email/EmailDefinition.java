package com.mmieczkowski.serverguard.email;

public abstract class EmailDefinition {
    public abstract String getTemplateFileName();
    public abstract String getSubject();
    public abstract EmailPlaceHolder[] getPlaceHolders ();
    public abstract String[] getRecipients();
}
