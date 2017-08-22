package dev.klippe.customlayoutmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        RecyclerView.LayoutManager manager = new RecyclerViewLayoutManager();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
    }
}
