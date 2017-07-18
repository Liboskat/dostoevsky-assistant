package rizvanov.dostoevskyassistant.fragment_notes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by Ильшат on 14.07.2017.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {
    private List<Note> notes;
    private OnEventClickListener listener;

    public NoteListAdapter(List<Note> notes, OnEventClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        public TextView creationDateTextView;
        public TextView shortDescriptionTextView;
        public RelativeLayout relativeLayout;

        public NoteViewHolder(View itemView) {
            super(itemView);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.diary_page_element_layout);
            shortDescriptionTextView = (TextView) itemView.findViewById(R.id.diary_page_element_shorttext);
            creationDateTextView = (TextView) itemView.findViewById(R.id.diary_page_element_creationdate);
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_page_element_layout, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, int position) {
        final Note note = notes.get(position);

        holder.shortDescriptionTextView.setText(note.getText());
        holder.creationDateTextView.setText(note.getDate());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnEventClick(notes.get(holder.getAdapterPosition()));
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.OnLongEventClick(notes.get(holder.getAdapterPosition()));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    interface  OnEventClickListener {
        void OnEventClick(Note note);

        void OnLongEventClick(Note note);
    }
}
