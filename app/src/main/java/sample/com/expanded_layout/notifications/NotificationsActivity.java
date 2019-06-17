package sample.com.expanded_layout.notifications;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import widget.com.expandedcardview.R;
import widget.com.expandedcardview.databinding.NotificationsActivityBinding;


/**
 * Created by fawzy on 7/23/18.
 */
public class NotificationsActivity extends AppCompatActivity {

    private NotificationsActivityBinding binding;
    private NotificationsAdapter adapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderView();
    }

    private void renderView() {
        binding = DataBindingUtil.setContentView(this, R.layout.notifications_activity);
        setActionBar(binding.toolbar);
        initiate();
    }

    private void initiate() {
        setRecycler();
    }

    private void setRecycler() {
        adapter = new NotificationsAdapter(new ArrayList<>());
        binding.recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.recycler.setAdapter(adapter);
    }

}