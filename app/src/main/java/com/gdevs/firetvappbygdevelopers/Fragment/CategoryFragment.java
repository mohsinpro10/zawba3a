package com.gdevs.firetvappbygdevelopers.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.gdevs.firetvappbygdevelopers.Utils.Constant.BANNER_HOME;
import static com.gdevs.firetvappbygdevelopers.Utils.Constant.INTERSTITIAL_POST_LIST;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gdevs.firetvappbygdevelopers.Activity.ChannelActivity;
import com.gdevs.firetvappbygdevelopers.Activity.DetailsActivity;
import com.gdevs.firetvappbygdevelopers.Adapter.CategoryAdapter;
import com.gdevs.firetvappbygdevelopers.Adapter.ChannelAdapter;
import com.gdevs.firetvappbygdevelopers.Config;
import com.gdevs.firetvappbygdevelopers.Model.Category;
import com.gdevs.firetvappbygdevelopers.Model.Channel;
import com.gdevs.firetvappbygdevelopers.R;
import com.gdevs.firetvappbygdevelopers.Utils.AdNetwork;
import com.gdevs.firetvappbygdevelopers.Utils.AdsPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    RecyclerView rvCategory;
    DatabaseReference categoryReference;
    List<Category> categoryList;
    CategoryAdapter categoriesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    AdNetwork adNetwork;
    AdsPref adsPref;

    public CategoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        adsPref = new AdsPref(getActivity());
        adNetwork = new AdNetwork(getActivity());
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        showRefresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(view);
            }
        });

        loadCategory(view);

        return view;
    }

    private void loadCategory(View view) {

        categoryList = new ArrayList<>();
        rvCategory = view.findViewById(R.id.recyclerView);
        rvCategory.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        rvCategory.setLayoutManager(gridLayoutManager);

        categoriesAdapter = new CategoryAdapter(getContext(),categoryList);
        rvCategory.setAdapter(categoriesAdapter);

        categoryReference = FirebaseDatabase.getInstance("https://livetv-e6c09-default-rtdb.firebaseio.com").getReference("categories");

        fetchCategory();

        categoriesAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Category obj, int position) {
                Intent intent = new Intent(getContext(), ChannelActivity.class);
                intent.putExtra(Config.DEVELOPER_NAME, obj.getName());
                startActivity(intent);
                showInterstitialAd();
            }
        });
    }

    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    private void fetchCategory() {
        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showRefresh(false);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String name = wallpaperSnapshot.getKey();
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String thumb = wallpaperSnapshot.child("thumbnail").getValue(String.class);

                        Category category = new Category(name, desc, thumb);

                        categoryList.add(0,category);
                    }
                    categoriesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(final View view) {

        //lyt_no_item.setVisibility(View.GONE);
        categoryList.clear();
        categoriesAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCategory(view);
            }
        }, 2000);
    }

    private void showRefresh(boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }
}