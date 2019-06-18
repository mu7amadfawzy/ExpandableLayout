package sample.com.expanded_layout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import sample.com.expanded_layout.notifications.NotificationsActivity;
import widget.com.expandablelayout.ExpandableLayout;
import widget.com.expandedcardview.R;

public class MainActivity extends AppCompatActivity {
    ExpandableLayout expandableLayout;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableLayout = findViewById(R.id.expandable);
        container = findViewById(R.id.container);
        setListener();
        addExpandable();
        addCustomExpandable();
    }

    private void addExpandable() {
        ExpandableLayout expandableLayout = new ExpandableLayout(this)
                .setHeaderTitle("Added By Java", Color.BLACK)
                .setDefaultContent("Content xxx xxxxxxxxx xxxxxxxx xxxxxx xxx", Color.BLUE)
                .setArrowDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_down));
        container.addView(expandableLayout);
    }

    private void addCustomExpandable() {
        ExpandableLayout expandableLayout = new ExpandableLayout(this)
                .setHeaderTitle(R.layout.layout_expandable_header)
                .setContentLayout(R.layout.layout_expandable_content)
                .setArrowDrawable(getDrawable(R.drawable.ic_arrow_downward));
        container.addView(expandableLayout);
    }

    private void setListener() {
        expandableLayout.setOnExpandedListener(new ExpandableLayout.OnExpandedListener() {
            @Override
            public void onExpandChanged(View v, boolean isExpanded) {
                Toast.makeText(MainActivity.this, "isExpanded== " + isExpanded, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onNotificationsClicked(View view) {
        NotificationsActivity.start(this);
    }
}
