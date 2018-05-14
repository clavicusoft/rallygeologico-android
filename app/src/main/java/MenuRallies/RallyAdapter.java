package MenuRallies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rallygeologico.R;
import java.util.ArrayList;

import SqlEntities.Rally;

/**
 * Clase para manejar los el adaptador de un Rally
 * Created by Pablo Madrigal on 20/04/2018.
 */

public class RallyAdapter extends RecyclerView.Adapter<RallyAdapter.ViewHolder> {

    //Member Variables
    private ArrayList<Rally> mRallyData;
    private Context mContext;

    /**
     * Constructor that passes in the sports data and the context
     * @param rallyData ArrayList containing the sports data
     * @param context Context of the application
     */
    public RallyAdapter(ArrayList<Rally> rallyData, Context context) {
        this.mRallyData = rallyData;
        this.mContext = context;
    }

    /**
     * Required method for creating the viewholder objects.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly create ViewHolder.
     */
    @Override
    public RallyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     * @param holder The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get current rally
        Rally currentRally = mRallyData.get(position);
        //Populate the textviews with data
        holder.bindTo(currentRally);
        //Populate the image view with the correct image
        //Glide.with(mContext).load(currentRally.getImageURL()).into(holder.mRallyImage);
    }

    /**
     * Required method for determining the size of the data set.
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mRallyData.size();
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        //Member Variables for the TextViews
        private TextView mNameRally;
        private TextView mMemoryUsageRally;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         * @param itemView The rootview of the list_item.xml layout file
         */
        public ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            mNameRally = (TextView)itemView.findViewById(R.id.name);
            mMemoryUsageRally = (TextView)itemView.findViewById(R.id.memoryUsage);
        }

        /**
         * Mete los datos del rally en los ViewText correspondientes
         * @param currentRally Rally que actualmente esta recibiendo la información
         */
        public void bindTo(Rally currentRally) {
            //Populate the textviews with data
            mNameRally.setText(currentRally.getName());
            mMemoryUsageRally.setText(currentRally.getMemoryUsage());
        }

    }
}
