package sk.svelta.icaltimereports.validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validate url
 * @author Jaroslav Švelta
 */
@FacesValidator
public class UrlValidator implements Validator {

    private static final Logger LOG = Logger.getLogger(UrlValidator.class.getName());

    public UrlValidator() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
            URI uri = new URI(value.toString());
            URL url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            LOG.log(Level.INFO, "Exception", e);
            throw new ValidatorException(new FacesMessage("Invalid URL"), e);
        }
    }

}
