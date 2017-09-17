package com.salemkb.paginator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.paginate.Paginate;
import com.paginate.recycler.RecyclerPaginate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

/**
 * Created by Salem on 8/14/2017.
 */

public abstract class Paginator {

    private Context context;
    private RecyclerView recyclerView;
    private List items;
    private String paramsString;
    private String baseUrl;
    private Class modelType;

    private RecyclerPaginate.Builder builder;
    private Paginate paginate;

    private boolean noMore = false;
    private boolean isLoading = false;
    private boolean hasParams = false;

    public Paginator(Context context,
                     RecyclerView recyclerView,
                     List items, String baseUrl,
                     @Nullable Map<String, Object> params,
                     Class modelType) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.items = items;
        this.baseUrl = baseUrl;
        this.modelType = modelType;
        this.paramsString = initParams(params);
        initPagenator();
    }

    protected abstract String getUrl(String baseUrl, List data);

    protected abstract JSONArray getArray(String response) throws JSONException;

    private String initParams(@Nullable Map<String, ?> params) {
        if (params == null)
            return "";
        String url = "";
        for (String key : params.keySet())
            url += "&" + key + "=" + params.get(key).toString();
        hasParams = (params.size() > 0);
        return url;
    }

    private void initPagenator() {
        paginate = null;
        builder = Paginate.with(recyclerView, callbacks)
                .addLoadingListItem(true)
                .setLoadingTriggerThreshold(2);
    }

    public void enablePagenate() {
        if (paginate != null)
            paginate.unbind();
        paginate = builder.build();
    }

    public void disablePagenate() {
        if (paginate != null)
            paginate.unbind();
    }

    private Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            isLoading = true;
            Volley.newRequestQueue(recyclerView.getContext())
                    .add(new StringRequest(
                                    getUrl(baseUrl, items) + paramsString,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            JSONArray array = new JSONArray();
                                            try {
                                                array = getArray(response);
                                                Gson gson = new Gson();
                                                for (int i = 0; i < array.length(); i++)
                                                    items.add(gson.fromJson(array.getJSONObject(i).toString(), modelType));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            isLoading = false;
                                            if (array.length() == 0) {
                                                paginate.setHasMoreDataToLoad(false);
                                                noMore = true;
                                            }
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                            isLoading = false;
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                            )
                    );

        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            return noMore;
        }
    };
}
