package example.com.shepherd;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import example.com.shepherd.data.EventContract;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventItemViewHolder> {
    private Cursor mCursor;

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int id);
    }

    public EventAdapter(ListItemClickListener listener, Cursor cursor) {
        mOnClickListener = listener;
        mCursor = cursor;
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        EventItemViewHolder viewHolder = new EventItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class EventItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int eventId;
        TextView listItemNameView;
        TextView listItemDescriptionView;

        EventItemViewHolder(View itemView) {
            super(itemView);

            listItemNameView = (TextView) itemView.findViewById(R.id.tv_event_list_item_name);
            listItemDescriptionView = (TextView) itemView.findViewById(R.id.tv_event_list_item_description);

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            if (!mCursor.moveToPosition(listIndex))
                return; // bail if returned null

            String name = mCursor.getString(mCursor.getColumnIndex(EventContract.EventEntry.COLUMN_EVENT_NAME));
            listItemNameView.setText(name);

            String description = mCursor.getString(mCursor.getColumnIndex(EventContract.EventEntry.COLUMN_DESCRIPTION));
            listItemDescriptionView.setText(description);

            eventId = mCursor.getInt(mCursor.getColumnIndex(EventContract.EventEntry._ID));
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(eventId);
        }
    }
}
