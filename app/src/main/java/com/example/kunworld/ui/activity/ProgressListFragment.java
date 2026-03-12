package com.example.kunworld.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.data.models.UserProgress;
import com.example.kunworld.data.repository.UserRepository;

import java.util.List;

public class ProgressListFragment extends Fragment implements ProgressAdapter.OnProgressClickListener {

    private static final String ARG_TAB_TYPE = "tab_type";
    public static final int TAB_IN_PROGRESS = 0;
    public static final int TAB_COMPLETED = 1;

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private ProgressAdapter adapter;
    private UserRepository userRepository;
    private int tabType;

    public static ProgressListFragment newInstance(int tabType) {
        ProgressListFragment fragment = new ProgressListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_TYPE, tabType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabType = getArguments().getInt(ARG_TAB_TYPE, TAB_IN_PROGRESS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyState = view.findViewById(R.id.emptyState);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = UserRepository.getInstance(requireContext());

        adapter = new ProgressAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        LiveData<List<UserProgress>> liveData;

        if (tabType == TAB_COMPLETED) {
            liveData = userRepository.getCompletedItems();
        } else {
            liveData = userRepository.getInProgressItems();
        }

        if (liveData != null) {
            liveData.observe(getViewLifecycleOwner(), progressList -> {
                if (progressList != null && !progressList.isEmpty()) {
                    adapter.submitList(progressList);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressClick(UserProgress progress) {
        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();

            if ("book".equals(progress.getContentType())) {
                args.putString("bookId", progress.getContentId());
                navController.navigate(R.id.bookReaderFragment, args);
            } else if ("course".equals(progress.getContentType())) {
                args.putString("courseId", progress.getContentId());
                navController.navigate(R.id.courseDetailFragment, args);
            }
        } catch (Exception e) {
            // Ignore navigation errors
        }
    }
}
