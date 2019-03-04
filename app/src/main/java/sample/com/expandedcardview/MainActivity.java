package sample.com.expandedcardview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import widget.com.expandablelayout.ExpandableLayout;
import widget.com.expandedcardview.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ExpandableLayout) findViewById(R.id.expandable)).setOnExpandedListener(new ExpandableLayout.OnExpandedListener() {
            @Override
            public void onExpandChanged(View v, boolean isExpanded) {
                Toast.makeText(MainActivity.this, "isExpanded== " + isExpanded, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
