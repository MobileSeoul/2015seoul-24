package co.askseoulites.seoulcityapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.model.CategoryUtils;
import co.askseoulites.seoulcityapp.model.Collection;
import co.askseoulites.seoulcityapp.model.ModelUtils;

/**
 * Created by hassanabid on 10/30/15.
 */
public class CollectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_CATEGORY_POSITION = "category_position";
    private static final String LOG_TAG = CollectionFragment.class.getSimpleName();
    private int catPos;
    static List<Collection> collections;
    private TextView collectionTitle;
    private int viewPagePos;
    private OnCollectionSelectedListener mListener;
    private TextView collectionTag;
    private TextView collectionWriter;
    private TextView collectionNumber;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CollectionFragment newInstance(int sectionNumber, int catPos, List<Collection> c) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARG_CATEGORY_POSITION,catPos);
        fragment.setArguments(args);
        collections = c;
        return fragment;
    }

    public CollectionFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCollectionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCollectionSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catPos = getArguments().getInt(ARG_CATEGORY_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_cat_v, container, false);
        collectionTag = (TextView) rootView.findViewById(R.id.tagTitle);
        viewPagePos = getArguments().getInt(ARG_SECTION_NUMBER);
        collectionTitle = (TextView) rootView.findViewById(R.id.collectionTitle);
        collectionWriter = (TextView) rootView.findViewById(R.id.collectionWriter);
        collectionNumber = (TextView) rootView.findViewById(R.id.collectionNo);
        setupViewPage();
        return rootView;
    }

    private void setupViewPage() {

        if(collectionTitle == null )
            return;

        if (collections.size() == 0 || viewPagePos > (collections.size()-1)) {
            collectionTitle.setText(getResources().getString(R.string.no_collection_available));
            return;
        }
        collectionNumber.setText(String.valueOf(viewPagePos));
        Collection collection = collections.get(viewPagePos);
        int tagPos = collection.getTags().get(0);
        collectionTag.setText(CategoryUtils.getTagTitle(tagPos, getActivity()));
        String name = collection.getWriter().getString(ModelUtils.NAME);
        if(name != null)
            collectionWriter.setText(name);

        collectionTitle.setText(collection.getTitle());
        collectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCollectionSelected(viewPagePos);
            }
        });

    }

    public interface OnCollectionSelectedListener {
        public void onCollectionSelected(int position);
    }
}