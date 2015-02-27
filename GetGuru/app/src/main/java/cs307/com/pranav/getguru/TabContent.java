package cs307.com.pranav.getguru;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */
import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class TabContent implements TabContentFactory{
    private Context mContext;

    public TabContent(Context context){
        mContext = context;
    }

    @Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
}
