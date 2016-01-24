package net.dongliu.requests.struct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * http header
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Header extends Pair<String, String> {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String Accept_Encoding = "Accept-Encoding";
    public static final String Accept_Encoding_COMPRESS = "compress, deflate";

    public Header(String name, String value) {
        super(name, value);
    }

    public static Header of(String name, Object value) {
        if (value instanceof Date) {
            String dateStr = dateFormat().format((Date) value);
            return new Header(name, dateStr);
        } else {
            return new Header(name, value.toString());
        }
    }

    public Date getDateValue() throws ParseException {
        return dateFormat().parse(getValue());
    }

    private static SimpleDateFormat dateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    }
}
