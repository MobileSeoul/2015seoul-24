package co.askseoulites.seoulcityapp.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by hassanabid on 10/25/15.
 */

@ParseClassName("Category")
public class Category extends ParseObject {


    public String getTitle() {
        return getString(ModelUtils.TITLE);
    }
    public void setTitle(String title) { put(ModelUtils.TITLE, title); }

    public String getDescription() {
        return getString(ModelUtils.DESCRIPTION);
    }
    public void setDescription(String title) { put(ModelUtils.DESCRIPTION, title); }

    public ParseFile getIcon() {return getParseFile(ModelUtils.ICON);}
    public void setIcon(ParseFile icon) {
        put(ModelUtils.ICON, icon);
    }

}
