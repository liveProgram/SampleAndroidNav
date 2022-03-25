package com.live.programming.an21_functionalapp.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.live.programming.an21_functionalapp.LocalSession;
import com.live.programming.an21_functionalapp.MainActivity;
import com.live.programming.an21_functionalapp.R;
import com.live.programming.an21_functionalapp.databinding.FragmentSignOutBinding;

public class SignOutFragment extends DialogFragment {

    FragmentSignOutBinding binding;
    Button btnConfirm, btnCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignOutBinding.bind(inflater.inflate(R.layout.fragment_sign_out, container, false));
        btnCancel = binding.btnCancel;
        btnConfirm = binding.btnConfirm;

        btnConfirm.setOnClickListener(v -> {
            LocalSession localSession = new LocalSession(getContext());
            localSession.deleteAll();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logout successful", Toast.LENGTH_SHORT).show();
            // navigate to the login
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        });

        btnCancel.setOnClickListener(v -> getDialog().dismiss());

        return binding.getRoot();
    }
}