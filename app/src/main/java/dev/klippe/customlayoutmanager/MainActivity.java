package dev.klippe.customlayoutmanager;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        final RecyclerView.LayoutManager manager = new RecyclerViewLayoutManager();
        adapter = new RecyclerViewAdapter(this);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        RecyclerView.OnItemTouchListener listener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int x = (int) e.getX();
                int y = (int) e.getY();

                Rect rvBounds = new Rect();
                rv.getGlobalVisibleRect(rvBounds);
                x += rvBounds.left;
                y += rvBounds.top;

                Rect bounds = new Rect();
                View mapView = manager.findViewByPosition(0);
                mapView.getGlobalVisibleRect(bounds);

                return bounds.contains(x, y) && mapView.dispatchTouchEvent(e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                //View headerView = manager.findViewByPosition(0);
                //headerView.dispatchTouchEvent(e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        rv.addOnItemTouchListener(listener);
        adapter.getMapView().onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.getMapView().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.getMapView().onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.getMapView().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.getMapView().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.getMapView().onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        adapter.getMapView().onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.getMapView().onSaveInstanceState(outState);
    }
}
