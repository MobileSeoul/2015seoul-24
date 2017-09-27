package co.askseoulites.seoulcityapp.model;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hassanabid on 10/25/15.
 */
@ParseClassName("Question")
public class Question extends ParseObject {

    private static final String LOG_TAG = "Board";
    private  static List<Question> result ;


    public Question() {
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

    public List<ParseUser> getVoters(){ return getList(ModelUtils.VOTERS);}
    public void addVoter(ParseUser user,Question q){ q.addUnique(ModelUtils.VOTERS,user);}

    public int getAnswersCount() { return getInt(ModelUtils.ANSWERS_COUNT);}
    public void setAnswersCount(int answersCount) {  put(ModelUtils.ANSWERS_COUNT,answersCount);}


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
    public static ParseQuery<Question> createQuery(int catPosition) {
        ParseQuery<Question> query = new ParseQuery<Question>(Question.class);
        query.whereEqualTo(ModelUtils.CATNO,catPosition);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.VOTES);

        return query;
    }


    /**
     * Creates a local query for questions with all the includes
     */
    public static ParseQuery<Question> createLocalQuery(int catPosition) {

        ParseQuery<Question> query = new ParseQuery<Question>(Question.class);
        query.whereEqualTo(ModelUtils.CATNO,catPosition);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.VOTES);
        query.fromLocalDatastore();

        return query;
    }

    public static ParseQuery<Question> createSingleLocalQuery(String objectId) {

        ParseQuery<Question> query = new ParseQuery<Question>(Question.class);
        query.include(ModelUtils.WRITER);
        query.whereEqualTo(ModelUtils.OBJECT_ID, objectId);
        query.fromLocalDatastore();

        return query;
    }

    public static ParseQuery<Question> createSingleQuery(String objectId) {

        ParseQuery<Question> query = ParseQuery.getQuery("Question");
        query.include(ModelUtils.WRITER);
        query.whereEqualTo(ModelUtils.OBJECT_ID, objectId);

        return query;
    }

}
