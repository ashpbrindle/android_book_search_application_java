package com.ashb.booksapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class SearchFragment extends Fragment {

    /* SearchFragment Attributes */
    private Button btn_search;
    private EditText txt_title;
    private EditText txt_year;
    private SearchListener listener;

    public SearchFragment() {}

    /* Interface which listens for interaction with the fragment */
    public interface SearchListener { void SearchInteraction(String title, String year); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_search,
                container, false);

        // initialise GUI attributes
        txt_title = (EditText) rootView.findViewById(R.id.txtTitle);
        txt_year = (EditText) rootView.findViewById(R.id.txtYear);
        btn_search = (Button) rootView.findViewById(R.id.btnSearch);

        //  gets bundle data for when the view is recreated (to retain its previous state)
        //  and assign those changes
        txt_title.setText(getArguments().getString("title"));
        txt_title.setText(getArguments().getString("title"));
        txt_year.setText(getArguments().getString("year"));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // passes the search terms back to the listener in MainActivity
                    listener.SearchInteraction(txt_title.getText().toString(), txt_year.getText().toString());

                    // removes keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchListener) listener = (SearchListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /* Creates a new instance of this fragment */
    public static SearchFragment newInstance() { return new SearchFragment(); }

}