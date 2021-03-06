package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_COURSE_ID = "com.example.notekeeper.ORIGINAL_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo noteValue;
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int newNotePosition;
    private boolean isCancelled;
    private String originalCourseId;
    private String originalNoteTitle;
    private String originalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = findViewById(R.id.spinner_courses);
        textNoteTitle = findViewById(R.id.text_note_title);
        textNoteText = findViewById(R.id.text_note_text);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if (savedInstanceState == null){
            saveOriginalNoteValues();
        }else {
            restoreOriginalNoteValues(savedInstanceState);
        }


        //if there's a new note, don't call the display note function
        if (!isNewNote) {
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        }
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        originalCourseId = savedInstanceState.getString(ORIGINAL_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_COURSE_ID, originalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);
    }

    private void saveOriginalNoteValues() {
        if (isNewNote){
            return;
        }

        originalCourseId = noteValue.getCourse().getCourseId();
        originalNoteTitle = noteValue.getTitle();
        originalNoteText = noteValue.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(spinnerCourses);
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(noteValue.getTitle());
        textNoteText.setText(noteValue.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int notePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = notePosition == POSITION_NOT_SET;

        if (isNewNote){
            createNewNote();
        } else{
            noteValue = DataManager.getInstance().getNotes().get(notePosition);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        newNotePosition = dm.createNewNote();
        noteValue = dm.getNotes().get(newNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelled){
            if (isNewNote) {
                DataManager.getInstance().removeNote(newNotePosition);
            }else {
                restoreOriginalNoteValues();
            }
        }else {
            saveNote();
        }
    }

    private void restoreOriginalNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalCourseId);
        noteValue.setCourse(course);
        noteValue.setTitle(originalNoteTitle);
        noteValue.setText(originalNoteText);
    }

    private void saveNote() {
        noteValue.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        noteValue.setTitle(textNoteTitle.getText().toString());
        noteValue.setText(textNoteText.getText().toString());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(newNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendMail();
            return true;
        }else if (id == R.id.action_cancel){
            isCancelled = true;
            finish();
        }else if (id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        saveNote();
        ++newNotePosition;

        noteValue = DataManager.getInstance().getNotes().get(newNotePosition);

        saveOriginalNoteValues();
        displayNote(spinnerCourses, textNoteTitle, textNoteText);
        invalidateOptionsMenu();
    }

    private void sendMail() {
        CourseInfo courses = (CourseInfo) spinnerCourses.getSelectedItem();
        String mailSubject = textNoteTitle.getText().toString();
        String mailBody = "Check what I learned in pluralsight course \"" +
                courses.getTitle() + "\"\n" + textNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, mailBody);
        startActivity(intent);
    }
}
