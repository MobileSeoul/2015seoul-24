package co.askseoulites.seoulcityapp.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by hassanabid on 10/27/15.
 */
@ParseClassName("Answer")
public class Answer extends ParseObject {

    public Answer() {

    }

    public String getContent() { return getString(ModelUtils.CONTENT); }
    public void setContent(String content) { put(ModelUtils.CONTENT, content);}

    public ParseUser getWriter() { return getParseUser(ModelUtils.WRITER); }
    public void setWriter(ParseUser writer) { put(ModelUtils.WRITER, writer); }

    public void setQuestion(Question q) { put(ModelUtils.QUESTION,q); }
    public Question getQuestion() { return (Question)getParseObject(ModelUtils.QUESTION); }

    public void setStatus(boolean status) { put(ModelUtils.STATUS,status);}
    public boolean getStatus () { return  getBoolean(ModelUtils.STATUS); }

    public void setLocation(ParseGeoPoint point) { put(ModelUtils.LOCATION,point);}
    public ParseGeoPoint getLocation() { return getParseGeoPoint(ModelUtils.LOCATION);}


    /**
     * Creates a query for questions with all the includes
     */
    public static ParseQuery<Answer> createQuery(Question q) {

        ParseQuery<Answer> query = new ParseQuery<Answer>(Answer.class);
        query.whereEqualTo(ModelUtils.QUESTION, q);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.CREATED_AT);

        return query;
    }


    /**
     * Creates a local query for questions with all the includes
     */
    public static ParseQuery<Answer> createLocalQuery(Question q) {

        ParseQuery<Answer> query = new ParseQuery<Answer>(Answer.class);
        query.whereEqualTo(ModelUtils.QUESTION, q);
        query.include(ModelUtils.WRITER);
        query.orderByDescending(ModelUtils.CREATED_AT);
        query.fromLocalDatastore();

        return query;
    }


}
