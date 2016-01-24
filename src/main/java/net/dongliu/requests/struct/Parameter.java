package net.dongliu.requests.struct;

/**
 * http parameter
 *
 * @author Dong Liu
 */
public class Parameter extends Pair<String, String> {

    public Parameter(String name, String value) {
        super(name, value);
    }

    public static Parameter of(String name, Object value) {
        return new Parameter(name, value.toString());
    }
}
