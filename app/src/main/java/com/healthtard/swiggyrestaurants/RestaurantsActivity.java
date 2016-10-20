package com.healthtard.swiggyrestaurants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

public class RestaurantsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RestaurantsAdapter mAdapter;
    ArrayList<Restaurant> restaurantsList = new ArrayList<Restaurant>();
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new RestaurantsAdapter(restaurantsList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = true;
                            Log.v("...", "Last Item Wow !");
                            mSwipeRefreshLayout.setRefreshing(true);
                            startService(new Intent(RestaurantsActivity.this, FetchRestaurantsService.class));
                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(false);

        if (savedInstanceState == null) {
            mSwipeRefreshLayout.setRefreshing(true);
            startService(new Intent(this, FetchRestaurantsService.class));
        }

    }

    @Override
    public void onRefresh() {
        restaurantsList.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        startService(new Intent(this, FetchRestaurantsService.class));
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FetchRestaurantsService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                boolean isRefreshing = intent.getBooleanExtra(FetchRestaurantsService.EXTRA_REFRESHING, false);
                String jsonResponse = intent.getStringExtra(FetchRestaurantsService.RESPONSE);
                if (jsonResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray array = jsonObject.getJSONArray("restaurants");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Restaurant restaurant = new Restaurant();
                            restaurant.setName(object.getString("name"));
                            restaurant.setArea(object.getString("area"));
                            restaurant.setCity(object.getString("city"));
                            restaurant.setRating(object.getString("avg_rating"));
                            restaurant.setCid(object.getString("cid"));
                            restaurant.setCostfortwo(object.getString("costForTwo"));
                            restaurant.setDelivery_time(object.getString("deliveryTime"));
                            restaurant.setClosed(object.getBoolean("closed"));
                            if (object.has("chain")) {
                                JSONArray chain = object.getJSONArray("chain");
                                ArrayList<RestaurentChain> restaurentChainList = new ArrayList<RestaurentChain>();
                                RestaurentChain restaurentChain = new RestaurentChain();
                                for (int j = 0; j < chain.length(); j++) {
                                    JSONObject sub_object = array.getJSONObject(i);
                                    restaurentChain.setName(sub_object.getString("name"));
                                    restaurentChain.setArea(sub_object.getString("area"));
                                    restaurentChain.setCity(sub_object.getString("city"));
                                    restaurentChain.setRating(sub_object.getString("avg_rating"));
                                    restaurentChain.setCid(sub_object.getString("cid"));
                                    restaurentChain.setCostfortwo(sub_object.getString("costForTwo"));
                                    restaurentChain.setDelivery_time(sub_object.getString("deliveryTime"));
                                    restaurentChain.setClosed(sub_object.getBoolean("closed"));
                                    restaurentChainList.add(restaurentChain);
                                }
                                restaurant.setChain(restaurentChainList);
                            }
                            restaurantsList.add(restaurant);
                        }

                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(FetchRestaurantsService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }


    public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>{

        private List<Restaurant> restaurantList;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView restaurant_name, restaurant_area, restaurant_city, costfortwo, rating, delivery_time, restaurant_near_text;
            public ListView chainList;
            public ImageView restaurant_image;
            public LinearLayout restaurant_near;

            public ViewHolder(View view) {
                super(view);
                restaurant_name = (TextView) view.findViewById(R.id.restaurant_name);
                restaurant_area = (TextView) view.findViewById(R.id.restaurant_area);
                costfortwo = (TextView) view.findViewById(R.id.cost);
                rating = (TextView) view.findViewById(R.id.rating);
                delivery_time = (TextView) view.findViewById(R.id.delivery_time);
                restaurant_image = (ImageView) view.findViewById(R.id.restaurant_image);
                restaurant_near_text = (TextView) view.findViewById(R.id.restaurants_near);
                chainList = (ListView) view.findViewById(R.id.chain_restaurents);
                restaurant_near = (LinearLayout) view.findViewById(R.id.restaurants_near_layout);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v.findViewById(R.id.chain_restaurents).getVisibility() == View.VISIBLE)
                    v.findViewById(R.id.chain_restaurents).setVisibility(View.GONE);
                else
                    v.findViewById(R.id.chain_restaurents).setVisibility(View.VISIBLE);
            }
        }


        public RestaurantsAdapter(List<Restaurant> restaurantList) {
            this.restaurantList = restaurantList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restautants_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Restaurant restaurent = restaurantList.get(position);
            holder.restaurant_name.setText(restaurent.getName());
            holder.restaurant_area.setText(restaurent.getArea());
            holder.costfortwo.setText(restaurent.getCostfortwo());
            holder.rating.setText(restaurent.getRating());
            holder.delivery_time.setText(restaurent.getDelivery_time() + " hrs");
            //Setting image temp as original image URL not woeking
            String res_name = restaurent.getName();
            if (res_name.equalsIgnoreCase("bhukkad"))
                holder.restaurant_image.setImageResource(R.drawable.bhukkard);
            else if (res_name.equalsIgnoreCase("Madhurai Idly Shop"))
                holder.restaurant_image.setImageResource(R.drawable.madhurai_idli);
            else if (res_name.equalsIgnoreCase("Sree Krishna Kafe"))
                holder.restaurant_image.setImageResource(R.drawable.shree_krishna_cafe);
            else if (res_name.equalsIgnoreCase("Sagar Fast Food"))
                holder.restaurant_image.setImageResource(R.drawable.sagar_fast_foods);
            else if (res_name.equalsIgnoreCase("Lucknowiz"))
                holder.restaurant_image.setImageResource(R.drawable.lucknowiz);
            else if (res_name.equalsIgnoreCase("Chai Point"))
                holder.restaurant_image.setImageResource(R.drawable.chai_point);
            else if (res_name.equalsIgnoreCase("Upsouth"))
                holder.restaurant_image.setImageResource(R.drawable.upsouth);
            else if (res_name.equalsIgnoreCase("Muffets and Tuffets"))
                holder.restaurant_image.setImageResource(R.drawable.muffets_tuffets);
            else if (res_name.equalsIgnoreCase("Tadka Singh"))
                holder.restaurant_image.setImageResource(R.drawable.tadka_singh);
            else
                holder.restaurant_image.setImageResource(R.drawable.shree_krishna_cafe);
            if (restaurent.getChain() != null) {
                holder.restaurant_near.setVisibility(View.VISIBLE);
                holder.restaurant_near_text.setText(restaurent.getChain().size() + " Restaurant near you");
                holder.chainList.setAdapter(new RestaurantChainAdapter(RestaurantsActivity.this, restaurent.getChain()));
                setListViewHeightBasedOnItems(holder.chainList);
            }
        }

        @Override
        public int getItemCount() {
            if (restaurantList == null)
                return 0;
            return restaurantList.size();
        }
    }

    public class RestaurantChainAdapter extends BaseAdapter {
       ArrayList<RestaurentChain> restaurentChainsList;
        Context context;
        private LayoutInflater inflater = null;


        public RestaurantChainAdapter(Activity mainActivity, ArrayList<RestaurentChain> restaurentChainsList) {
            // TODO Auto-generated constructor stub
            this.restaurentChainsList = restaurentChainsList;
            context = mainActivity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return restaurentChainsList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView restaurent_name;
            TextView rating;
            TextView delivery_time;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.chain_listview_item, null);
            holder.restaurent_name = (TextView) rowView.findViewById(R.id.restaurant_name);
            holder.rating = (TextView) rowView.findViewById(R.id.rating);
            holder.delivery_time = (TextView) rowView.findViewById(R.id.delivery_time);
            holder.restaurent_name.setText(restaurentChainsList.get(position).getArea() + ", " + restaurentChainsList.get(position).getCity());
            holder.rating.setText(restaurentChainsList.get(position).getRating());
            holder.delivery_time.setText(restaurentChainsList.get(position).getDelivery_time() + " hrs");
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "You Clicked chained restaurant in area " + restaurentChainsList.get(position).getArea(), Toast.LENGTH_LONG).show();
                }
            });
            return rowView;
        }
    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            for(itemPos = 0; itemPos < numberOfItems; itemPos++){
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}