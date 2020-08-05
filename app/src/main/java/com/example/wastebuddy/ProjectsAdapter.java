package com.example.wastebuddy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.ItemProjectItemCardBinding;
import com.example.wastebuddy.models.Project;
import com.parse.ParseFile;

import java.util.List;

import com.example.wastebuddy.fragments.ProjectDetailsFragment;

import org.apache.commons.text.WordUtils;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder>{

    private Context mContext;
    private List<Project> mProjects;

    public ProjectsAdapter(Context context, List<Project> projects) {
        this.mContext = context;
        this.mProjects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProjectItemCardBinding projectBinding = ItemProjectItemCardBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(projectBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder holder, int position) {
        holder.binding.itemCardView.setAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
        Project project = mProjects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemProjectItemCardBinding binding;

        public ViewHolder(@NonNull ItemProjectItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setOnClickListener(binding);
        }

        private void setOnClickListener(@NonNull com.example.wastebuddy.databinding.ItemProjectItemCardBinding binding) {
            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                Project project = mProjects.get(position);

                if (position != RecyclerView.NO_POSITION) {
                    ProjectDetailsFragment fragment = new ProjectDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Project.KEY_OBJECT_ID, project.getObjectId());
                    fragment.setArguments(bundle);
                    switchContent(fragment);
                }
            });
        }

        public void bind(Project project) {
            TextView projectNameTextView = binding.projectNameTextView;
            ImageView projectImageView = binding.projectImageView;

            projectNameTextView.setText(WordUtils.capitalizeFully(project.getName()));

            ParseFile image = project.getImage();

            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(projectImageView);
            }
        }

        public void switchContent(Fragment fragment) {
            if (mContext == null)
                return;
            if (mContext instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.replaceFragment(fragment);
            }

        }
    }

}
