package cs300.apcs04.traveltogether;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note_Main_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    NotesAdapter adapter;
    List<Note> list_of_notes;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("note");

    //long initialCount;
    //int modifyPos = -1;
    long tempDate;
    String tempID;
    final static int REQUEST_CODE_MODIFY_AND_ADD = 1234;
    int callback = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_main);
        Log.d("Main", "onCreate");
        recyclerView = findViewById(R.id.main_list);
        fab = findViewById(R.id.fab);


        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        list_of_notes = new ArrayList<>();
        adapter = new NotesAdapter(Note_Main_Activity.this, list_of_notes);
        recyclerView.setAdapter(adapter);

        // Read data in firebase and insert to list ***********************
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Note post = postSnapshot.getValue(Note.class);
                    list_of_notes.add(post);
                    adapter.notifyDataSetChanged();
                }
                Log.d("Retreving database", "Read database successfully ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Retreving database", "Read database failed ");
            }
        });


        //if (savedInstanceState != null)
        //modifyPos = savedInstanceState.getInt("modify");



        // Floating point action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNoteIntent = new Intent(Note_Main_Activity.this, Note_AddNote_Activity.class);
                startActivityForResult(addNoteIntent, REQUEST_CODE_MODIFY_AND_ADD);
            }
        });

        //Handling swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                //remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                final Note note = list_of_notes.get(viewHolder.getAdapterPosition());
                list_of_notes.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(position);

                // remove on firease*********************************
                myRef.child(note.getNoteID()).removeValue();

                Snackbar.make(recyclerView, "Note deleted.", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //note.save();
                                myRef.child(note.getNoteID()).setValue(note);
                                list_of_notes.add(position, note);
                                adapter.notifyItemInserted(position);
                            }
                        }).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //adapter on click listener
        adapter.SetOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("Main", "click");
                Note tempNote = list_of_notes.get(position);
                tempDate = tempNote.getTime();
                tempID = tempNote.getNoteID();

                Intent i = new Intent(Note_Main_Activity.this, Note_AddNote_Activity.class);
                i.putExtra("isEditing", true);
                i.putExtra("note_title", tempNote.getTitle());
                i.putExtra("note_description", tempNote.getDescription());
                i.putExtra("note_ID", tempNote.getNoteID());

                startActivityForResult(i, REQUEST_CODE_MODIFY_AND_ADD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MODIFY_AND_ADD && resultCode == RESULT_OK){

            callback = data.getIntExtra("callback", 0);
            Note newNote = (Note) data.getSerializableExtra("NewNote");

            Log.d("SIZE", "onResume: " + list_of_notes.size());
            ;
            if (callback == 2){
                //a note is added
                Log.d("Main", "Adding new note");

                // Just load the last added note (new)
                list_of_notes.add(newNote);

                // list_of_notes.add(note);
                adapter.notifyDataSetChanged();
            }

            if (callback == 1){
                //list_of_notes.set(modifyPos, Note.listAll(Note.class).get(modifyPos));
                Note note = null;
                for(int i = 0; i < list_of_notes.size(); i++){
                    Note tmpNote =  list_of_notes.get(i);
                    if(tmpNote.getNoteID().equals(tempID) && tempDate==tmpNote.getTime()){
                        note = tmpNote;
                        list_of_notes.add(i, newNote);
                        list_of_notes.remove(note);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //final long newCount = Note.count(Note.class);

    }
}
