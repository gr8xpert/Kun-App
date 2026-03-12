package com.example.kunworld.ui.consultancy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.data.models.ConsultancyService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ConsultancyFragment extends Fragment {

    private RecyclerView rvServices;
    private MaterialButton btnBookAppointment;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultancy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Setup toolbar back button
        view.findViewById(R.id.toolbar).setOnClickListener(v -> navController.popBackStack());
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // Setup RecyclerView
        rvServices = view.findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvServices.setNestedScrollingEnabled(false);

        // Load services
        List<ConsultancyService> services = ConsultancyService.getAllServices();
        rvServices.setAdapter(new ServiceAdapter(services, this::onServiceClick));

        // Setup Book Appointment button
        btnBookAppointment = view.findViewById(R.id.btnBookAppointment);
        btnBookAppointment.setOnClickListener(v -> showBookingDialog());
    }

    private void onServiceClick(ConsultancyService service) {
        Bundle args = new Bundle();
        args.putString("serviceId", service.getId());
        navController.navigate(R.id.action_consultancy_to_detail, args);
    }

    private void showBookingDialog() {
        BookingDialogFragment dialog = new BookingDialogFragment();
        Bundle args = new Bundle();
        args.putString("serviceName", "General Consultation");
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "BookingDialog");
    }

    // RecyclerView Adapter
    private static class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
        private final List<ConsultancyService> services;
        private final OnServiceClickListener listener;

        interface OnServiceClickListener {
            void onClick(ConsultancyService service);
        }

        ServiceAdapter(List<ConsultancyService> services, OnServiceClickListener listener) {
            this.services = services;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_consultancy_service, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ConsultancyService service = services.get(position);

            holder.ivServiceImage.setImageResource(service.getImageRes());
            holder.tvServiceTitle.setText(service.getTitle());
            holder.tvTagline.setText(service.getTagline());

            // Set feature chips
            List<String> features = service.getFeatures();
            if (features.size() > 0) {
                holder.chipFeature1.setText(features.get(0));
                holder.chipFeature1.setVisibility(View.VISIBLE);
            }
            if (features.size() > 1) {
                holder.chipFeature2.setText(features.get(1));
                holder.chipFeature2.setVisibility(View.VISIBLE);
            }
            if (features.size() > 2) {
                holder.chipFeature3.setText("+" + (features.size() - 2) + " more");
                holder.chipFeature3.setVisibility(View.VISIBLE);
            }

            // Click listeners
            holder.cardService.setOnClickListener(v -> listener.onClick(service));
            holder.btnLearnMore.setOnClickListener(v -> listener.onClick(service));
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardService;
            ImageView ivServiceImage;
            TextView tvServiceTitle;
            TextView tvTagline;
            Chip chipFeature1, chipFeature2, chipFeature3;
            MaterialButton btnLearnMore;

            ViewHolder(View view) {
                super(view);
                cardService = view.findViewById(R.id.cardService);
                ivServiceImage = view.findViewById(R.id.ivServiceImage);
                tvServiceTitle = view.findViewById(R.id.tvServiceTitle);
                tvTagline = view.findViewById(R.id.tvTagline);
                chipFeature1 = view.findViewById(R.id.chipFeature1);
                chipFeature2 = view.findViewById(R.id.chipFeature2);
                chipFeature3 = view.findViewById(R.id.chipFeature3);
                btnLearnMore = view.findViewById(R.id.btnLearnMore);
            }
        }
    }
}
