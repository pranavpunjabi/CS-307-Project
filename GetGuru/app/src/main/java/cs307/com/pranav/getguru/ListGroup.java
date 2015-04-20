package cs307.com.pranav.getguru;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DiGiT_WiZARD on 4/10/15.
 */
public class ListGroup {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public ListGroup(String string) {
        this.string = string;
    }
}
