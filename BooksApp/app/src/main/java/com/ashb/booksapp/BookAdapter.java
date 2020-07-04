package com.ashb.booksapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    /* BookAdapter Attributes */
    // a book list which will contain all books to display in the adapter
    private List<Book> books;

    /* Used to describe the item view and the contents of the recycler view */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_title,txt_year, txt_author, txt_publisher, txt_price;

        public MyViewHolder(View view) {
            super(view);
            // initialise GUI attributes to the correct layout ID's
            txt_title = (TextView) view.findViewById(R.id.title);
            txt_year = (TextView) view.findViewById(R.id.year);
            txt_author = (TextView) view.findViewById(R.id.author);
            txt_publisher = (TextView) view.findViewById(R.id.publisher);
            txt_price = (TextView) view.findViewById(R.id.price);
        }
    }

    /* Constructor for the BookAdapter */
    // when created the books from MainActivity will be passed into this class and assigned
    public BookAdapter(List<Book> books) { this.books = books; }

    /* Called when the view holder is created */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflates the book row in the recycler view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row, parent, false);
        return new MyViewHolder(itemView);
    }

    /* Called to display the Book data in the relevant position (parameter "position") */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // gets the book and assigns the relevant views the correct details for each attribute
        Book book = books.get(position);
        holder.txt_title.setText(book.getTitle());
        holder.txt_year.setText(book.getYear());
        String authors_str = "";

        // authors are then separated and concatenated to a string
        // if there are more than 2 authors, "Et al" will append to the end of 2 authors shown
        if (book.getAuthors().size() > 2) {
            authors_str = book.getAuthors().get(0) + ", " + book.getAuthors().get(1) + ", Et al";
        }
        else {
            for (int i = 0; i < book.getAuthors().size(); i++) {
                if (i+1 != book.getAuthors().size()) authors_str += book.getAuthors().get(i) + ", ";
                else authors_str += book.getAuthors().get(i);
            }
        }

        holder.txt_author.setText(authors_str);
        holder.txt_publisher.setText(book.getPublisher());
        // price shown with 2 decimal place
        holder.txt_price.setText("£" + String.format("%.2f", book.getPrice()));
    }

    /* Returns the size of the list */
    @Override
    public int getItemCount() { return books.size(); }

}
