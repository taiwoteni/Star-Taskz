package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchDestination extends AppCompatActivity {
    SearchView searchView;
    ListView searchList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        searchList = findViewById(R.id.searchList);
        //searchView = findViewById(R.id.search_view);

        arrayList = new ArrayList<>();
        arrayList.add("Food");
        arrayList.add("Clothes");
        arrayList.add("Cars");
        arrayList.add("Cups");
        arrayList.add("Ifechukwu");
        arrayList.add("ICN");
        arrayList.add("Inspire");
        arrayList.add("Chukwuoma");
        arrayList.add("Postpone");
        arrayList.add("My reminder");

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        searchList.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (arrayList.contains(query)){
                    arrayAdapter.getFilter().filter(query);
                }else {
                    Toast.makeText(SearchDestination.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}