package com.salemkb.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.salemkb.paginator.Paginator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list1)
    RecyclerView list1;
    @BindView(R.id.list2)
    RecyclerView list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initList1();
        initList2();


    }

    private void initList1() {
        List<PostModel> posts = new ArrayList<>();
        PostsAdapter postsAdapter = new PostsAdapter(this, posts);
        list1.setLayoutManager(new LinearLayoutManager(this));
        list1.setAdapter(postsAdapter);
        Paginator pagenator = new Paginator(this, list1, posts, "http://jsonplaceholder.typicode.com/posts",
                new HashMap<String, Object>(), PostModel.class) {
            @Override
            protected String getUrl(String baseUrl, List data) {
                baseUrl += "?_start=" + data.size() + "&_limit=5";
                return baseUrl;
            }

            @Override
            protected JSONArray getArray(String response) throws JSONException {
                return new JSONArray(response);
            }
        };
        pagenator.enablePagenate();
    }

    private void initList2() {
        List<UserModel> users = new ArrayList<>();
        UsersAdapter adapter2 = new UsersAdapter(this, users);
        list2.setLayoutManager(new LinearLayoutManager(this));
        list2.setAdapter(adapter2);
        Paginator pagenator = new Paginator(this, list2, users, "http://jsonplaceholder.typicode.com/users",
                new HashMap<String, Object>(), UserModel.class) {
            @Override
            protected String getUrl(String baseUrl, List data) {
                baseUrl += "?_start=" + data.size() + "&_limit=2";
                return baseUrl;
            }

            @Override
            protected JSONArray getArray(String response) throws JSONException {
                return new JSONArray(response);
            }
        };
        pagenator.enablePagenate();
    }
}


