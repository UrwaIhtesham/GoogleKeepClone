package com.example.l215404.googlekeep.HomePage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l215404.googlekeep.R;

import java.util.List;

public class OtherNoteAdapter extends RecyclerView.Adapter<OtherNoteAdapter.NoteViewHolder> {
    private List<Note> noteList;
    private Context context;
    private onNoteClickListener onNoteClickListener;

    public OtherNoteAdapter(List<Note> noteList, Context context, onNoteClickListener onNoteClickListener) {
        this.noteList = noteList;
        this.context = context;
        this.onNoteClickListener = onNoteClickListener;
    }

    public interface onNoteClickListener {
        void onNoteClick(Note note);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("NoteAdapter", "onCreateViewHolder of OtherNoteAdapter: Helooooo");
        View view = LayoutInflater.from(context).inflate(R.layout.note_list, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        Log.d("NoteAdapter", "OtherNote Note: " + position);
        Log.d("NoteAdapter", "OtherNote NoteTitle: " + note.getTitle() + " and " + note.getContent());
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

        holder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("NoteAdapter", "Inside OtherNoteAdapter getItemCount: "+noteList.size());
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
        }
    }
}
