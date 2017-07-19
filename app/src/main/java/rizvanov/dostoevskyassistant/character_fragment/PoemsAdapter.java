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

public class PoemsAdapter  extends RecyclerView.Adapter<PoemsAdapter.PoemViewHolder> {

    private  List<Poem> poems;
    private PoemsPageOnClickListener poemsPageOnClickListener;

    public PoemsAdapter(List<Poem> poems,PoemsPageOnClickListener poemsPageOnClickListener){

        this.poems = poems;
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
    public void onBindViewHolder(final PoemViewHolder holder, int position) {
        final String title  = poems.get(position).getTitle();
        holder.titleTextView.setText(title);
        holder.relativePageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.relative_page_layout:
                        poemsPageOnClickListener.openPageCharacter(title);
                        break;
                    default:
                        break;
                }
            }
        });
        holder.relativePageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return poemsPageOnClickListener.changePoem(holder.getAdapterPosition());

            }
        });

    }


    @Override
    public int getItemCount() {
        return poems.size();
    }

    public interface PoemsPageOnClickListener{

        void openPageCharacter(String title);

        boolean changePoem(int adapterPosition);
    }
}
