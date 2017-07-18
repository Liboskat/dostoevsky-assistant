package rizvanov.dostoevskyassistant.character_fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import rizvanov.dostoevskyassistant.R;

/**
 * Created by user on 14.07.2017.
 */

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private List<Character> characters;

    private CharacterOnClickListener characterOnClickListener;

    public CharacterAdapter(List<Character> quests,CharacterOnClickListener characterOnClickListener){

        this.characters = quests;
        this.characterOnClickListener = characterOnClickListener;

    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder{

        public TextView nameOfCharacter;
        public RelativeLayout relativeLayout;

        public CharacterViewHolder(View itemView){
            super(itemView);

            nameOfCharacter = (TextView) itemView.findViewById(R.id.reflections_characterpage_element_shorttext);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_character_layout);

        }
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reflections_characterpage_element_layout,parent,false);
        return new CharacterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CharacterViewHolder holder, final int position) {
        final Character character = characters.get(position);

        holder.nameOfCharacter.setText(character.getName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                characterOnClickListener.completeCharacter(characters.get(holder.getAdapterPosition()),holder.getAdapterPosition());

            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return characterOnClickListener.changeCharacter(holder.getAdapterPosition());

            }
        });

    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public interface CharacterOnClickListener{

        void completeCharacter(Character character, int position);

        boolean changeCharacter(int adapterPosition);
    }
}


