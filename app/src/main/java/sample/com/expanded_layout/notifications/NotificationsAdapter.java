package sample.com.expanded_layout.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import widget.com.expandedcardview.R;
import widget.com.expandedcardview.databinding.NotificationRowBinding;


/**
 * Created by fawzy
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<NotificationsDM> dataList;
    private LayoutInflater layoutInflater;
    private NotificationRowBinding binding;
    private RecyclerView recyclerView;
    private int expandedPos = -1;

    public NotificationsAdapter(List<NotificationsDM> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.notification_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 60;//dataList.size();
    }

    public void setDataList(List<NotificationsDM> faqs) {
        this.dataList = faqs;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private NotificationRowBinding binding;


        ViewHolder(NotificationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(int position) {
            binding.expandable.setRecyclerItem((LinearLayoutManager) recyclerView.getLayoutManager(), getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            //onTitleClicked
        }

    }
}
