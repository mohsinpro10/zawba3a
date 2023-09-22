package com.gdevs.firetvappbygdevelopers.Fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.gdevs.firetvappbygdevelopers.Utils.Constant.BANNER_HOME;
import static com.gdevs.firetvappbygdevelopers.Utils.Constant.INTERSTITIAL_POST_LIST;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gdevs.firetvappbygdevelopers.Activity.DetailsActivity;
import com.gdevs.firetvappbygdevelopers.Adapter.ChannelAdapter;
import com.gdevs.firetvappbygdevelopers.Model.Channel;
import com.gdevs.firetvappbygdevelopers.R;
import com.gdevs.firetvappbygdevelopers.Utils.AdNetwork;
import com.gdevs.firetvappbygdevelopers.Utils.AdsPref;
import com.gdevs.firetvappbygdevelopers.Utils.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class LatestFragment extends Fragment {

    RecyclerView rvWallpaper;
    DatabaseReference wallpaperReference;
    List<Channel> wallpaperList;
    ChannelAdapter channelAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PrefManager prefManager;
    AdNetwork adNetwork;
    AdsPref adsPref;



    public LatestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        adsPref = new AdsPref(getActivity());
        adNetwork = new AdNetwork(getActivity());
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        prefManager = new PrefManager(getActivity());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        showRefresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(view);
            }
        });

        loadPictures(view);

        return view;
    }

    private void loadPictures(View view) {

        wallpaperList = new ArrayList<>();
        rvWallpaper = view.findViewById(R.id.recyclerView);
        rvWallpaper.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),prefManager.getInt("wallpaperColumns"));
        rvWallpaper.setLayoutManager(gridLayoutManager);

        channelAdapter = new ChannelAdapter(getContext(),wallpaperList);
        rvWallpaper.setAdapter(channelAdapter);
        wallpaperReference = FirebaseDatabase.getInstance("https://livetv-e6c09-default-rtdb.firebaseio.com").getReference("Channels");

        fetchWallpapers();

        channelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Channel obj, int position) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("name", obj.getChannelName());
                intent.putExtra("image", obj.getChannelImage());
                intent.putExtra("type", obj.getChannelType());
                intent.putExtra("link", obj.getChannelLink());
                intent.putExtra("desc", obj.getChannelDesc());
                intent.putExtra("category", obj.getChannelCategory());
                intent.putExtra("language", obj.getChannelLanguage());
                intent.putExtra("id", obj.getId());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                showInterstitialAd();
            }
        });

    }

    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    private void fetchWallpapers() {
        wallpaperReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showRefresh(false);
                if (dataSnapshot.exists()) {

                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        int id  = wallpaperSnapshot.child("id").getValue(int.class);
                        String wallpaper = wallpaperSnapshot.child("channelImage").getValue(String.class);
                        String name = wallpaperSnapshot.child("channelName").getValue(String.class);
                        String category = wallpaperSnapshot.child("channelCategory").getValue(String.class);
                        String type = wallpaperSnapshot.child("channelType").getValue(String.class);
                        String link = wallpaperSnapshot.child("channelLink").getValue(String.class);
                        String desc = wallpaperSnapshot.child("channelDesc").getValue(String.class);
                        String language = wallpaperSnapshot.child("channelLanguage").getValue(String.class);

                        Channel wallpaper1 = new Channel(id, wallpaper, name, category, type,link, desc,language);

                        wallpaperList.add(0,wallpaper1);
                    }
                    channelAdapter.notifyDataSetChanged();
                }else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(final View view) {

        wallpaperList.clear();

        channelAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPictures(view);
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