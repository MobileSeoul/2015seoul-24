package co.askseoulites.seoulcityapp.model;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by hassanabid on 10/28/15.
 */
@ParseClassName("Collection")
public class Collection extends ParseObject {

    private static final String LOG_TAG = "Board";
    private  static List<Question> result ;


    public Collection() {
    }

    public String getTitle() {
        return getString(ModelUtils.TITLE);
    }
    public void setTitle(String title) {
        put(ModelUtils.TITLE, title);
    }

    public String getContent() { return getString(ModelUtils.CONTENT); }
    public void setContent(String content) { put(ModelUtils.CONTENT, content);}

    public ParseFile getPhoto() {return getParseFile(ModelUtils.PHOTO);}
    public void setPhoto(ParseFile photo) {
        put(ModelUtils.PHOTO, photo);
    }
    public void setThumbnail(ParseFile thumb) { put(ModelUtils.THUMBNAIL, thumb); }
    public ParseFile getThumbnail() {
        return getParseFile(ModelUtils.THUMBNAIL);
    }


    public String getlink() { return getString(ModelUtils.LINK);}
    public void setlink(String link) {
        put(ModelUtils.LINK, link);
    }

    public Category getCategory() { return (Category )get(ModelUtils.CATEGORY); }
    public void setCategory(Category category) { put(ModelUtils.CATEGORY, category); }

    public int getCatNo() { return getInt(ModelUtils.CATNO); }
    public void setCatNo(int views) { put(ModelUtils.CATNO, views); }

    public void setVotes(int votes) {
        put(ModelUtils.VOTES,votes);
    }
    public int getVotes() { return getInt(ModelUtils.VOTES); }

    public ParseUser getWriter() { return getParseUser(ModelUtils.WRITER); }
    public void setWriter(ParseUser writer) { put(ModelUtils.WRITER, writer); }

    public boolean isSolved() { return getBoolean(ModelUtils.SOLVED);}
    public void setIsSolved(boolean solved) { put(ModelUtils.SOLVED,solved); }

/*    public String getcreatedAt() {

        return convertDateToString(getCreatedAt());

    }*/


    public List<Integer> getTags() {

        return getList(ModelUtils.TAG);
    }

    public void setTags(List<Integer> tags) {

        put(ModelUtils.TAG,tags);
    }

    /**
     * Creates a query for questions with all the includes
     */
    public static ParseQuery<Collection> createQuery(int catPosition) {
        ParseQuery<Collection> query = new ParseQuery<Collection>(Collection.class);
        query.whereEqualTo(ModelUtils.CATNO,catPosition);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.VOTES);

        return query;
    }


    /**
     * Creates a local query for questions with all the includes
     */
    public static ParseQuery<Collection> createLocalQuery(int catPosition) {

        ParseQuery<Collection> query = new ParseQuery<Collection>(Collection.class);
        query.whereEqualTo(ModelUtils.CATNO,catPosition);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.VOTES);
        query.fromLocalDatastore();

        return query;
    }

    public static ParseQuery<Collection> createSingleLocalQuery(String objectId) {

        ParseQuery<Collection> query = new ParseQuery<Collection>(Collection.class);
        query.include(ModelUtils.WRITER);
        query.fromLocalDatastore();

        return query;
    }

    public static ParseQuery<Collection> createSingleQuery(String objectId) {

        ParseQuery<Collection> query = ParseQuery.getQuery("Collection");
        query.include(ModelUtils.WRITER);
        return query;
    }

}
