package rizvanov.dostoevskyassistant.CharacterFragment;

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

public class PoemsAdapter  extends RecyclerView.Adapter<PoemsAdapter.PoemViewHolder> {

    public List<Poem> quests;
    PoemsPageOnClickListener poemsPageOnClickListener;

    public PoemsAdapter(List<Poem> quests,PoemsPageOnClickListener poemsPageOnClickListener){

        this.quests = quests;
        this.poemsPageOnClickListener = poemsPageOnClickListener;
    }

    public static class PoemViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView;

        public RelativeLayout relativePageLayout;

        public PoemViewHolder(View itemView){
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.reflections_page_element_shorttext);
            relativePageLayout = (RelativeLayout) itemView.findViewById(R.id.relative_page_layout);

        }
    }

    @Override
    public PoemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reflections_page_element_layout,parent,false);
        return new PoemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PoemViewHolder holder, int position) {
        Poem quest = quests.get(position);

        holder.titleTextView.setText(quest.getTitle());
       holder.relativePageLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               switch (view.getId()){
                   case R.id.relative_page_layout:
                       poemsPageOnClickListener.openPageCharacter();
                       break;
                   default:
                       break;
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    public interface PoemsPageOnClickListener{

        void openPageCharacter();
    }
}
