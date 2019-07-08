package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final List<NoteInfo> _notes;
    private final LayoutInflater _layoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        _context = context;
        _notes = notes;
        _layoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = _layoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoteInfo note = _notes.get(position);
        holder._textCourse.setText(note.getCourse().getTitle());
        holder._textTitle.setText(note.getTitle());
        holder._currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return _notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView _textCourse;
        public final TextView _textTitle;
        public int _currentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);
            _textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_context, MainActivity.class);
                    intent.putExtra(MainActivity.NOTE_POSITION, _currentPosition);
                    _context.startActivity(intent);
                }
            });
        }
    }
}
