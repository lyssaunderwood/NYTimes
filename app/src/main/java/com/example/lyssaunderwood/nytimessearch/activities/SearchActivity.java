package com.example.lyssaunderwood.nytimessearch.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lyssaunderwood.nytimessearch.Article;
import com.example.lyssaunderwood.nytimessearch.ArticleAdapter;
import com.example.lyssaunderwood.nytimessearch.EndlessRecyclerViewScrollListener;
import com.example.lyssaunderwood.nytimessearch.Filters;
import com.example.lyssaunderwood.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    Filters filter;
    String spinnerVal;
    String date;
    //int flag;

    private final int REQUEST_CODE = 20;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    //EditText etQuery;
    //GridView gvResults;
    //Button btnSearch;
    String addQuery;

    ArrayList<Article> articles;
    ArticleAdapter adapter;

    StaggeredGridLayoutManager grid;
    //RecyclerView rvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupView();
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800080")));
        //flag = 0;
        filter = new Filters(false, false, false, null, null);

        topStories();
    }

    public void setupView() {
        //rvResults = (RecyclerView) findViewById(R.id.rvResults);
        //etQuery = (EditText) findViewById(R.id.etQuery);
        //gvResults = (GridView) findViewById(R.id.gvResults);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(articles);
        rvResults.setAdapter(adapter);
        grid = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(grid);
        //gvResults.setAdapter(adapter);

        // hook up listener for grid click
//        Results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // create an intent to display article
//                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
//                // get the article to display
//                Article article = articles.get(position);
//                // pass in the article into intent
//                i.putExtra("article", article);
//                // launch the activity
//                startActivity(i);
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                addQuery = query;
                onArticleSearch();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_filter) {
            //Toast.makeText(getApplicationContext(), "Text", Toast.LENGTH_SHORT).show();
            Intent j = new Intent(getApplicationContext(), FilterActivity.class);
            startActivityForResult(j, REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch() {
        rvResults.clearOnScrollListeners();
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });

        articles.clear();
        //String query = etQuery.getText().toString();
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "c1ac884016ce4f5a9df1ddc7fb9e63ec");
        params.put("page", 0);
        params.put("q", addQuery);
        if (filter.getDate() != null) {
            params.put("begin_date", date);
        }
        if (filter.getSpinnerVal() != null) {
            params.put("sort", spinnerVal);
        }
        Log.d("SEARCH ACTIVITY", url + "?" + params);
        //make an array list
        // if not 0 size then add it as a parameter
        ArrayList<String> newsDeskItems = new ArrayList<>();
        if (filter.isArts()) {
            newsDeskItems.add("\"Arts\"");
        }
        if (filter.isFashion()) {
            newsDeskItems.add("\"Fashion\"");
        }
        if (filter.isSports()) {
            newsDeskItems.add("\"Sports\"");
        }
        if (newsDeskItems.size() != 0) {
            String newsDeskItemsStr =
                    android.text.TextUtils.join(" ", newsDeskItems);
            String newsDeskParamValue =
                    String.format("news_desk:(%s)", newsDeskItemsStr);
            params.put("fq", newsDeskParamValue);
        }
        Log.d("SEARCH ACTIVITY", url + "?" + params);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        //String query = etQuery.getText().toString();
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "c1ac884016ce4f5a9df1ddc7fb9e63ec");
        params.put("page", offset);
        params.put("q", addQuery);
        if (filter.getDate() != null) {
            params.put("begin_date", date);
        }
        if (filter.getSpinnerVal() != null) {
            params.put("sort", spinnerVal);
        }
        Log.d("SEARCH ACTIVITY", url + "?" + params);
        //make an array list
        // if not 0 size then add it as a parameter
        ArrayList<String> newsDeskItems = new ArrayList<>();
        if (filter.isArts()) {
            newsDeskItems.add("\"Arts\"");
        }
        if (filter.isFashion()) {
            newsDeskItems.add("\"Fashion\"");
        }
        if (filter.isSports()) {
            newsDeskItems.add("\"Sports\"");
        }
        if (newsDeskItems.size() != 0) {
            String newsDeskItemsStr =
                    android.text.TextUtils.join(" ", newsDeskItems);
            String newsDeskParamValue =
                    String.format("news_desk:(%s)", newsDeskItemsStr);
            params.put("fq", newsDeskParamValue);
        }
        Log.d("SEARCH ACTIVITY", url + "?" + params);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            //Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
            //flag = 1;
            //date = data.getExtras().getString("date");
            //Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            //spinnerVal = data.getExtras().getString("spinner");
            //filter = (Filters) data.getSerializableExtra("vals");
            filter = (Filters) Parcels.unwrap(data.getParcelableExtra("vals"));
            date = filter.getDate();
            Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            spinnerVal = filter.getSpinnerVal();

            //articles.clear();
            //adapter.notifyDataSetChanged();
            //onArticleSearch();

        }
    }

    public void topStories() {
        rvResults.clearOnScrollListeners();
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });

        articles.clear();
        //String query = etQuery.getText().toString();
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "c1ac884016ce4f5a9df1ddc7fb9e63ec");
        params.put("page", 0);
        params.put("callback", "callbackTopStories");

        Log.d("SEARCH ACTIVITY", url + "?" + params);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

