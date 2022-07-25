package sparta.realm.cschat;

import sparta.realm.utils.Mail.MailBuilder;

public class OvhMailBuilder  extends MailBuilder {
    public OvhMailBuilder()
    {
        md.hostAddress="ssl0.ovh.net";
        md.port="587";

    }
}
