package cz.incad.kramerius.rights.server;

import cz.incad.kramerius.rights.server.utils.GeneratePasswordUtils;
import cz.incad.kramerius.rights.server.utils.I18NUtils;
import cz.incad.kramerius.security.utils.PasswordDigest;
import org.aplikator.client.shared.data.Operation;
import org.aplikator.client.shared.data.Record;
import org.aplikator.client.shared.data.RecordContainer;
import org.aplikator.client.shared.rpc.AplikatorService;
import org.aplikator.server.Context;
import org.aplikator.server.descriptor.View;
import org.aplikator.server.function.Executable;
import org.aplikator.client.shared.data.FunctionParameters;
import org.aplikator.client.shared.data.FunctionResult;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratePasswordExec extends Executable {
    private static final Logger LOGGER = Logger.getLogger(GeneratePasswordExec.class.getName());


    public static final String EMAIL_REGEXP = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

    protected View userArr;
    protected Mailer mailer;


    @Override
    public FunctionResult execute(FunctionParameters parameters, Context context) {
        String result = null;
        boolean success = false;
        try {
            String emailAddres = Structure.user.EMAIL.getValue(parameters.getClientContext().getCurrentRecord());
            if ((emailAddres != null) && (validation(emailAddres))) {
                String generated = GeneratePasswordUtils.generatePswd();
                Record currentRecord = parameters.getClientContext().getCurrentRecord();
                Record edited = new Record(currentRecord.getPrimaryKey());
                Structure.user.PASSWORD.setValue(edited, PasswordDigest.messageDigest(generated));
                RecordContainer container = new RecordContainer();
                container.addRecord(getView().getViewDTO(context), currentRecord, edited, Operation.UPDATE);
                AplikatorService service = context.getAplikatorService();
                service.processRecords(container);
                GeneratePasswordUtils.sendGeneratedPasswordToMail(emailAddres, Structure.user.LOGINNAME.getValue(currentRecord), generated, mailer, context);
                String okResultString = I18NUtils.getLocalizedString("VygenerovatHeslo.ok.result", context);
                result = MessageFormat.format(okResultString, emailAddres);
                success= true;
            } else {
                String okResultString = I18NUtils.getLocalizedString("VygenerovatHeslo.notvalidmail", context);
                result = MessageFormat.format(okResultString, emailAddres);
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            String failResult = I18NUtils.getLocalizedString("VygenerovatHeslo.fail.result", context);
            return new FunctionResult(MessageFormat.format(failResult, ex.getMessage()), success);
        }
        return new FunctionResult(result, success);
    }


    public Mailer getMailer() {
        return mailer;
    }

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public static boolean validation(String email) {
        boolean isValid = false;
        Pattern pattern = Pattern.compile(EMAIL_REGEXP, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public View getView() {
        return userArr;
    }

    public void setArrangement(View userArr) {
        this.userArr = userArr;
    }

}
