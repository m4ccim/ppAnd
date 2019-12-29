package com.example.lb2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.lb1.R;
import com.example.lb2.activity.MainActivity;
import com.example.lb2.model.Importance;
import com.example.lb2.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    private LayoutInflater inflater;
    private int layout;
    private List<Note> filteredNotes;
    private List<Note> notes;
    private ItemFilter mFilter = new ItemFilter();
    private ImportanceFilter iFilter = new ImportanceFilter();


    public NoteAdapter(Context context, int resource, List<Note> notes) {
        super(context, resource, notes);
        this.filteredNotes = new ArrayList<>(notes);
        this.notes = notes;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);

    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView picView = view.findViewById(R.id.pic);
        TextView nameView = view.findViewById(R.id.name);
        TextView dateView = view.findViewById(R.id.date);
        ImageView importanceView = view.findViewById(R.id.importance);


        Note note = filteredNotes.get(position);
        picView.setImageURI(Uri.parse("content://media/external/images/media/"+note.getPicResource()));
        nameView.setText(note.getName());

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(note.getDate());
        dateView.setText(date);
        switch (note.getImportance()){
            case Low: importanceView.setImageResource(R.drawable.l); break;
            case Medium: importanceView.setImageResource(R.drawable.m); break;
            case High: importanceView.setImageResource(R.drawable.h); break;
        }

        return view;
    }
    public int getCount() {

        return filteredNotes.size();
    }


    public long getItemId(int position) {
        return position;
    }

    public Note getItem(int position) {
        return filteredNotes.get(position);
    }

    public Filter getFilter() {
        return mFilter;
    }

    public Filter getIFilter() {
        return iFilter;
    }


    public int getConstNotePosition(int pos){
        int constPos = notes.indexOf(filteredNotes.get(pos));
        return constPos;
    }

    public Note getItemByPosition(int position) {
        return filteredNotes.get(position);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Note> list = notes;

            int count = list.size();
            final ArrayList<Note> nlist = new ArrayList<Note>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(notes.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }



        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }

    public void notifyListChanged(){
        filteredNotes = notes;
        notifyDataSetChanged();

    }
    public void removeNote(int position) {
        Note deleteNote = filteredNotes.remove(position);
        notes.remove(deleteNote);
        notifyListChanged();
    }

    private class ImportanceFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Importance imp = Importance.valueOf(String.valueOf(constraint));
            FilterResults results = new FilterResults();

            final List<Note> list = notes;

            int count = list.size();
            final ArrayList<Note> nlist = new ArrayList<Note>(count);

            Importance filterableImportance ;

            for (int i = 0; i < count; i++) {
                filterableImportance = list.get(i).getImportance();
                if (filterableImportance.equals(imp)) {
                    nlist.add(notes.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }


}

