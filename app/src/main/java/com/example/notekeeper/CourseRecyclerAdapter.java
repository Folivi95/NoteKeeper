package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final List<CourseInfo> _courses;
    private final LayoutInflater _layoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> notes) {
        _context = context;
        _courses = notes;
        _layoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = _layoutInflater.inflate(R.layout.item_course_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseInfo course = _courses.get(position);
        holder._textCourse.setText(course.getTitle());
        holder._currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return _courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView _textCourse;
        public int _currentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, _courses.get(_currentPosition).getTitle(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
