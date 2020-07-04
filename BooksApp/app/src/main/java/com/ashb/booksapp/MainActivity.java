package com.ashb.booksapp;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SearchFragment.SearchListener, FormatFragment.FormatListener, LoaderManager.LoaderCallbacks<String> {

    /* GUI Attributes */
    public RecyclerView rec_view;
    public BookAdapter book_adapter;
    private Button btn_open_search;
    private Button btn_open_format;
    private TextView txt_format_read;

    /* Constant Attributes */
    // states for the bundle
    static final String STATE_FRAGMENT = "state_of_fragment";
    static final String STATE_TITLE = "state_of_title";
    static final String STATE_YEAR = "state_of_year";
    static final String STATE_FORMAT = "state_of_format";
    static final String STATE_FORMAT_FRAGMENT = "state_of_format_fragment";
    static final String BOOKS_ARRAY_LIST = "books_array_list";
    // defines decoding type
    static final int DECODE_JSON = 1;
    static final int DECODE_XML = 2;

    /* Other Attributes */
    int decode_type = -1;
    // holds the search term received from the search fragment
    private String searchterm_title;
    private String searchterm_year;
    // defines which fragment(s) is open
    private Boolean fragment_open = false;
    private Boolean format_fragment_open = false;
    // both array lists to hold all the books and the filtered list
    private ArrayList<Book> books = new ArrayList<>();
    public ArrayList<Book> searched_books = new ArrayList<>();

    /* Runs when the Activity is created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hides action bar
        try { this.getSupportActionBar().hide(); }
        catch (NullPointerException e){}

        // initialises the GUI attributes
        btn_open_search = (Button) findViewById(R.id.open_search_button);
        btn_open_format = (Button) findViewById(R.id.open_format_button);
        txt_format_read = (TextView) findViewById(R.id.txt_format_header);
        rec_view = (RecyclerView) findViewById(R.id.rec_books);

        // listener for the search button
        btn_open_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initially closes the other fragment
                if (format_fragment_open) closeFormat();
                if (fragment_open == false) openSearch();
                else if (fragment_open == true)closeSearch();
            }
        });
        // listener for the format button
        btn_open_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initially closes the other fragment
                if (fragment_open) closeSearch();
                if (format_fragment_open == false) openFormat();
                else if (format_fragment_open == true) closeFormat();
            }
        });

        // initialise the SupportLoaderManager for the AsyncTask
        if (LoaderManager.getInstance(this).getLoader(0) != null) {
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }

        // create book adapter and set up recycler view
        book_adapter = new BookAdapter(books);
        RecyclerView.LayoutManager rec_layout_manager = new LinearLayoutManager(getApplicationContext());
        rec_view.setLayoutManager(rec_layout_manager);
        rec_view.setItemAnimator(new DefaultItemAnimator());
        rec_view.setAdapter(book_adapter);
        if (books.size() > 0) searchForBooks();
    }

    /* Called when the back button is pressed */
    @Override
    public void onBackPressed() {
        // creates an alert to confirm closer of the application
        new AlertDialog.Builder(this)
                .setMessage("Do You Wish To Close The Application?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { MainActivity.super.onBackPressed(); }})
                .setNegativeButton("No", null)
                .show();
    }

    private void searchForBooks() {
        searched_books = new ArrayList<>();
        if (searchterm_title == null) searchterm_title = "";
        if (searchterm_year == null) searchterm_year = "";

        // checks each book in the array for if it contains the search query
        for (int i = 0; i < books.size(); i++) {
            if (!searchterm_year.equals("") && searchterm_title.equals("")) {  // if title empty and year not empty
                if (books.get(i).getYear().toLowerCase().contains(searchterm_year)) searched_books.add(books.get(i));
            }
            else if (!searchterm_title.equals("") && searchterm_year.equals("")) {  // if year empty and title not empty
                if (books.get(i).getTitle().toLowerCase().contains(searchterm_title)) searched_books.add(books.get(i));
            }
            else if (!searchterm_title.equals("") && !searchterm_year.equals("")) { // if year not empty and title not empty
                if (books.get(i).getYear().toLowerCase().contains(searchterm_year) && books.get(i).getTitle().toLowerCase().contains(searchterm_title)) searched_books.add(books.get(i));
            }
        }

        // if no search was made, the adapter will not be swapped to the searched array
        if (searchterm_year.equals("") && searchterm_title.equals("")) {
            book_adapter = new BookAdapter(books);  // pass in array list to show
            rec_view.swapAdapter(book_adapter, false);
        }
        // if there was a search term, the adapter will be searched to the filtered results
        else {
            book_adapter = new BookAdapter(searched_books);
            rec_view.swapAdapter(book_adapter, false);
        }
    }

    private void openSearch() {
        try {
            // bundles data into fragment to show in the search text views
            Bundle bundle = new Bundle();
            bundle.putString("title", searchterm_title);
            bundle.putString("year", searchterm_year);
            SearchFragment search = SearchFragment.newInstance();
            search.setArguments(bundle);

            // transaction is made to the FrameLayout with the SearchFragment
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container, search).commit();

            // attributes updated
            btn_open_search.setText("Close Search");
            fragment_open = true;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void closeSearch() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            // get the fragment by its ID
            SearchFragment search_fragment = (SearchFragment) manager.findFragmentById(R.id.fragment_container);
            if (search_fragment != null) {
                // remove fragments from the transactions
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(search_fragment).commit();
            }

            // attributes updated
            btn_open_search.setText("Open Search");
            fragment_open = false;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openFormat() {
        try {
            // bundles data into fragment to show in the search text views
            Bundle bundle = new Bundle();
            bundle.putInt("format", decode_type);
            FormatFragment format = FormatFragment.newInstance();
            format.setArguments(bundle);

            // transaction is made to the FrameLayout with the FormatFragment
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container, format).commit();

            // attributes updated
            btn_open_format.setText("Close Format");
            format_fragment_open = true;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void closeFormat() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            // get the fragment by its ID
            FormatFragment format_fragment = (FormatFragment) manager.findFragmentById(R.id.fragment_container);
            if (format_fragment != null) {
                // remove fragments from the transactions
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(format_fragment).commit();
            }

            // attributes updated
            btn_open_format.setText("Open Format");
            format_fragment_open = false;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // bundles the relevant data to the activity for when the view is rotated
        savedInstanceState.putBoolean(STATE_FRAGMENT, fragment_open);
        savedInstanceState.putBoolean(STATE_FORMAT_FRAGMENT, format_fragment_open);
        savedInstanceState.putString(STATE_TITLE, searchterm_title);
        savedInstanceState.putString(STATE_YEAR, searchterm_year);
        savedInstanceState.putString(STATE_FORMAT, txt_format_read.getText().toString());
        savedInstanceState.putParcelableArrayList(BOOKS_ARRAY_LIST, books);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // initially ensures both fragments are closed before restoring states
        closeSearch();
        closeFormat();
        // reads received bundle states
        fragment_open = savedInstanceState.getBoolean(STATE_FRAGMENT);
        format_fragment_open = savedInstanceState.getBoolean(STATE_FORMAT_FRAGMENT);
        searchterm_title = savedInstanceState.getString(STATE_TITLE);
        searchterm_year = savedInstanceState.getString(STATE_YEAR);
        txt_format_read.setText(savedInstanceState.getString(STATE_FORMAT));
        books = savedInstanceState.getParcelableArrayList(BOOKS_ARRAY_LIST);

        // attributes updated
        if (txt_format_read.getText() == "Format: JSON") decode_type = DECODE_JSON;
        else if (txt_format_read.getText() == "Format: XML") decode_type = DECODE_XML;

        // reopens/closes fragments depending on previous state
        if (fragment_open == true) openSearch();
        else if (fragment_open == false) closeSearch();
        if (format_fragment_open == true) openFormat();
        else if (format_fragment_open == false) closeFormat();
        // performs the search again to show filtered results from previous states
        searchForBooks();
        Log.d("Books", Integer.toString(rec_view.getAdapter().getItemCount()));
    }

    /* Listener for the Search Fragment Interaction */
    @Override
    public void SearchInteraction(String title, String year) {
        // saves the title and year from the search
        searchterm_title = title.toLowerCase();
        searchterm_year = year.toLowerCase();
        // searches the books array
        searchForBooks();
    }

    /* Listener for the Format Fragment Interaction */
    @Override
    public void FormatInteraction(int format) {
        // displays the relevant data depending on the format selected
        if (format == DECODE_XML) {
            decode_type = DECODE_XML;
            Bundle bundle = new Bundle();
            LoaderManager.getInstance(this).restartLoader(0, bundle, this);
        }
        else if (format == DECODE_JSON) {
            decode_type = DECODE_JSON;
            Bundle bundle = new Bundle();
            LoaderManager.getInstance(this).restartLoader(0, bundle, this);
        }
    }

    /*  When a loader is created the relevant parameter will be added
        to decode in either JSON or XML */
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (decode_type == DECODE_JSON) return new BookLoader(this, DECODE_JSON);
        else return new BookLoader(this, DECODE_XML);
    }

    /* Called when data is loaded from the URL in the AsyncTask */
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        //  decodes in the relevant format (XML or JSON), a check is also made for if the data
        //  has already been retrieved (when screen rotation changed)
        if (decode_type == DECODE_JSON && txt_format_read.getText().toString() != "Format: JSON") {
            int i = 0;
            try {
                // creates a json object and creates an array for the contents of the library TAG
                JSONObject json_obj = new JSONObject(s);
                JSONArray books_array = json_obj.getJSONArray("library");
                // clear books in case data is already present
                books.clear();
                // loop runs through all of the books in the library
                while (i < books_array.length()) {
                    // assigns all of the values to a book object
                    JSONObject book = books_array.getJSONObject(i);
                    Book book_obj = new Book();

                    try {
                        book_obj.setTitle(book.getString("title"));
                        book_obj.setYear(book.getString("year"));
                        JSONArray authors = book.getJSONArray("author");

                        for (int author = 0; author < authors.length(); author++) {
                            book_obj.addAuthor(authors.getString(author));
                        }

                        book_obj.setPublisher(book.getString("publisher"));
                        book_obj.setPrice(book.getDouble("price"));
                        // the object then gets added to the books array
                        books.add(book_obj);

                    } catch (Exception e) { e.printStackTrace(); }
                    i++;
                }

                // adapter is then swapped from the searched result to the new data set
                book_adapter = new BookAdapter(books);  // pass in array list to show
                rec_view.swapAdapter(book_adapter, false);
                txt_format_read.setText("Format: JSON");
                book_adapter.notifyDataSetChanged();
                //  when the decoding is finished on either JSON or XML, the search terms are reset
                //  for the new data set
                searchterm_title = "";
                searchterm_year = "";

                for (int x = 0; x < books.size(); x++) searched_books.add(books.get(i));

            } catch (Exception e) { e.printStackTrace(); }

        }
        //  decodes in the relevant format (XML or JSON), a check is also made for if the data
        //  has already been retrieved (when screen rotation changed)
        else if (decode_type == DECODE_XML && txt_format_read.getText().toString() != "Format: XML"){
            // creates a book object and clears the books array in case any data is already present
            Book book = null;
            this.books.clear();
            // XML parser is then created to get the XML data from the string
            XmlPullParser data;

            try {
                // converts the string to InputStream, so it can be used with the parser
                InputStream stream = new ByteArrayInputStream(s.getBytes());
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                data = factory.newPullParser();
                data.setInput(stream, "UTF_8");

                // event type is then stored
                int event_type = data.getEventType();
                // cycles through until the end of the document
                while (event_type != XmlPullParser.END_DOCUMENT) {
                    // tag is then acquired
                    String tag_name = data.getName();

                    // if that tag is a book, this will then be added to books
                    switch (event_type) {
                        case XmlPullParser.START_TAG:
                            if (tag_name.equals("book")) {
                                book = new Book();
                                this.books.add(book);
                            }
                            //  if it is not a book, the tag must be inside the book and will be
                            //  decoded depending on which is the current tag
                            else if (book != null) {
                                Log.d("TAG", " START TAG");
                                if (tag_name.equals("title")) book.setTitle(data.nextText());
                                else if (tag_name.equals("year")) book.setYear(data.nextText());
                                else if (tag_name.equals("author")) book.addAuthor(data.nextText());
                                else if (tag_name.equals("publisher")) book.setPublisher(data.nextText());
                                else if (tag_name.equals("price")) book.setPrice(Double.valueOf(data.nextText()));
                            }
                            break;
                    }
                    // cycles to next tag
                    event_type = data.next();
                }

                // adapter is then swapped from the searched result to the new data set
                book_adapter = new BookAdapter(this.books);  // pass in array list to show
                rec_view.swapAdapter(book_adapter, false);
                txt_format_read.setText("Format: XML");
                for (int i = 0; i < books.size(); i++) searched_books.add(books.get(i));
                book_adapter.notifyDataSetChanged();
                //  when the decoding is finished on either JSON or XML, the search terms are reset
                //  for the new data set
                searchterm_title = "";
                searchterm_year = "";

            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) { }
}

