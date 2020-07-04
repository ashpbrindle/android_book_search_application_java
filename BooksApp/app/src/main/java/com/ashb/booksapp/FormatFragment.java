package com.ashb.booksapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FormatFragment extends Fragment {

    /* BookAdapter Attributes */
    private Button btn_JSON;
    private Button btn_XML;
    private FormatListener listener;
    // used to differentiate how the data will be formatted
    static final int DECODE_JSON = 1;
    static final int DECODE_XML = 2;

    /* Interface which listens for interaction with the fragment */
    public interface FormatListener { void FormatInteraction(int decode_type);}

    /* Runs when fragment is created */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment.
        final View rootView = inflater.inflate(R.layout.fragment_format,
                container, false);

        // initialise GUI attributes
        btn_JSON = (Button) rootView.findViewById(R.id.btnJSON);
        btn_XML = (Button) rootView.findViewById(R.id.btnXML);

        // gets bundle data for when the view is recreated (to retain its previous state)
        int format = getArguments().getInt("format");
        // assigns those changes
        if (format == DECODE_JSON) {
            btn_JSON.setEnabled(false);
            btn_XML.setEnabled(true);
        }
        else if (format == DECODE_XML) {
            btn_JSON.setEnabled(true);
            btn_XML.setEnabled(false);
        }

        // listener for the JSON button
        btn_JSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // when button is pressed the relevant button will get disabled
                    btn_JSON.setEnabled(false);
                    btn_XML.setEnabled(true);
                    // and the listener will get called in MainActivity to load the correct books
                    listener.FormatInteraction(DECODE_JSON);
                }
            }
        });

        // listener for the JSON button
        btn_XML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // when button is pressed the relevant button will get disabled
                    btn_JSON.setEnabled(true);
                    btn_XML.setEnabled(false);
                    // and the listener will get called in MainActivity to load the correct books
                    listener.FormatInteraction(DECODE_XML);
                }
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormatListener) listener = (FormatListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /* Creates a new instance of this fragment */
    public static FormatFragment newInstance() { return new FormatFragment(); }

}
